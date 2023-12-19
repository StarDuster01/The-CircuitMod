package net.stardust.circuitmod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.explosives.LargeNukeEntity;
import net.stardust.circuitmod.block.entity.explosives.NukeEntity;

public class ModEntities {
    public static final EntityType<NukeEntity> NUKE_ENTITY;
    public static final EntityType<LargeNukeEntity> LARGE_NUKE_ENTITY;

    static {
        Identifier id = new Identifier(CircuitMod.MOD_ID, "nuke_entity");

        NUKE_ENTITY = Registry.register(
                Registries.ENTITY_TYPE,
                id,
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, NukeEntity::create)
                        .dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build() // Size of TNT, adjust if needed
        );
    }
    static {
        Identifier id = new Identifier(CircuitMod.MOD_ID, "large_nuke_entity");

        LARGE_NUKE_ENTITY = Registry.register(
                Registries.ENTITY_TYPE,
                id,
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, LargeNukeEntity::create)
                        .dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build() // Size of TNT, adjust if needed
        );
    }



}



