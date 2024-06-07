package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerResidueSlaveBlockEntity;

import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.refinery.PrimaryRefineryFluidInputSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FluidPipeBlockEntity extends BlockEntity implements ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int fluid_level = 0;
    private final int max_fluid_level = 64800;
    private Direction incomingDirection = null;
    private Direction outgoingDirection = null;
    private String currentFluidType;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PIPE_BE, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            transferFluid();
        }
    }

    public void transferFluid() {
        if (world == null || world.isClient() || fluid_level <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (direction == incomingDirection) {
                continue; // Skip the direction from which fluid was last received.
            }

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if (adjacentEntity instanceof OilTowerResidueSlaveBlockEntity) {
                OilTowerResidueSlaveBlockEntity slaveEntity = (OilTowerResidueSlaveBlockEntity) adjacentEntity;
                BlockPos masterPos = slaveEntity.getMasterPos();

                if (masterPos != null) {
                    BlockEntity masterEntity = world.getBlockEntity(masterPos);

                    if (masterEntity instanceof IFluidConsumer && ((IFluidConsumer) masterEntity).canReceiveFluid(currentFluidType)) {
                        IFluidConsumer masterConsumer = (IFluidConsumer) masterEntity;
                        int transferAmount = calculateFluidTransferToConsumer(masterConsumer);
                        transferFluidToConsumer(masterConsumer, transferAmount, direction);
                    }
                }
            } else if (adjacentEntity instanceof FuelGeneratorInventorySlaveBlockEntity) {
                FuelGeneratorInventorySlaveBlockEntity slaveEntity = (FuelGeneratorInventorySlaveBlockEntity) adjacentEntity;
                BlockPos masterPos = slaveEntity.getMasterPos();

                if (masterPos != null) {
                    BlockEntity masterEntity = world.getBlockEntity(masterPos);

                    if (masterEntity instanceof IFluidConsumer && ((IFluidConsumer) masterEntity).canReceiveFluid(currentFluidType)) {
                        IFluidConsumer masterConsumer = (IFluidConsumer) masterEntity;
                        int transferAmount = calculateFluidTransferToConsumer(masterConsumer);
                        transferFluidToConsumer(masterConsumer, transferAmount, direction);
                    }
                }
            } else if (adjacentEntity instanceof PrimaryRefineryFluidInputSlaveBlockEntity) {
                PrimaryRefineryFluidInputSlaveBlockEntity slaveEntity = (PrimaryRefineryFluidInputSlaveBlockEntity) adjacentEntity;
                BlockPos masterPos = slaveEntity.getMasterPos();

                if (masterPos != null) {
                    BlockEntity masterEntity = world.getBlockEntity(masterPos);

                    if (masterEntity instanceof IFluidConsumer && ((IFluidConsumer) masterEntity).canReceiveFluid(currentFluidType)) {
                        IFluidConsumer masterConsumer = (IFluidConsumer) masterEntity;
                        int transferAmount = calculateFluidTransferToConsumer(masterConsumer);
                        transferFluidToConsumer(masterConsumer, transferAmount, direction);
                    }
                }
            } else if (adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity adjacentPipe = (FluidPipeBlockEntity) adjacentEntity;
                if (adjacentPipe.canReceiveFluid(currentFluidType) && fluidTypeMatches(adjacentPipe)) {
                    int transferAmount = calculateFluidTransfer(adjacentPipe);
                    transferFluidToPipe(adjacentPipe, transferAmount, direction);
                }
            } else if (adjacentEntity instanceof IFluidConsumer && ((IFluidConsumer) adjacentEntity).canReceiveFluid(currentFluidType)) {
                int transferAmount = calculateFluidTransferToConsumer((IFluidConsumer) adjacentEntity);
                transferFluidToConsumer((IFluidConsumer) adjacentEntity, transferAmount, direction);
            }
        }
    }

    private boolean fluidTypeMatches(FluidPipeBlockEntity adjacentPipe) {
        return currentFluidType == null || adjacentPipe.getCurrentFluidType() == null ||
                currentFluidType.equals(adjacentPipe.getCurrentFluidType());
    }

    private int calculateFluidTransfer(FluidPipeBlockEntity pipe) {
        int transferAmount = Math.min(this.fluid_level, 100); // Assume 100 as the transfer rate
        return (int) Math.min(transferAmount, pipe.getMaxFluidLevel() - pipe.getFluidLevel());
    }

    private void transferFluidToPipe(FluidPipeBlockEntity pipe, int amount, Direction direction) {
        if (amount > 0) {
            this.fluid_level -= amount;
            pipe.increaseFluidLevel(amount, this.currentFluidType);
            pipe.setIncomingDirection(direction.getOpposite());
            this.outgoingDirection = direction;
        }
    }

    private int calculateFluidTransferToConsumer(IFluidConsumer consumer) {
        return Math.min(this.fluid_level, 100); // Assume 100 as the transfer rate to consumer
    }

    private void transferFluidToConsumer(IFluidConsumer consumer, int amount, Direction direction) {
        if (amount > 0) {
            this.fluid_level -= amount;
            consumer.addFluid(amount, this.currentFluidType);
            this.outgoingDirection = direction;
        }
    }

    public void setIncomingDirection(Direction direction) {
        this.incomingDirection = direction;
    }

    public boolean canReceiveFluid(String fluidType) {
        return this.fluid_level < max_fluid_level && (currentFluidType == null || currentFluidType.equals(fluidType));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("FluidLevel", fluid_level);
        if (currentFluidType != null) {
            nbt.putString("FluidType", currentFluidType);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        this.fluid_level = nbt.getInt("FluidLevel");
        if (nbt.contains("FluidType")) {
            this.currentFluidType = nbt.getString("FluidType");
        }
    }

    public void increaseFluidLevel(int fluidAmount, String incomingFluidType) {
        if (isPipeEmpty()) {
            this.currentFluidType = incomingFluidType;
        }
        this.fluid_level += fluidAmount;
        if (this.fluid_level > max_fluid_level) {
            this.fluid_level = max_fluid_level;
        }
    }

    public String getCurrentFluidType() {
        return this.currentFluidType;
    }

    public boolean isPipeEmpty() {
        return this.fluid_level <= 0;
    }

    public float getFluidLevel() {
        return this.fluid_level;
    }

    public int getMaxFluidLevel() {
        return max_fluid_level;
    }
}
