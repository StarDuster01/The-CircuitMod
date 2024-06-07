package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.slave.refinery.RefineryEnergySlaveBlockEntity;
import net.stardust.circuitmod.screen.RefineryScreenHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class RefineryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY); // Input and output slots
    private int energy = 0;
    private int fluidLevel = 0;

    private static final int MAX_ENERGY = 100000;
    private static final int FLUID_CAPACITY = 100000;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_BE, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Refinery");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RefineryScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        // Retrieve the Energy Slave Block Entity
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        BlockPos energySlavePos = pos.offset(facing.rotateYClockwise(), 1); // Rotate 90 degrees from the facing direction
        BlockEntity slaveBlockEntity = world.getBlockEntity(energySlavePos);

        if (slaveBlockEntity instanceof RefineryEnergySlaveBlockEntity) {
            RefineryEnergySlaveBlockEntity slave = (RefineryEnergySlaveBlockEntity) slaveBlockEntity;
        }
        reduceEnergy(100);

    }



    public void reduceEnergy(int amount) {
        Direction facing = getCachedState().get(Properties.HORIZONTAL_FACING);
        BlockPos energySlavePos = getPos().offset(facing.rotateYClockwise(), 1); // Rotate 90 degrees from the facing direction
        BlockEntity slaveBlockEntity = world.getBlockEntity(energySlavePos);

        if (slaveBlockEntity instanceof RefineryEnergySlaveBlockEntity) {
            RefineryEnergySlaveBlockEntity slave = (RefineryEnergySlaveBlockEntity) slaveBlockEntity;
            slave.reduceEnergy(amount);
            this.energy = slave.getDirectEnergy();
            markDirty();
        }

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Energy", energy);
        nbt.putInt("FluidLevel", fluidLevel);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        energy = nbt.getInt("Energy");
        fluidLevel = nbt.getInt("FluidLevel");
        Inventories.readNbt(nbt, inventory);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    public int getOutputFluidLevel() {
        return 0; // TODO: Implement this method
    }

    public int getFluidLevel() {
        return 0; // TODO: Implement this method
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
