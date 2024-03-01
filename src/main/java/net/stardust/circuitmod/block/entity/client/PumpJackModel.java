package net.stardust.circuitmod.block.entity.client;

import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class PumpJackModel extends GeoModel<PumpJackBlockEntity> {
    @Override
    public Identifier getModelResource(PumpJackBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "geo/pumpjack.geo.json");
    }

    @Override
    public Identifier getTextureResource(PumpJackBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/pumpjack.png");
    }

    @Override
    public Identifier getAnimationResource(PumpJackBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/pumpjack.animation.json");
    }
}
