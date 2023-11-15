package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorRedstoneSlaveBlockEntity;
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {


    public static final BlockEntityType<ConductorBlockEntity> CONDUCTOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "conductor_block"),
                    FabricBlockEntityTypeBuilder.create(ConductorBlockEntity::new,
                            ModBlocks.CONDUCTOR_BLOCK).build(null));
    public static final BlockEntityType<EfficientCoalGeneratorBlockEntity> EFFICIENT_COAL_GENERATOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "efficient_coal_generator_block"),
                    FabricBlockEntityTypeBuilder.create(EfficientCoalGeneratorBlockEntity::new,
                            ModBlocks.EFFICIENT_COAL_GENERATOR_BLOCK).build(null));
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
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, MOVING_WALKWAY_BE);



        //EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, MEDIUM_COAL_GENERATOR_BE);


    }
}
