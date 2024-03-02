package net.stardust.circuitmod.block.entity.client;

import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class CrusherModel extends GeoModel<CrusherBlockEntity> {
    @Override
    public Identifier getModelResource(CrusherBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "geo/crusher.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrusherBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/machines/crusher.png");
    }

    @Override
    public Identifier getAnimationResource(CrusherBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/crusher.animation.json");
    }
}
