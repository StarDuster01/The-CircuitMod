package net.stardust.circuitmod.block.entity.client;

import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.AdvancedSolarPanelBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class AdvancedSolarPanelModel extends GeoModel<AdvancedSolarPanelBlockEntity> {
    @Override
    public Identifier getModelResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "advanced_solar_panel_tracker.geo.json");
    }

    @Override
    public Identifier getTextureResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/advanced_solar_panel_tracker.png");
    }

    @Override
    public Identifier getAnimationResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/pumpjackrunning.animation.json");
    }
}
