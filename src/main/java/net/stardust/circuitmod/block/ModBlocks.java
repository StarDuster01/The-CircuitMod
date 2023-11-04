package net.stardust.circuitmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.custom.ConductorBlock;
import net.stardust.circuitmod.block.custom.EfficientCoalGeneratorBlock;
import net.stardust.circuitmod.block.custom.QuarryBlock;
import net.stardust.circuitmod.block.custom.slave.EfficientCoalGeneratorEnergySlaveBlock;

public class ModBlocks {
    public static final Block CONDUCTOR_BLOCK = registerBlock("conductor_block",
            new ConductorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque())); // Need non Opaque to make not seethrough
    public static final Block EFFICIENT_COAL_GENERATOR_BLOCK = registerBlock("efficient_coal_generator_block",
            new EfficientCoalGeneratorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK = registerBlock("efficient_coal_generator_energy_slave_block",
            new EfficientCoalGeneratorEnergySlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block QUARRY_BLOCK = registerBlock("quarry_block",
            new QuarryBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));



    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks() {
        CircuitMod.LOGGER.info("Registering ModBlocks for " + CircuitMod.MOD_ID);
    }
}
