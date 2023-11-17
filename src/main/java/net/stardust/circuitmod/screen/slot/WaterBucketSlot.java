package net.stardust.circuitmod.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class WaterBucketSlot extends Slot {
    public WaterBucketSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(Items.WATER_BUCKET); // This line ensures that only coal can be inserted into this slot.
    }


}