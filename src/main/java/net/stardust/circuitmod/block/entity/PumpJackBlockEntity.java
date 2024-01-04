package net.stardust.circuitmod.block.entity;

import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.EfficientCoalGeneratorBlock;
import net.stardust.circuitmod.block.custom.PumpJackBlock;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreenHandler;
import net.stardust.circuitmod.screen.PumpJackScreenHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

import java.util.Map;

public class PumpJackBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    /// NEW TEST ENERGY CODE

    private boolean isInOilChunk = false;

    private int max_oil = 64800;
    private int directEnergy = 0;
    public void addEnergy(int amount) {
        directEnergy += amount;
        // Ensure the energy does not exceed the capacity
        directEnergy = Math.min(directEnergy, MAX_DIRECT_ENERGY);
    }
    private void useEnergyForOil() {
        final int energyUsage = 10;
        final int oilProduction = 100; // Amount of oil produced per cycle

        if (directEnergy >= energyUsage) {
            int producibleOil = Math.min(oilProduction, max_oil - oilLevel); // Calculate the amount of oil that can be produced without exceeding the max
            if (producibleOil > 0) {
                // Increase oil level and decrease energy
                oilLevel += producibleOil;
                directEnergy -= energyUsage;
            }
        }
    }

    public PumpJackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUMP_JACK_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
    }

    private static final int MAX_DIRECT_ENERGY = 100000;

    private static final int OIL_CAPACITY = 64800; // Example capacity, adjust as needed
    private int oilLevel = 0; // Oil level in the tank

    private static final int INPUT_SLOT = 0;

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Pump Jack");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PumpJackScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    private boolean isRunning = false;

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;

    }

    private static final int POWERED_INDEX = 1;
    /////////////////////// PROPERTY DELEGATE ////////////////////////
    private static final int RUNNING_INDEX = 2; // New index for isRunning

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case POWERED_INDEX: return isPowered ? 1 : 0;
                case RUNNING_INDEX: return isRunning ? 1 : 0;
                case FLUID_LEVEL_INDEX: return oilLevel;
                default: return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case FLUID_LEVEL_INDEX: oilLevel = value; break;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public void setEnergy(int energy) {
        this.directEnergy = Math.min(energy, MAX_DIRECT_ENERGY);
        markDirty();
    }

    public void setOilLevel(int oil) {
        this.oilLevel = Math.min(oil, OIL_CAPACITY);
        markDirty();
    }


    ////////////////ALL ADDITIONAL ENERGY FUNCTION HERE //////////////////


    private static final int FLUID_LEVEL_INDEX = 3; // Assign an appropriate index

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        useEnergyForOil();
        for (PlayerEntity playerEntity : world.getPlayers()) {
            if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20 * 20) {
                ModMessages.sendPumpJackUpdate((ServerPlayerEntity) playerEntity, pos, directEnergy, oilLevel);
            }
        }
        System.out.println("The current directy energy is" + directEnergy);
        System.out.println("The current oil is" + oilLevel + " Out of a max of "+ max_oil);
    }
    ///////// ANIMATION DETAILS ////////////




    //////////////// NBT DATA /////////////
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OilLevel", oilLevel);
        Inventories.writeNbt(nbt, getItems());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, getItems());
        oilLevel = nbt.getInt("OilLevel");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));

    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (directEnergy > 0 && oilLevel < max_oil) {
            System.out.println("model running animation state being called");
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.running", Animation.LoopType.LOOP));
        }
        else {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
