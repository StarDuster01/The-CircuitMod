package net.stardust.circuitmod.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class PCBStationInventory implements Inventory {
    private final DefaultedList<ItemStack> items;

    public PCBStationInventory(DefaultedList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(items, slot, amount);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        this.markDirty();
    }

    // Implement the remaining methods as necessary...

    @Override
    public void markDirty() {
        // Implement this method to notify the system when the inventory changes.
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        // Implement this method based on your requirements.
        return true;
    }

    @Override
    public void clear() {

    }
}
