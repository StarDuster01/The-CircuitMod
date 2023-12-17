package net.stardust.circuitmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.custom.*;
import net.stardust.circuitmod.block.custom.explosives.NukeBlock;
import net.stardust.circuitmod.block.custom.slave.*;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.world.tree.RubberSaplingGenerator;

public class ModBlocks {
    public static final Block CONDUCTOR_BLOCK = registerBlock("conductor_block",
            new ConductorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque())); // Need non Opaque to make not seethrough
    public static final Block EFFICIENT_COAL_GENERATOR_BLOCK = registerBlock("efficient_coal_generator_block",
            new EfficientCoalGeneratorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block NUKE_BLOCK = registerBlock("nuke_block",
            new NukeBlock(FabricBlockSettings.copyOf(Blocks.TNT).sounds(BlockSoundGroup.STEM).nonOpaque()));

    public static final Block FUEL_GENERATOR_BLOCK = registerBlock("liquid_generator",
            new FuelGeneratorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block QUARRY_BLOCK = registerBlock("quarry_block",
            new QuarryBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block MOVING_WALKWAY_BLOCK = registerBlock("moving_walkway_block",
            new MovingWalkwayBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block PCBSTATION_BLOCK = registerBlock("pcbstation_block",
            new PCBStationBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block RUBBER_TAP_BLOCK = registerBlock("rubber_tap_block",
            new RubberTapBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CHUNK_LOADER_BLOCK = registerBlock("chunk_loader_block",
            new ChunkLoaderBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));

    /////////////// WOOD TYPES //////////////
    public static final Block RUBBER_LOG = registerBlock("rubber_log",
            new RubberLog(FabricBlockSettings.copyOf(Blocks.OAK_LOG).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_RUBBER_LOG = registerBlock("stripped_rubber_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).sounds(BlockSoundGroup.WOOD).strength(4)));
    public static final Block RUBBER_LEAVES = registerBlock("rubber_leaves",
            new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).sounds(BlockSoundGroup.WOOD).strength(1)));
    public static final Block RUBBER_SAPLING = registerBlock("rubber_tree_sapling",
            new SaplingBlock(new RubberSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING).sounds(BlockSoundGroup.CHERRY_SAPLING).strength(1).nonOpaque()));





    //////////////////////// SLAVE BLOCKS //////////////////

    public static final Block EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK = registerBlock("efficient_coal_generator_energy_slave_block",
            new EfficientCoalGeneratorEnergySlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block EFFICIENT_COAL_GENERATOR_INVENTORY_SLAVE_BLOCK = registerBlock("efficient_coal_generator_inventory_slave_block",
            new EfficientCoalGeneratorInventorySlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block EFFICIENT_COAL_GENERATOR_BASE_SLAVE_BLOCK = registerBlock("efficient_coal_generator_base_slave_block",
            new EfficientCoalGeneratorBaseSlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE)));
    public static final Block EFFICIENT_COAL_GENERATOR_REDSTONE_SLAVE_BLOCK = registerBlock("efficient_coal_generator_redstone_slave_block",
            new EfficientCoalGeneratorRedstoneSlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));



    public static final Block FUEL_GENERATOR_ENERGY_SLAVE_BLOCK = registerBlock("fuel_generator_energy_slave_block",
            new FuelGeneratorEnergySlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block FUEL_GENERATOR_INVENTORY_SLAVE_BLOCK = registerBlock("fuel_generator_inventory_slave_block",
            new FuelGeneratorInventorySlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block FUEL_GENERATOR_BASE_SLAVE_BLOCK = registerBlock("fuel_generator_base_slave_block",
            new FuelGeneratorBaseSlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE)));
    public static final Block FUEL_GENERATOR_REDSTONE_SLAVE_BLOCK = registerBlock("fuel_generator_redstone_slave_block",
            new FuelGeneratorRedstoneSlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));



    public static final Block PCBSTATION_BASE_SLAVE_BLOCK = registerBlock("pcbstation_base_slave_block",
            new PCBStationBaseSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


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
