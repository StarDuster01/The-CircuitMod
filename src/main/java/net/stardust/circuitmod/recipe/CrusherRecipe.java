package net.stardust.circuitmod.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public class CrusherRecipe {
    private final ItemStack input;
    private final List<ItemStack> outputs;
    private final int craftTime;
    private final int energyConsumption;

    public CrusherRecipe(ItemStack input, List<ItemStack> outputs, int craftTime, int energyConsumption) {
        this.input = input;
        this.outputs = outputs;
        this.craftTime = craftTime;
        this.energyConsumption = energyConsumption;
    }
    public ItemStack getInput() {
        return input;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

}

