package net.stardust.circuitmod.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;


public class PCBStationRecipeSerializer implements RecipeSerializer<PCBStationRecipe> {
    private PCBStationRecipeSerializer() {
    }

    public static final PCBStationRecipeSerializer INSTANCE = new PCBStationRecipeSerializer();
    // This will be the "type" field in the json
    public static final Identifier ID = new Identifier("circuitmod:pcbstation_recipe");

    private static class PCBStationRecipeJsonFormat {
        JsonObject inputA;
        JsonObject inputB;
        String outputItem;
        int outputAmount;
    }

    @Override
    public PCBStationRecipe read(Identifier recipeId, JsonObject json) {
        PCBStationRecipeJsonFormat recipeJson = new Gson().fromJson(json, PCBStationRecipeJsonFormat.class);
        if (recipeJson.inputA == null || recipeJson.inputB == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;

        Ingredient inputA = Ingredient.fromJson(recipeJson.inputA);
        Ingredient inputB = Ingredient.fromJson(recipeJson.inputB);
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new PCBStationRecipe(inputA, inputB, output, recipeId);
    }


    @Override
    public void write(PacketByteBuf packetData, PCBStationRecipe recipe) {
        recipe.getInputA().write(packetData);
        recipe.getInputB().write(packetData);
        packetData.writeItemStack(recipe.getOutput()); // Use the static version for serialization
    }

    @Override
    public PCBStationRecipe read(Identifier recipeId, PacketByteBuf packetData) {
        Ingredient inputA = Ingredient.fromPacket(packetData);
        Ingredient inputB = Ingredient.fromPacket(packetData);
        ItemStack output = packetData.readItemStack();
        return new PCBStationRecipe(inputA, inputB, output, recipeId);
    }
}