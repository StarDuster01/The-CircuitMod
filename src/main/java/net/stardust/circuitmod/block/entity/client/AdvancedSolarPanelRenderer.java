package net.stardust.circuitmod.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.stardust.circuitmod.block.entity.AdvancedSolarPanelBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AdvancedSolarPanelRenderer extends GeoBlockRenderer<AdvancedSolarPanelBlockEntity> {
    public AdvancedSolarPanelRenderer(BlockEntityRendererFactory.Context context) {
        super(new AdvancedSolarPanelModel());
    }
}
