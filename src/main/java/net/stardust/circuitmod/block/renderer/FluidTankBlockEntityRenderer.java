package net.stardust.circuitmod.block.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.block.entity.FluidTankBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluidTankBlockEntityRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluidTankBlockEntityRenderer.class);

    public FluidTankBlockEntityRenderer(Context context) {
    }

    @Override
    public void render(FluidTankBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getCurrentFluidAmount() <= 0 || entity.getCurrentFluidType() == null) {
            return;
        }

        matrices.push();

        // Translate to the bottom of the block
        matrices.translate(0.0, 0.0, 0.0);

        // Render the fluid inside the tank
        float fluidHeight = (entity.getCurrentFluidAmount() / (float) entity.getMaxFluidAmount()) * 14.0F / 16.0F; // Adjust height based on fluid amount

        String fluidType = entity.getCurrentFluidType().toLowerCase();
        renderFluid(matrices, vertexConsumers, light, overlay, fluidHeight, fluidType);

        matrices.pop();
    }

    private void renderFluid(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float height, String fluidType) {
        String texturePath = "circuitmod:block/fluids/" + fluidType + "/" + fluidType + "_still";
        Identifier fluidTexture = new Identifier(texturePath);

        // Log the texture path and fluid type
        LOGGER.info("Rendering fluid type: {}, using texture: {}", fluidType, texturePath);

        SpriteAtlasTexture atlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        Sprite sprite = atlas.getSprite(fluidTexture);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

        // Calculate texture coordinates for the current frame
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        // Offset to make the fluid slightly smaller than the block
        float offset = 0.05F;

        // Render each face of the fluid box separately for troubleshooting

        // Bottom face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(0.0F, -1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(0.0F, -1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, 1 - offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(0.0F, -1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, 1 - offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(0.0F, -1.0F, 0.0F).next();

        // Top face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, 1 - offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, 1 - offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();

        // West face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, 1 - offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, 1 - offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();


        // East face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, 1 - offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, 1 - offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(1.0F, 0.0F, 0.0F).next();

        // North face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(0.0F, 0.0F, -1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(0.0F, 0.0F, -1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(0.0F, 0.0F, -1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(0.0F, 0.0F, -1.0F).next();

        // South face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, 0, 1 - offset).color(255, 255, 255, 255).texture(minU, minV).overlay(overlay).light(light).normal(0.0F, 0.0F, 1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, 0, 1 - offset).color(255, 255, 255, 255).texture(maxU, minV).overlay(overlay).light(light).normal(0.0F, 0.0F, 1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1 - offset, height, 1 - offset).color(255, 255, 255, 255).texture(maxU, maxV).overlay(overlay).light(light).normal(0.0F, 0.0F, 1.0F).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), offset, height, 1 - offset).color(255, 255, 255, 255).texture(minU, maxV).overlay(overlay).light(light).normal(0.0F, 0.0F, 1.0F).next();

        RenderSystem.disableBlend();
    }

    @Override
    public boolean rendersOutsideBoundingBox(FluidTankBlockEntity blockEntity) {
        return true;
    }
}
