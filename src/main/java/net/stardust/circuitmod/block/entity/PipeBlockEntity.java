package net.stardust.circuitmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.minecraft.item.ItemStack.canCombine;

public class PipeBlockEntity extends BlockEntity implements ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean isPowered = false;
    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_BE, pos, state);
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

    public void setPowered(boolean powered) {
        if (isPowered != powered) {
            isPowered = powered;
            markDirty();
        }
    }
    private int tickCounter = 0;
    private final int TICK_DELAY = 20;
    private void updatePowerState(World world, BlockPos pos) {
        boolean currentPowerState = world.isReceivingRedstonePower(pos);
      //  System.out.println("Updating power state for PipeBlockEntity at " + pos + ": " + currentPowerState);

        setPowered(currentPowerState);
    }
    private void extractFromAdjacentInventory(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentBlockEntity = world.getBlockEntity(adjacentPos);
            if (adjacentBlockEntity instanceof Inventory) {
                Inventory adjacentInventory = (Inventory) adjacentBlockEntity;

                for (int i = 0; i < adjacentInventory.size(); i++) {
                    ItemStack stackInAdjacentInventory = adjacentInventory.getStack(i);
                    if (!stackInAdjacentInventory.isEmpty()) {
                        ItemStack transferredStack = adjacentInventory.removeStack(i, 1);
                        inventory.set(0, transferredStack);
                        break;
                    }
                }
            }
        }
    }
    private void sendInventoryUpdate() {
        if (this.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.world;
            serverWorld.getPlayers().forEach(player -> {
                ModMessages.sendPipeInventoryUpdate((ServerPlayerEntity) player, this.getPos(), this.getItems().get(0));
            });
        }
    }
    private void pushToAdjacentInventory(World world, BlockPos pos) {
        if (inventory.get(0).isEmpty()) {
            return;
        }

        ItemStack itemToTransfer = inventory.get(0);
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentBlockEntity = world.getBlockEntity(adjacentPos);

            if (adjacentBlockEntity instanceof Inventory && !(adjacentBlockEntity instanceof PipeBlockEntity)) {
                Inventory adjacentInventory = (Inventory) adjacentBlockEntity;

                // Loop over all slots in the adjacent inventory to find an empty slot or a stack that can be merged
                for (int i = 0; i < adjacentInventory.size(); i++) {
                    ItemStack stackInSlot = adjacentInventory.getStack(i);

                    if (stackInSlot.isEmpty()) {
                        adjacentInventory.setStack(i, itemToTransfer.copy()); // Place item in empty slot
                        inventory.set(0, ItemStack.EMPTY);
                        return;
                    } else if (canCombine(itemToTransfer, stackInSlot) && stackInSlot.getCount() < stackInSlot.getMaxCount()) {
                        int transferAmount = Math.min(itemToTransfer.getCount(), stackInSlot.getMaxCount() - stackInSlot.getCount());
                        stackInSlot.increment(transferAmount);
                        itemToTransfer.decrement(transferAmount);

                        if (itemToTransfer.isEmpty()) {
                            inventory.set(0, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        System.out.println("Tick called for PipeBlockEntity at " + pos);
        if (!world.isClient()) {
            updatePowerState(world, pos);
            if (tickCounter >= TICK_DELAY) {
                // Attempt to extract items from an adjacent inventory if powered and inventory is empty
                if (isPowered && inventory.get(0).isEmpty()) {
                    extractFromAdjacentInventory(world, pos);
                }
                if (!isPowered) {
                    pushToAdjacentInventory(world, pos);
                }
                if (isPowered || !inventory.get(0).isEmpty()) {
                    pushToNextPipe(world, pos);
                }
                tickCounter = 0;
            } else {
                tickCounter++;
            }
        }
        sendInventoryUpdate();
    }

    private Direction lastReceivedDirection = null;

    private void pushToNextPipe(World world, BlockPos pos) {
        if (inventory.get(0).isEmpty()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (direction == lastReceivedDirection) {
                continue;
            }

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentBlockEntity = world.getBlockEntity(adjacentPos);

            if (adjacentBlockEntity instanceof PipeBlockEntity) {
                PipeBlockEntity nextPipe = (PipeBlockEntity) adjacentBlockEntity;
                if (nextPipe.canReceiveItem()) {
                    nextPipe.receiveItem(inventory.get(0).copy(), direction.getOpposite()); // Use copy() to avoid direct reference issues
                    inventory.set(0, ItemStack.EMPTY); // Clear the item from the pipe's inventory
                    break;
                }
            }
        }
    }

    public boolean canReceiveItem() {
        return inventory.get(0).isEmpty();
    }
    public void receiveItem(ItemStack itemStack, Direction fromDirection) {
        if (canReceiveItem()) {
            inventory.set(0, itemStack);
            lastReceivedDirection = fromDirection; // Update the last received direction
        }
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
}
