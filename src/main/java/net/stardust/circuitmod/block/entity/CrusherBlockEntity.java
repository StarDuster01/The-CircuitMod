package net.stardust.circuitmod.block.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
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
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.networking.ModMessages;
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

public class CrusherBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IEnergyConsumer, GeoBlockEntity {

    private static final int MAX_ENERGY = 100000;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private long energyStored = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int tickCounter = 0;
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
    }


    public void tick(World world, BlockPos pos, BlockState state) {
       // System.out.println("Crusher Energy: " + this.energyStored);
        //TODO Put Crusher Logic here
        if(!world.isClient) {
            for (PlayerEntity playerEntity : world.getPlayers()) {
                if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20*20) {
                    ModMessages.sendCrusherUpdate((ServerPlayerEntity) playerEntity, pos, energyStored, isCrushingActive);
                }
            } //TODO Make animation power dependant
            sendAnimationUpdate();

        }
    }
    boolean isCrushingActive = true;



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
    private void consumeEnergy(long amount) {
        this.energyStored = Math.max(this.energyStored - amount, 0);
    }

    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY; // Cap the energy at the maximum limit
        }
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }
    public long getEnergyStored() {
        return this.energyStored;
    }
    public void setEnergyStored(long energy) {
        this.energyStored = energy;
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
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
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crusher.run", Animation.LoopType.LOOP)); //TODO Change animations
        }
        else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crusher.run", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    boolean shouldMachineAnimateFast = true;
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
