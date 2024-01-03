package net.stardust.circuitmod.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PumpJackRenderer extends GeoBlockRenderer<PumpJackBlockEntity> {
    public PumpJackRenderer(BlockEntityRendererFactory.Context context) {
        super(new PumpJackModel());
    }
}
