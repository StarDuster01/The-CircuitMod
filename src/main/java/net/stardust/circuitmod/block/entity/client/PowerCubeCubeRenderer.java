package net.stardust.circuitmod.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.stardust.circuitmod.block.entity.PowerCubeBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PowerCubeCubeRenderer extends GeoBlockRenderer<PowerCubeBlockEntity> {
    public PowerCubeCubeRenderer(BlockEntityRendererFactory.Context context) {
        super(new PowerCubeCubeModel());
    }
}
