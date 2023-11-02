package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
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

    public static final BlockEntityType<QuarryBlockEntity> QUARRY_BLOCK_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(CircuitMod.MOD_ID, "quarry_block"),
                    FabricBlockEntityTypeBuilder.create(QuarryBlockEntity::new,
                            ModBlocks.QUARRY_BLOCK).build(null));

    public static void registerBlockEntities() {
        CircuitMod.LOGGER.info("Registering Block Entities for" + CircuitMod.MOD_ID);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, QUARRY_BLOCK_BE); // Allows the machine to accept energy from the sides


        //EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.energyStorage, MEDIUM_COAL_GENERATOR_BE);


    }
}
