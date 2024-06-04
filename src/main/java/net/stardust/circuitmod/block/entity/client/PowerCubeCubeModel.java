package net.stardust.circuitmod.block.entity.client;

import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.PowerCubeBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class PowerCubeCubeModel extends GeoModel<PowerCubeBlockEntity> {
    @Override
    public Identifier getModelResource(PowerCubeBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "geo/powercube.geo.json");
    }

    @Override
    public Identifier getTextureResource(PowerCubeBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/powercube2.png");
    }

    @Override
    public Identifier getAnimationResource(PowerCubeBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/powercubes.animation.json");
    }
}
