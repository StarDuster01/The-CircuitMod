package net.stardust.circuitmod.block.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;
import net.stardust.circuitmod.block.entity.PipeBlockEntity;
import net.stardust.circuitmod.block.entity.QuarryBlockEntity;

import java.util.logging.Logger;

public class PipeBlockEntityRenderer implements BlockEntityRenderer<PipeBlockEntity> {



    public PipeBlockEntityRenderer(Context context) {

    }



    @Override
    public void render(PipeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
       // System.out.println("Rendering PipeBlockEntity");

        DefaultedList<ItemStack> inventory = entity.getItems();
        ItemStack stack = inventory.get(0);

        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 4.0;
        matrices.translate(0.5, 1.25 + offset, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), null, 0);
        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GROUND,
                false,
                matrices,
                vertexConsumers,
                lightAbove,
                overlay,
                model
        );

        matrices.pop();
    }
}

