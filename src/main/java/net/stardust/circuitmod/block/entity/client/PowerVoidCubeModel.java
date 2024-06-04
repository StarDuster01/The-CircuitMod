package net.stardust.circuitmod.block.entity.client;

import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.PowerVoidBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class PowerVoidCubeModel extends GeoModel<PowerVoidBlockEntity> {
    @Override
    public Identifier getModelResource(PowerVoidBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "geo/powercube.geo.json");
    }

    @Override
    public Identifier getTextureResource(PowerVoidBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/powercube.png");
    }

    @Override
    public Identifier getAnimationResource(PowerVoidBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/powercubes.animation.json");
    }
}
