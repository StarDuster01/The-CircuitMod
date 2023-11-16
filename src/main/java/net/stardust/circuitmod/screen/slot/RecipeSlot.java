package net.stardust.circuitmod.screen.slot;

import net.minecraft.item.Item;

public class RecipeSlot {
    public final Item item;
    public final int count;
    public final boolean isOutput;

    public RecipeSlot(Item item, int count, boolean isOutput) {
        this.item = item;
        this.count = count;
        this.isOutput = isOutput;
    }
}

