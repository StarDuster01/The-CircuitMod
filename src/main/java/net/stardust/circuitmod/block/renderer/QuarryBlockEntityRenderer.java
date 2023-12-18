package net.stardust.circuitmod.block.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.stardust.circuitmod.block.entity.QuarryBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;

public class QuarryBlockEntityRenderer implements BlockEntityRenderer<QuarryBlockEntity> {;

    private static ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE, 1);
    public QuarryBlockEntityRenderer(Context context) {

    }


    @Override
    public void render(QuarryBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        // Calculate the current offset in the y value
        double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 4.0;
        // Move the item
        matrices.translate(0.5, 1.25 + offset, 0.5);
        // Rotate the item
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), null, 0);

        // Determine the light level above the quarry block
        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        // Render the item with the appropriate transformation

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GROUND, // This might need to be changed depending on the visual you want
                false, // Adjust this if the item should be rendered as left-handed
                matrices,
                vertexConsumers,
                lightAbove, // Use the calculated light level
                overlay,
                model
        );




        // Mandatory call after GL calls
        matrices.pop();

    }
}
