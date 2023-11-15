package net.stardust.circuitmod.recipe;  // Changed 'recipie' to 'recipe' to match conventional spelling

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PCBStationRecipe implements Recipe<Inventory> {

    private final Ingredient inputA;
    private final Ingredient inputB;
    private final ItemStack outputStack;
    private final Identifier id;

    public PCBStationRecipe(Ingredient inputA, Ingredient inputB, ItemStack outputStack, Identifier id) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.outputStack = outputStack;
        this.id = id;
    }

    public Ingredient getInputA() {
        return inputA;
    }

    public Ingredient getInputB() {
        return inputB;
    }

    public static class Type implements RecipeType<PCBStationRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "pcb_station";
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return inputA.test(inventory.getStack(0)) && inputB.test(inventory.getStack(1));
    }


    @Override
    public boolean fits(int width, int height) {
        // Return true if the recipe can fit in the given width and height
        return width * height >= 2;
    }


    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PCBStationRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.outputStack.copy();
    }

    public ItemStack getOutput() {
        return this.outputStack.copy();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return getOutput(); // or use registryManager to get a dynamic output
    }



}
