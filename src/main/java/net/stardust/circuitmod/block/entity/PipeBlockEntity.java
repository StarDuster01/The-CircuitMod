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

public class PipeBlockEntity extends BlockEntity implements ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean isPowered = false;

    private Queue<Direction> transferQueue = new LinkedList<>();

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

    private Direction nextTransferDirection = null;

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            updatePowerState(world, pos);

            tickCounter++;
            while (tickCounter >= TICK_DELAY && !transferQueue.isEmpty()) {
                Direction directionToTransfer = transferQueue.poll();
                if (isPowered) {
                    tryExtractItemsFromAdjacentChest(world, pos);
                }
                transferItemsToNextPipe(world, pos, this, directionToTransfer);
                tickCounter = 0; // Reset the counter after each transfer
            }
        }
        sendInventoryUpdate();
    }


    private void transferItemsToNextPipe(World world, BlockPos currentPos, PipeBlockEntity currentPipe, @Nullable Direction comingFrom) {
        if (currentPipe.inventory.get(0).isEmpty()) {
            return;
        }

        boolean transferred = false;
        for (Direction direction : Direction.values()) {
            if (comingFrom != null && direction == comingFrom.getOpposite()) {
                continue;
            }

            BlockPos nextPos = currentPos.offset(direction);
            BlockEntity entity = world.getBlockEntity(nextPos);
            if (entity instanceof PipeBlockEntity) {
                PipeBlockEntity nextPipe = (PipeBlockEntity) entity;
                if (nextPipe.inventory.get(0).isEmpty() || ItemStack.canCombine(nextPipe.inventory.get(0), currentPipe.inventory.get(0))) {
                    ItemStack remaining = nextPipe.insertItem(currentPipe.inventory.get(0).copy());
                    if (remaining.isEmpty()) {
                        currentPipe.inventory.set(0, ItemStack.EMPTY);
                        nextPipe.scheduleTransfer(direction);
                        transferred = true;
                        break;
                    }
                }
            }
        }

        // If the pipe is unpowered and the item wasn't transferred, try to insert into an adjacent inventory
        if (!transferred && !isPowered) {
            tryInsertIntoAdjacentInventory(world, currentPos, currentPipe);
        }
    }
    private void tryInsertIntoAdjacentInventory(World world, BlockPos pos, PipeBlockEntity pipe) {
        for (Direction direction : Direction.values()) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if (entity instanceof Inventory) {
                Inventory adjacentInventory = (Inventory) entity;

                // Check if the adjacent inventory is an EfficientCoalGeneratorInventorySlaveBlockEntity
                if (entity instanceof EfficientCoalGeneratorInventorySlaveBlockEntity) {
                    EfficientCoalGeneratorInventorySlaveBlockEntity coalGeneratorInventory =
                            (EfficientCoalGeneratorInventorySlaveBlockEntity) entity;

                    // Check if the item is allowed in the coal generator inventory
                    if (coalGeneratorInventory.isValid(0, pipe.inventory.get(0))) {
                        ItemStack remaining = transferItemToInventory(pipe.inventory.get(0), adjacentInventory);
                        if (remaining.isEmpty()) {
                            pipe.inventory.set(0, ItemStack.EMPTY);
                            break;
                        }
                    }
                } else {
                    // For other inventories, just transfer the item
                    ItemStack remaining = transferItemToInventory(pipe.inventory.get(0), adjacentInventory);
                    if (remaining.isEmpty()) {
                        pipe.inventory.set(0, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
    }

    private ItemStack transferItemToInventory(ItemStack stack, Inventory inventory) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Try to combine the stack with existing stacks in the inventory
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack slotStack = inventory.getStack(i);
            if (ItemStack.canCombine(stack, slotStack)) {
                int transferableAmount = Math.min(stack.getCount(), slotStack.getMaxCount() - slotStack.getCount());
                stack.decrement(transferableAmount);
                slotStack.increment(transferableAmount);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        // If there are remaining items in the stack, try to put them in an empty slot
        if (!stack.isEmpty()) {
            for (int i = 0; i < inventory.size(); ++i) {
                if (inventory.getStack(i).isEmpty()) {
                    inventory.setStack(i, stack.copy());
                    return ItemStack.EMPTY;
                }
            }
        }

        // Return the remaining stack if it couldn't be completely transferred
        return stack;
    }

    public void scheduleTransfer(Direction fromDirection) {
        // Add the direction to the transfer queue
        transferQueue.add(fromDirection);
        // Reset the tick counter for immediate processing
        this.tickCounter = TICK_DELAY; // processing in the next tick
    }


    private void updatePowerState(World world, BlockPos pos) {
        boolean currentPowerState = world.isReceivingRedstonePower(pos);
        setPowered(currentPowerState);
    }

    private void tryExtractItemsFromAdjacentChest(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            Inventory adjacentInventory = getAdjacentInventory(world, pos.offset(direction));
            if (adjacentInventory != null) {
                transferItemsFromChest(adjacentInventory);
                break;
            }
        }
    }

    private Inventory getAdjacentInventory(World world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof Inventory) {
            return (Inventory) entity;
        }
        return null;
    }

    private void transferItemsFromChest(Inventory chest) {
        for (int i = 0; i < chest.size(); i++) {
            ItemStack stackInChest = chest.getStack(i);
            if (!stackInChest.isEmpty()) {
                ItemStack remaining = insertItem(stackInChest);
                chest.setStack(i, remaining);
                if (remaining.isEmpty()) {
                    break; // Stop after transferring one non-empty stack
                }
            }
        }
    }

    public ItemStack insertItem(ItemStack stackFromChest) {
        if (this.inventory.get(0).isEmpty()) {
            this.inventory.set(0, stackFromChest.copy());
            stackFromChest.setCount(0);
            return ItemStack.EMPTY;
        } else if (ItemStack.canCombine(this.inventory.get(0), stackFromChest)) {
            int transferableAmount = Math.min(stackFromChest.getCount(),
                    this.inventory.get(0).getMaxCount() - this.inventory.get(0).getCount());
            this.inventory.get(0).increment(transferableAmount);
            stackFromChest.decrement(transferableAmount);
            return stackFromChest;
        } else {
            return stackFromChest;
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
