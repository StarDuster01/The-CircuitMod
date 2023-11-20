package net.stardust.circuitmod.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class SolidFuelSlot extends Slot {
    public SolidFuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return isFuel(stack);
    }
    private boolean isFuel(ItemStack stack) {
        return stack.isOf(Items.COAL) ||
                stack.isOf(Items.COAL_BLOCK) ||
                stack.isOf(Items.OAK_PLANKS) ||
                stack.isOf(Items.SPRUCE_PLANKS) ||
                stack.isOf(Items.BIRCH_PLANKS) ||
                stack.isOf(Items.JUNGLE_PLANKS) ||
                stack.isOf(Items.ACACIA_PLANKS) ||
                stack.isOf(Items.DARK_OAK_PLANKS) ||
                stack.isOf(Items.CRIMSON_PLANKS) ||
                stack.isOf(Items.WARPED_PLANKS) ||
                stack.isOf(Items.OAK_LOG) ||
                stack.isOf(Items.SPRUCE_LOG) ||
                stack.isOf(Items.BIRCH_LOG) ||
                stack.isOf(Items.JUNGLE_LOG) ||
                stack.isOf(Items.ACACIA_LOG) ||
                stack.isOf(Items.DARK_OAK_LOG) ||
                stack.isOf(Items.CRIMSON_STEM) ||
                stack.isOf(Items.WARPED_STEM) ||
                stack.isOf(Items.BAMBOO) ||
                (Items.BAMBOO_BLOCK != null && stack.isOf(Items.BAMBOO_BLOCK));
    }


}