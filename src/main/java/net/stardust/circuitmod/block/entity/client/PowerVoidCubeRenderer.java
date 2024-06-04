package net.stardust.circuitmod.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.stardust.circuitmod.block.entity.PowerVoidBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PowerVoidCubeRenderer extends GeoBlockRenderer<PowerVoidBlockEntity> {
    public PowerVoidCubeRenderer(BlockEntityRendererFactory.Context context) {
        super(new PowerVoidCubeModel());
    }
}
