package net.stardust.circuitmod.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.stardust.circuitmod.fluid.ModFluids;

public class LubeSlot extends Slot {
    public LubeSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(ModFluids.CRUDE_OIL_BUCKET);
    }


}