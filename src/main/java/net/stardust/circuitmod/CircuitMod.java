package net.stardust.circuitmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.item.ModItemGroups;
import net.stardust.circuitmod.item.ModItems;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.recipe.PCBStationRecipe;
import net.stardust.circuitmod.recipe.PCBStationRecipeSerializer;
import net.stardust.circuitmod.screen.ModScreenHandlers;
import net.stardust.circuitmod.util.ModRegistries;
import net.stardust.circuitmod.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.stardust.circuitmod.block.ModBlocks;

public class CircuitMod implements ModInitializer {

    public static final String MOD_ID = "circuitmod";


    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {

        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModWorldGeneration.generateModWorldGeneration();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandler();

        ModRegistries.registerModStuffs();
      //  ModSounds.registerSounds();
        ModMessages.registerC2SPackets();
        // Register the Recipe Serializer
        Registry.register(Registries.RECIPE_SERIALIZER, PCBStationRecipeSerializer.ID,
                PCBStationRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("pcbstation", PCBStationRecipe.Type.ID), PCBStationRecipe.Type.INSTANCE);
    }
}
