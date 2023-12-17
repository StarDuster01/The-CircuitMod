package net.stardust.circuitmod.block.entity.explosives;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;

public class NukeEntityRenderer extends EntityRenderer<NukeEntity> {
    private final BlockRenderManager blockRenderManager;
    private static final Identifier NUKE_TEXTURE = new Identifier(CircuitMod.MOD_ID, "textures/entity/nuke.png");

    public NukeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 1.5F; // You can adjust the shadow size if needed
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(NukeEntity nukeEntity) {
        return NUKE_TEXTURE;
    }

    @Override
    public void render(NukeEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int i) {
        matrixStack.push();
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        int j = entity.getFuseTimer();
        if ((float)j - tickDelta + 1.0F < 10.0F) {
            float h = 1.0F - ((float)j - tickDelta + 1.0F) / 10.0F;
            h = MathHelper.clamp(h, 0.0F, 1.0F);
            h *= h;
            h *= h;
            float k = 1.0F + h * 0.3F;
            matrixStack.scale(k, k, k);
        }

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        matrixStack.translate(-0.5F, -0.5F, 0.5F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
        TntMinecartEntityRenderer.renderFlashingBlock(this.blockRenderManager, ModBlocks.NUKE_BLOCK.getDefaultState(), matrixStack, vertexConsumers, i, j / 5 % 2 == 0);
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, i);
    }
}