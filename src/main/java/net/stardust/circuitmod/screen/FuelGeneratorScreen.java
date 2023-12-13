package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.screen.renderer.FluidStackRenderer;
import net.stardust.circuitmod.util.MouseUtil;

import java.util.List;
import java.util.Optional;

public class FuelGeneratorScreen extends HandledScreen<FuelGeneratorScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/fuel_generator_gui.png");


    public FuelGeneratorScreen(FuelGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
        assignFluidStackRenderer();

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }

    private FluidStackRenderer fluidStackRenderer;
    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(648000, true, 15, 53 );
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        renderFluidTooltip(context, mouseX, mouseY, x, y, 26, 11, fluidStackRenderer);
    }
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);


        // Draw the fluid level bar
        int fluidLevel = handler.getPropertyDelegate().get(3); // Get fluid level from PropertyDelegate
        int fluidIndicatorHeight = (int) (53 * (fluidLevel / ((FluidConstants.BUCKET / 81) * 64.0)));
        int fluidTextureY = y + 53 - fluidIndicatorHeight;
        FuelGeneratorBlockEntity.FluidType fluidType = handler.getCurrentFluidType();
        FluidVariant fluidVariant = FluidVariant.of(Fluids.WATER); // default to water
        System.out.println("Drawing Background for Fluid Type: " + fluidType); // Debug statement
        if (fluidType == FuelGeneratorBlockEntity.FluidType.CRUDE_OIL) {
            fluidVariant = FluidVariant.of(ModFluids.STILL_CRUDE_OIL);
        } else if (fluidType == FuelGeneratorBlockEntity.FluidType.WATER) {
            fluidVariant = FluidVariant.of(Fluids.WATER);
        } else if (fluidType == FuelGeneratorBlockEntity.FluidType.LAVA) {
            fluidVariant = FluidVariant.of(Fluids.LAVA);
        }

        System.out.println("Drawing Background for Fluid Type: " + fluidType); // Debug statement


        fluidStackRenderer.drawFluid(context, fluidLevel, x + 35, y + 17, 8, 53, 648000, fluidVariant);



        // Draw the powered state indicator
        boolean isPowered = handler.getPropertyDelegate().get(1) != 0;
        if (isPowered) {
            context.drawTexture(TEXTURE, x + 129 +5, y + 17, 176, 30, 14, 14); // Full red texture
        } else {
            context.drawTexture(TEXTURE, x + 129 +5, y + 17, 176, 14, 14, 14); // Faded red texture
        }
        // Draw the running state indicator
        boolean isRunning = handler.getPropertyDelegate().get(2) != 0; // Index 2 for isRunning
        if (isRunning) {
            context.drawTexture(TEXTURE, x + 129 +5, y + 42, 176, 62, 14, 14); // Full green texture
        } else {
            context.drawTexture(TEXTURE, x + 129 + 5, y + 42, 176, 46, 14, 14); // Faded green texture
        }

    }




    private void renderFluidTooltip(DrawContext context, int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            int fluidLevel = handler.getPropertyDelegate().get(3); // Get fluid level from PropertyDelegate
            Text fluidTooltip = Text.literal("Fluid Level: " + fluidLevel + " / " + 648000);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(fluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }
    }




    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }


}
