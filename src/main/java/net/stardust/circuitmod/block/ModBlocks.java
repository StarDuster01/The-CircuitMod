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
import net.stardust.circuitmod.block.custom.explosives.LargeNukeBlock;
import net.stardust.circuitmod.block.custom.explosives.NukeBlock;
import net.stardust.circuitmod.block.custom.oiltower.OilTowerFuelBlock;
import net.stardust.circuitmod.block.custom.oiltower.OilTowerLubeBlock;
import net.stardust.circuitmod.block.custom.oiltower.OilTowerResidueBlock;
import net.stardust.circuitmod.block.custom.oiltower.OilTowerResidueSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.*;
import net.stardust.circuitmod.block.custom.slave.crusher.*;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.pumpjack.PumpJackBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.pumpjack.PumpJackEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.pumpjack.PumpJackExtraSlaveBlock;
import net.stardust.circuitmod.world.tree.RubberSaplingGenerator;

public class ModBlocks {
    /////////////// NETWORK BLOCKS //////////////
    public static final Block CONDUCTOR_BLOCK = registerBlock("conductor_block",
            new ConductorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque())); // Need non Opaque to make not seethrough
    public static final Block PIPE_BLOCK = registerBlock("pipe_block",
            new PipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block FLUID_PIPE_BLOCK = registerBlock("fluid_pipe_block",
            new FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE)));
    public static final Block INPUT_PIPE_BLOCK = registerBlock("input_pipe_block",
            new InputPipeBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// GENERATOR BLOCKS //////////////
    public static final Block EFFICIENT_COAL_GENERATOR_BLOCK = registerBlock("efficient_coal_generator_block",
            new EfficientCoalGeneratorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block FUEL_GENERATOR_BLOCK = registerBlock("liquid_generator",
            new FuelGeneratorBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block BASIC_SOLAR_PANEL_BLOCK = registerBlock("basic_solar_panel",
            new BasicSolarPanelBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block ADVANCED_SOLAR_PANEL_BLOCK = registerBlock("advanced_solar_panel",
            new AdvancedSolarPanelBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block ADVANCED_SOLAR_PANEL_BASE_BLOCK = registerBlock("advanced_solar_panel_base",
            new AdvancedSolarPanelBaseBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// OIL BLOCKS //////////////
    public static final Block PUMP_JACK_BLOCK = registerBlock("pump_jack_block",
            new PumpJackBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// QUARRY BLOCKS //////////////
    public static final Block QUARRY_BLOCK = registerBlock("quarry_block",
            new QuarryBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// NUKE BLOCKS //////////////
    public static final Block NUKE_BLOCK = registerBlock("nuke_block",
            new NukeBlock(FabricBlockSettings.copyOf(Blocks.TNT).sounds(BlockSoundGroup.STEM).nonOpaque()));
    public static final Block LARGE_NUKE_BLOCK = registerBlock("large_nuke_block",
            new LargeNukeBlock(FabricBlockSettings.copyOf(Blocks.TNT).sounds(BlockSoundGroup.STEM).nonOpaque()));


    /////////////// FUNCTIONAL BLOCKS //////////////
    public static final Block QUANTUM_TELEPORTER_BLOCK = registerBlock("quantum_teleporter_block",
            new QuantumTeleporterBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block MOVING_WALKWAY_BLOCK = registerBlock("moving_walkway_block",
            new MovingWalkwayBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CHUNK_LOADER_BLOCK = registerBlock("chunk_loader_block",
            new ChunkLoaderBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_BLOCK = registerBlock("crusher_block",
            new CrusherBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block GENERIC_MACHINE_FILLER_BLOCK = registerBlock("generic_machine_filler_block",
            new GenericMachineFillerBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// ORE BLOCKS //////////////
    public static final Block LEAD_ORE = registerBlock("lead_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_LEAD_ORE = registerBlock("deepslate_lead_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block ZIRCON_ORE = registerBlock("zircon_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_ZIRCON_ORE = registerBlock("deepslate_zircon_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block URANIUM_ORE = registerBlock("uranium_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_URANIUM_ORE = registerBlock("deepslate_uranium_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block BAUXITE_ORE = registerBlock("bauxite_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_BAUXITE_ORE = registerBlock("deepslate_bauxite_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));


    /////////////// DECOR BLOCKS //////////////



    /////////////// PRODUCTION BLOCKS //////////////
    public static final Block PCBSTATION_BLOCK = registerBlock("pcbstation_block",
            new PCBStationBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block RUBBER_TAP_BLOCK = registerBlock("rubber_tap_block",
            new RubberTapBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));

    //////////// OIL TOWER BLOCKS ///////////
    public static final Block OIL_TOWER_RESIDUE_BLOCK = registerBlock("oil_tower_residue_block",
            new OilTowerResidueBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block OIL_TOWER_RESIDUE_SLAVE_BLOCK = registerBlock("oil_tower_residue_slave_block",
            new OilTowerResidueSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block OIL_TOWER_LUBE_BLOCK = registerBlock("oil_tower_lube_block",
            new OilTowerLubeBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block OIL_TOWER_FUEL_BLOCK = registerBlock("oil_tower_fuel_block",
            new OilTowerFuelBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


    /////////////// RUBBER TREE BLOCKS //////////////
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


    public static final Block PUMP_JACK_ENERGY_SLAVE_BLOCK = registerBlock("pump_jack_energy_slave_block",
            new PumpJackEnergySlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block PUMP_JACK_BASE_SLAVE_BLOCK = registerBlock("pump_jack_base_slave_block",
            new PumpJackBaseSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block PUMP_JACK_EXTRA_SLAVE_BLOCK = registerBlock("pumpjackvalve",
            new PumpJackExtraSlaveBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.STONE).nonOpaque()));

    public static final Block CRUSHER_BASE_SLAVE_BLOCK = registerBlock("crusher_base_slave_block",
            new CrusherBaseSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_TOP_SLAVE_BLOCK = registerBlock("crusher_top_slave_block",
            new CrusherTopSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_ENERGY_SLAVE_BLOCK = registerBlock("crusher_energy_slave_block",
            new CrusherEnergySlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_REDSTONE_SLAVE_BLOCK = registerBlock("crusher_redstone_slave_block",
            new CrusherRedstoneSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_INVENTORY_SLAVE_BLOCK = registerBlock("crusher_inventory_slave_block",
            new CrusherInventorySlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block CRUSHER_INVENTORY_OUT_SLAVE_BLOCK = registerBlock("crusher_inventory_out_slave_block",
            new CrusherInventoryOutSlaveBlock(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.STONE).nonOpaque()));


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
