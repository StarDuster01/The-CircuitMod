package net.stardust.circuitmod.block.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.recipe.CrusherRecipe;
import net.stardust.circuitmod.screen.CrusherScreenHandler;
import net.stardust.circuitmod.screen.QuarryScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrusherBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {

    private static final int MAX_ENERGY = 100000;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private long energyStored = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(7, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int tickCounter = 0;
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_2 = 2;
    private static final int OUTPUT_SLOT_3 = 3;
    private static final int OUTPUT_SLOT_4 = 4;
    private static final int OUTPUT_SLOT_5 = 5;
    private static final int OUTPUT_SLOT_6 = 6;

    public ItemStack getInputItem() {
        return inventory.get(INPUT_SLOT);
    }
    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 0)
                    return (int) energyStored;
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0)
                    energyStored = value;

            }

            @Override
            public int size() {
                return 1;
            }
        };
        initRecipes();
    }


    public void tick(World world, BlockPos pos, BlockState state) {

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        BlockPos energySlavePos = pos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYCounterclockwise());
        BlockEntity slaveBlockEntity = world.getBlockEntity(energySlavePos);
        if (slaveBlockEntity instanceof CrusherEnergySlaveBlockEntity) {
            CrusherEnergySlaveBlockEntity slave = (CrusherEnergySlaveBlockEntity) slaveBlockEntity;
            energyStored = slave.getDirectEnergy();
            markDirty();
        }
        if (!world.isClient) {
            CrusherRecipe currentRecipe = findMatchingRecipe();
            CrusherEnergySlaveBlockEntity slave = (CrusherEnergySlaveBlockEntity) slaveBlockEntity;
            if (currentRecipe != null) {
                assert slave != null;
                if (slave.getDirectEnergy() >= currentRecipe.getEnergyConsumption() && canProcessRecipe(currentRecipe)) {
                    if (isPowered) {
                        return;
                    }
                    tickCounter++;
                    if (tickCounter >= currentRecipe.getCraftTime()) {
                        processRecipe(currentRecipe, slave);
                        tickCounter = 0; // Reset the counter
                    }
                }
            }


            for (PlayerEntity playerEntity : world.getPlayers()) {
                if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20*20) {
                    ModMessages.sendCrusherUpdate((ServerPlayerEntity) playerEntity, pos, energyStored, isCrushingActive);
                }
            }

            setshouldMachineAnimateFast(energyStored > 0);
            sendAnimationUpdate();

        }
    }
    boolean isCrushingActive = true;

    public void updatePoweredState(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            markDirty();
        }
    }



    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }


    @Override
    public Text getDisplayName() {
        return Text.literal("Crusher");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CrusherScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    //////////////////// ENERGY CODE //////////////////

    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY; // Cap the energy at the maximum limit
        }
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }

    /////////////// CRUSHING CODE ////////////////

    private List<CrusherRecipe> recipes = new ArrayList<>();

    private void initRecipes() {
        recipes.add(new CrusherRecipe(new ItemStack(Blocks.COBBLESTONE),
                Collections.singletonList(new ItemStack(Blocks.GRAVEL)),
                200, // Time in ticks
                1000)); // Energy consumption
    }
    private void processRecipe(CrusherRecipe recipe, CrusherEnergySlaveBlockEntity slave) {
        if (canProcessRecipe(recipe)) {
            slave.reduceEnergy(recipe.getEnergyConsumption());
            inventory.get(INPUT_SLOT).decrement(1); // Consume one item from the input

            // Add the output to the inventory
            addOutput(recipe.getOutputs().get(0)); // Assuming single output for simplicity

            // Play sound
            playProcessingSound();
        }
    }


    private CrusherRecipe findMatchingRecipe() {
        ItemStack inputItem = inventory.get(INPUT_SLOT);
        for(CrusherRecipe recipe : recipes) {
            if(ItemStack.areItemsEqual(inputItem, recipe.getInput()) && inputItem.getCount() >= recipe.getInput().getCount()) {
                return recipe;
            }
        }
        return null;
    }

    private boolean canProcessRecipe(CrusherRecipe recipe) {
        for (int i = OUTPUT_SLOT_1; i <= OUTPUT_SLOT_6; i++) {
            ItemStack outputStackInSlot = inventory.get(i);
            // Check if the slot is empty or contains the same item as the recipe output and is not full.
            if (outputStackInSlot.isEmpty() ||
                    (ItemStack.areItemsEqual(outputStackInSlot, recipe.getOutputs().get(0)) &&
                            outputStackInSlot.getCount() + recipe.getOutputs().get(0).getCount() <= outputStackInSlot.getMaxCount())) {
                return true;
            }
        }
        return false;
    }

    private void playProcessingSound() {
        if (!world.isClient) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1;
            double z = pos.getZ() + 0.5;
            world.playSound(null, x, y, z, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }
    private void playWorkingSound() {
        if (!world.isClient) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1;
            double z = pos.getZ() + 0.5;
            world.playSound(null, x, y, z, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }


    private void addOutput(ItemStack output) {
        for (int i = OUTPUT_SLOT_1; i <= OUTPUT_SLOT_6; i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                inventory.set(i, output.copy());
                return;
            } else if (ItemStack.areItemsEqual(stack, output) && stack.getCount() < stack.getMaxCount()) {
                int transferAmount = Math.min(output.getCount(), stack.getMaxCount() - stack.getCount());
                stack.increment(transferAmount);
                return;
            }
        }
    }


    /////// NBT ///////////
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("crusher.energy", this.energyStored);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        if(nbt.contains("crusher.energy")) {
            this.energyStored = nbt.getLong("crusher.energy");
        }
    }


    ////////// ANIMATIONS ///////////


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

        if (shouldMachineAnimateFast) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crusher.run", Animation.LoopType.LOOP));
        }
        else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crusher.slow", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    boolean shouldMachineAnimateFast = false;
    public void setshouldMachineAnimateFast(boolean shouldAnimate) {
        this.shouldMachineAnimateFast = shouldAnimate;
        if (this.world != null && this.world.isClient) {
        }
    }
    private void sendAnimationUpdate() {
        if (this.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.world;
            serverWorld.getPlayers().stream()
                    .filter(player -> player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) < 64 * 64) // within 64 blocks
                    .forEach(player -> {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBlockPos(this.pos);
                        buf.writeBoolean(this.shouldMachineAnimateFast);
                        ServerPlayNetworking.send(player, ModMessages.CRUSHER_FAST_ANIMATION_UPDATE_ID, buf);
                    });
        }
    }

}
