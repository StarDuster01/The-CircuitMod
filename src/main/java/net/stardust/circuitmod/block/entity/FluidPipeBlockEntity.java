package net.stardust.circuitmod.block.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.item.ItemStack.canCombine;

public class FluidPipeBlockEntity extends BlockEntity implements ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
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


    private int tickCounter = 0;
    private final int TICK_DELAY = 10;
    private int fluid_level = 0;
    private final int max_fluid_level = 64800;
    private void updatePowerState(World world, BlockPos pos) {
        boolean currentPowerState = world.isReceivingRedstonePower(pos);
    }

    private Direction incomingDirection = null;
    private Direction outgoingDirection = null;

    public void tick(World world, BlockPos pos, BlockState state) {
      //  System.out.println("Tick called for FluidPipeBlockEntity at " + pos);
        System.out.println("Fluid Level" + fluid_level + "Of Type" + getCurrentFluidType() + "at position" + pos);
        if (!world.isClient()) {
            transferFluid();
        }
        }




    private String currentFluidType;
    public void transferFluid() {
        if (world == null || world.isClient || fluid_level <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (direction == incomingDirection) {
                continue; // Skip the direction from which fluid was last received
            }

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if (adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity adjacentPipe = (FluidPipeBlockEntity) adjacentEntity;
                if (adjacentPipe.canReceiveFluid() && fluidTypeMatches(adjacentPipe)) {
                    int transferAmount = calculateFluidTransfer(adjacentPipe);
                    transferFluidToPipe(adjacentPipe, transferAmount, direction);
                }
            } else if (adjacentEntity instanceof IFluidConsumer && ((IFluidConsumer) adjacentEntity).canReceiveFluid()) {
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
        // Transfer a fixed amount or the available amount, whichever is lower
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
        // Transfer a fixed amount or the available amount, whichever is lower
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


    public boolean canReceiveFluid() {
        return this.fluid_level < max_fluid_level;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    public void increaseFluidLevel(int fluidAmount, String incomingFluidType) {
        if (isPipeEmpty()) {
            this.currentFluidType = incomingFluidType;
        }
        // Increase the fluid level
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
