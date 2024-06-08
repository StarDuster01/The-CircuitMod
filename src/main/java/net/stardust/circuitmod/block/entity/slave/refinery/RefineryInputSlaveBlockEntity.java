package net.stardust.circuitmod.block.entity.slave.refinery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.RefineryBlockEntity;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class RefineryInputSlaveBlockEntity extends BlockEntity implements SidedInventory {
    private BlockPos masterPos;

    public RefineryInputSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_INPUT_SLAVE_BE, pos, state);
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (masterPos != null) {
            nbt.putLong("MasterPos", masterPos.asLong());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("MasterPos")) {
            masterPos = BlockPos.fromLong(nbt.getLong("MasterPos"));
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0}; // Only one slot available
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true; // Allow insertion from any side
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false; // Do not allow extraction
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true; // Any item is valid
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemStack.EMPTY; // Always empty because items are forwarded immediately
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY; // Always empty because items are forwarded immediately
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY; // Always empty because items are forwarded immediately
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty() && masterPos != null) {
            World world = this.world;
            if (world != null) {
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                if (masterEntity instanceof RefineryBlockEntity) {
                    RefineryBlockEntity refineryEntity = (RefineryBlockEntity) masterEntity;
                    Inventory refineryInventory = refineryEntity;
                    ItemStack remainingStack = stack.copy();
                    for (int i = 0; i < refineryInventory.size(); i++) {
                        if (refineryEntity.canInsert(i, remainingStack, null)) {
                            ItemStack existingStack = refineryInventory.getStack(i);
                            if (existingStack.isEmpty()) {
                                refineryInventory.setStack(i, remainingStack);
                                remainingStack = ItemStack.EMPTY;
                                break;
                            } else if (ItemStack.canCombine(existingStack, remainingStack)) {
                                int combinedCount = existingStack.getCount() + remainingStack.getCount();
                                int maxCount = existingStack.getMaxCount();
                                if (combinedCount <= maxCount) {
                                    existingStack.setCount(combinedCount);
                                    remainingStack = ItemStack.EMPTY;
                                    break;
                                } else {
                                    remainingStack.setCount(combinedCount - maxCount);
                                    existingStack.setCount(maxCount);
                                }
                            }
                        }
                    }
                    if (!remainingStack.isEmpty()) {
                        // Drop remaining items in the world if they couldn't be forwarded
                        world.spawnEntity(new ItemEntity(world, this.pos.getX() + 0.5, this.pos.getY() + 1.0, this.pos.getZ() + 0.5, remainingStack));
                    }
                    this.markDirty();
                    refineryEntity.markDirty();
                }
            }
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world != null && this.world.getBlockEntity(this.pos) == this &&
                player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean isEmpty() {
        return true; // Always empty because items are forwarded immediately
    }

    @Override
    public int size() {
        return 1; // Only one slot
    }

    @Override
    public void clear() {
        // No operation needed since there's no inventory to clear
    }
}
