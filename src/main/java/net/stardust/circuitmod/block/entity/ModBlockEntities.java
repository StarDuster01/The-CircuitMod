package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.OilTowerFrame;
import net.stardust.circuitmod.block.entity.oiltower.*;
import net.stardust.circuitmod.block.entity.slave.*;
import net.stardust.circuitmod.block.entity.slave.crusher.*;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackExtraSlaveBlockEntity;

public class ModBlockEntities {


    public static final BlockEntityType<ConductorBlockEntity> CONDUCTOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "conductor_block"),
                    FabricBlockEntityTypeBuilder.create(ConductorBlockEntity::new,
                            ModBlocks.CONDUCTOR_BLOCK).build(null));

    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "basic_solar_panel_block"),
                    FabricBlockEntityTypeBuilder.create(BasicSolarPanelBlockEntity::new,
                            ModBlocks.BASIC_SOLAR_PANEL_BLOCK).build(null));
    public static final BlockEntityType<PipeBlockEntity> PIPE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pipe_block"),
                    FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new,
                            ModBlocks.PIPE_BLOCK).build(null));
    public static final BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fluid_pipe_block"),
                    FabricBlockEntityTypeBuilder.create(FluidPipeBlockEntity::new,
                            ModBlocks.FLUID_PIPE_BLOCK).build(null));
    public static final BlockEntityType<InputPipeBlockEntity> INPUT_PIPE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "input_pipe_block"),
                    FabricBlockEntityTypeBuilder.create(InputPipeBlockEntity::new,
                            ModBlocks.INPUT_PIPE_BLOCK).build(null));
    public static final BlockEntityType<EfficientCoalGeneratorBlockEntity> EFFICIENT_COAL_GENERATOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_BLOCK).build(null));
    public static final BlockEntityType<OilTowerFrameBlockEntity> OIL_TOWER_FRAME_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_frame"),
                    FabricBlockEntityTypeBuilder.create(OilTowerFrameBlockEntity::new,
                            ModBlocks.OIL_TOWER_FRAME).build(null));
    public static final BlockEntityType<OilTowerResidueBlockEntity> OIL_TOWER_RESIDUE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_residue_block"),
                    FabricBlockEntityTypeBuilder.create(OilTowerResidueBlockEntity::new,
                            ModBlocks.OIL_TOWER_RESIDUE_BLOCK).build(null));
    public static final BlockEntityType<OilTowerResidueSlaveBlockEntity> OIL_TOWER_RESIDUE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_residue_slave_block"),
                    FabricBlockEntityTypeBuilder.create(OilTowerResidueSlaveBlockEntity::new,
                            ModBlocks.OIL_TOWER_RESIDUE_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<OilTowerLubeBlockEntity> OIL_TOWER_LUBE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_lube_block"),
                    FabricBlockEntityTypeBuilder.create(OilTowerLubeBlockEntity::new,
                            ModBlocks.OIL_TOWER_LUBE_BLOCK).build(null));
    public static final BlockEntityType<OilTowerFuelBlockEntity> OIL_TOWER_FUEL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_fuel_block"),
                    FabricBlockEntityTypeBuilder.create(OilTowerFuelBlockEntity::new,
                            ModBlocks.OIL_TOWER_FUEL_BLOCK).build(null));
    public static final BlockEntityType<OilTowerNaphthaBlockEntity> OIL_TOWER_NAPHTHA_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_naphtha_block"),
                    FabricBlockEntityTypeBuilder.create(OilTowerNaphthaBlockEntity::new,
                            ModBlocks.OIL_TOWER_NAPHTHA_BLOCK).build(null));
    public static final BlockEntityType<OilTowerGasBlockEntity> OIL_TOWER_GAS_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "oil_tower_gasblock"),
                    FabricBlockEntityTypeBuilder.create(OilTowerGasBlockEntity::new,
                            ModBlocks.OIL_TOWER_GAS_BLOCK).build(null));
    public static final BlockEntityType<PumpJackBlockEntity> PUMP_JACK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pump_jack_block"),
                    FabricBlockEntityTypeBuilder.create(PumpJackBlockEntity::new,
                            ModBlocks.PUMP_JACK_BLOCK).build(null));
    public static final BlockEntityType<FuelGeneratorBlockEntity> FUEL_GENERATOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fuel_generator_block"),
                    FabricBlockEntityTypeBuilder.create(FuelGeneratorBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR_BLOCK).build(null));
    public static final BlockEntityType<PCBStationBaseSlaveBlockEntity> PCBSTATION_BASE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pcbstation_base_slave_block"),
            FabricBlockEntityTypeBuilder.create(PCBStationBaseSlaveBlockEntity::new,
                    ModBlocks.PCBSTATION_BASE_SLAVE_BLOCK).build(null)
    );
    public static final BlockEntityType<FuelGeneratorEnergySlaveBlockEntity> FUEL_GENERATOR_ENERGY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fuel_generator_energy_slave_block"),
                    FabricBlockEntityTypeBuilder.create(FuelGeneratorEnergySlaveBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR_ENERGY_SLAVE_BLOCK).build(null));

    public static final BlockEntityType<CrusherRedstoneSlaveBlockEntity> CRUSHER_REDSTONE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_redstone_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherRedstoneSlaveBlockEntity::new,
                            ModBlocks.CRUSHER_REDSTONE_SLAVE_BLOCK).build(null));

    public static final BlockEntityType<PumpJackEnergySlaveBlockEntity> PUMP_JACK_ENERGY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pump_jack_energy_slave_block"),
                    FabricBlockEntityTypeBuilder.create(PumpJackEnergySlaveBlockEntity::new,
                            ModBlocks.PUMP_JACK_ENERGY_SLAVE_BLOCK).build(null));

    public static final BlockEntityType<CrusherEnergySlaveBlockEntity> CRUSHER_ENERGY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_energy_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherEnergySlaveBlockEntity::new,
                            ModBlocks.CRUSHER_ENERGY_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "advanced_solar_panel_block"),
                    FabricBlockEntityTypeBuilder.create(AdvancedSolarPanelBlockEntity::new,
                            ModBlocks.ADVANCED_SOLAR_PANEL_BLOCK).build(null));
    public static final BlockEntityType<AdvancedSolarPanelBaseBlockEntity> ADVANCED_SOLAR_PANEL_BASE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "advanced_solar_panel_base"),
                    FabricBlockEntityTypeBuilder.create(AdvancedSolarPanelBaseBlockEntity::new,
                            ModBlocks.ADVANCED_SOLAR_PANEL_BASE_BLOCK).build(null));
    public static final BlockEntityType<PumpJackBaseSlaveBlockEntity> PUMP_JACK_BASE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pump_jack_base_slave_block"),
                    FabricBlockEntityTypeBuilder.create(PumpJackBaseSlaveBlockEntity::new,
                            ModBlocks.PUMP_JACK_BASE_SLAVE_BLOCK).build(null));

    public static final BlockEntityType<PumpJackExtraSlaveBlockEntity> PUMP_JACK_EXTRA_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pump_jack_extra_slave_block"),
                    FabricBlockEntityTypeBuilder.create(PumpJackExtraSlaveBlockEntity::new,
                            ModBlocks.PUMP_JACK_EXTRA_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<FuelGeneratorInventorySlaveBlockEntity> FUEL_GENERATOR_INVENTORY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fuel_generator_inventory_slave_block"),
                    FabricBlockEntityTypeBuilder.create(FuelGeneratorInventorySlaveBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR_INVENTORY_SLAVE_BLOCK).build(null));

    public static final BlockEntityType<CrusherInventorySlaveBlockEntity> CRUSHER_INVENTORY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_inventory_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherInventorySlaveBlockEntity::new,
                            ModBlocks.CRUSHER_INVENTORY_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<CrusherInventoryOutSlaveBlockEntity> CRUSHER_INVENTORY_OUT_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_inventory_out_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherInventoryOutSlaveBlockEntity::new,
                            ModBlocks.CRUSHER_INVENTORY_OUT_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<FuelGeneratorBaseSlaveBlockEntity> FUEL_GENERATOR_BASE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fuel_generator_base_slave_block"),
                    FabricBlockEntityTypeBuilder.create(FuelGeneratorBaseSlaveBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR_BASE_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<FuelGeneratorRedstoneSlaveBlockEntity> FUEL_GENERATOR_REDSTONE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fuel_generator_redstone_slave_block"),
                    FabricBlockEntityTypeBuilder.create(FuelGeneratorRedstoneSlaveBlockEntity::new,
                            ModBlocks.FUEL_GENERATOR_REDSTONE_SLAVE_BLOCK).build(null));


    public static final BlockEntityType<EfficientCoalGeneratorEnergySlaveBlockEntity> EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_energy_slave_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorEnergySlaveBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<EfficientCoalGeneratorInventorySlaveBlockEntity> EFFICIENT_COAL_GENERATOR_INVENTORY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_inventory_slave_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorInventorySlaveBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_INVENTORY_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<EfficientCoalGeneratorBaseSlaveBlockEntity> EFFICIENT_COAL_GENERATOR_BASE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_base_slave_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorBaseSlaveBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_BASE_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<EfficientCoalGeneratorRedstoneSlaveBlockEntity> EFFICIENT_COAL_GENERATOR_REDSTONE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_redstone_slave_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorRedstoneSlaveBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_REDSTONE_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<RubberTapBlockEntity> RUBBER_TAP_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "rubber_tap_block"),
                    FabricBlockEntityTypeBuilder.create(RubberTapBlockEntity::new,
                            ModBlocks.RUBBER_TAP_BLOCK).build(null));

    public static final BlockEntityType<GenericMachineFillerBlockEntity> GENERIC_MACHINE_FILLER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "generic_machine_filler_block"),
                    FabricBlockEntityTypeBuilder.create(GenericMachineFillerBlockEntity::new,
                            ModBlocks.GENERIC_MACHINE_FILLER_BLOCK).build(null));

    public static final BlockEntityType<QuarryBlockEntity> QUARRY_BLOCK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "quarry_block"),
                    FabricBlockEntityTypeBuilder.create(QuarryBlockEntity::new,
                            ModBlocks.QUARRY_BLOCK).build(null));
    public static final BlockEntityType<CrusherBlockEntity> CRUSHER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherBlockEntity::new,
                            ModBlocks.CRUSHER_BLOCK).build(null));
    public static final BlockEntityType<CrusherBaseSlaveBlockEntity> CRUSHER_BASE_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_base_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherBaseSlaveBlockEntity::new,
                            ModBlocks.CRUSHER_BASE_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<CrusherTopSlaveBlockEntity> CRUSHER_TOP_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "crusher_top_slave_block"),
                    FabricBlockEntityTypeBuilder.create(CrusherTopSlaveBlockEntity::new,
                            ModBlocks.CRUSHER_TOP_SLAVE_BLOCK).build(null));
    public static final BlockEntityType<QuantumTeleporterBlockEntity> QUANTUM_TELEPORTER_BLOCK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "quantum_teleporter_block"),
                    FabricBlockEntityTypeBuilder.create(QuantumTeleporterBlockEntity::new,
                            ModBlocks.QUANTUM_TELEPORTER_BLOCK).build(null));
    public static final BlockEntityType<PCBStationBlockEntity> PCBSTATION_BLOCK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pcbstation_block"),
                    FabricBlockEntityTypeBuilder.create(PCBStationBlockEntity::new,
                            ModBlocks.PCBSTATION_BLOCK).build(null));
    public static final BlockEntityType<MovingWalkwayBlockEntity> MOVING_WALKWAY_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "moving_walkway_block"),
                    FabricBlockEntityTypeBuilder.create(MovingWalkwayBlockEntity::new,
                            ModBlocks.MOVING_WALKWAY_BLOCK).build(null));
    public static final BlockEntityType<ChunkLoaderBlockEntity> CHUNK_LOADER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "chunk_loader_block"),
                    FabricBlockEntityTypeBuilder.create(ChunkLoaderBlockEntity::new,
                            ModBlocks.CHUNK_LOADER_BLOCK).build(null));

    public static final BlockEntityType<PowerVoidBlockEntity> POWER_VOID_CUBE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "power_void"),
                    FabricBlockEntityTypeBuilder.create(PowerVoidBlockEntity::new,
                            ModBlocks.POWER_VOID).build(null));
    public static final BlockEntityType<PowerCubeBlockEntity> POWER_CUBE_CUBE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "power_cube"),
                    FabricBlockEntityTypeBuilder.create(PowerCubeBlockEntity::new,
                            ModBlocks.POWER_CUBE).build(null));
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "fluidtank"),
                    FabricBlockEntityTypeBuilder.create(FluidTankBlockEntity::new,
                            ModBlocks.FLUID_TANK).build(null));

    public static void registerBlockEntities() {
        CircuitMod.LOGGER.info("Registering Block Entities for" + CircuitMod.MOD_ID);
    }
}
