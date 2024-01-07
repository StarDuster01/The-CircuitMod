package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.explosives.NukeEntity;
import net.stardust.circuitmod.block.entity.slave.*;
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
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {


    public static final BlockEntityType<ConductorBlockEntity> CONDUCTOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "conductor_block"),
                    FabricBlockEntityTypeBuilder.create(ConductorBlockEntity::new,
                            ModBlocks.CONDUCTOR_BLOCK).build(null));
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

    public static final BlockEntityType<PumpJackEnergySlaveBlockEntity> PUMP_JACK_ENERGY_SLAVE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "pump_jack_energy_slave_block"),
                    FabricBlockEntityTypeBuilder.create(PumpJackEnergySlaveBlockEntity::new,
                            ModBlocks.PUMP_JACK_ENERGY_SLAVE_BLOCK).build(null));
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

    public static final BlockEntityType<QuarryBlockEntity> QUARRY_BLOCK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "quarry_block"),
                    FabricBlockEntityTypeBuilder.create(QuarryBlockEntity::new,
                            ModBlocks.QUARRY_BLOCK).build(null));
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

    public static void registerBlockEntities() {
        CircuitMod.LOGGER.info("Registering Block Entities for" + CircuitMod.MOD_ID);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, QUARRY_BLOCK_BE); // Allows the machine to accept energy from the sides
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, QUANTUM_TELEPORTER_BLOCK_BE); // Allows the machine to accept energy from the sides
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, MOVING_WALKWAY_BE);
    }
}
