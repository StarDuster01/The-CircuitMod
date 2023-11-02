package net.stardust.circuitmod;

import net.fabricmc.api.ModInitializer;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.item.ModItemGroups;
import net.stardust.circuitmod.item.ModItems;
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
      //  ModWorldGeneration.generateModWorldGeneration();
        ModBlockEntities.registerBlockEntities();
       // ModScreenHandlers.registerScreenHandler();
      //  ModRecipes.registerRecipes();
      //  ModRegistries.registerModStuffs();
      //  ModSounds.registerSounds();
      //  ModMessages.registerC2SPackets();
    }
}
