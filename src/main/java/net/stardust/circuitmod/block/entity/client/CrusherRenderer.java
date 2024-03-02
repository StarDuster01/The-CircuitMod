package net.stardust.circuitmod.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrusherRenderer extends GeoBlockRenderer<CrusherBlockEntity> {
    public CrusherRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrusherModel());
    }
}
