package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.screen.renderer.EnergyInfoArea;
import net.stardust.circuitmod.screen.renderer.FluidStackRenderer;
import net.stardust.circuitmod.util.MouseUtil;

import java.util.List;
import java.util.Optional;

public class EfficientCoalGeneratorScreen extends HandledScreen<EfficientCoalGeneratorScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/efficient_coal_generator_gui.png");


    public EfficientCoalGeneratorScreen(EfficientCoalGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
        assignEnergyInfoArea();
        assignFluidStackRenderer();

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }
    private EnergyInfoArea energyInfoArea;
    private FluidStackRenderer fluidStackRenderer;
    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(648000, true, 35, 53 );
    }

    private void assignEnergyInfoArea() {
        int efficiency = handler.getBlockEntity().getCurrentEfficiency(); // Get current efficiency

        // Calculate the x and y positions relative to the screen
        int xPosition = (width - backgroundWidth) / 2 + 155;
        int yPosition = (height - backgroundHeight) / 2 + 17;

        // Set the width and height based on the specified offsets
        int width = 164 - 155; // Width from 155 to 164
        int height = 70 - 17; // Height from 17 to 70

        energyInfoArea = new EnergyInfoArea(xPosition, yPosition, efficiency, 100, width, height);
    }





    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderEnergyAreaTooltips(context,mouseX, mouseY, x,y);
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



        final int MAX_FUEL_LEVEL = 10; // Replace with your actual maximum fuel level
        int fuelLevel = handler.getPropertyDelegate().get(0); // Get current fuel level from PropertyDelegate

        // Calculate the proportion of remaining fuel
        float fuelProportion = (float) fuelLevel / MAX_FUEL_LEVEL;

        // Calculate the visible height of the fire texture
        final int FIRE_TEXTURE_TOTAL_HEIGHT = 14; // Total height of the fire texture in the texture file
        int visibleFireHeight = (int) (FIRE_TEXTURE_TOTAL_HEIGHT * fuelProportion);

        // Calculate source and destination Y coordinates
        int sourceY = FIRE_TEXTURE_TOTAL_HEIGHT - visibleFireHeight;
        int destY = y + 28 + (FIRE_TEXTURE_TOTAL_HEIGHT - visibleFireHeight); // Adjust destination Y to move up as fuel decreases

        // Draw the fire texture proportionally
        context.drawTexture(TEXTURE, x + 81, destY, 176, sourceY, 14, visibleFireHeight);



        // Draw the efficiency bar
        int efficiency = handler.getBlockEntity().getCurrentEfficiency();
        int efficiencyTextureY = 28 + (14 - efficiency);

        // Draw the fluid level bar
        int fluidLevel = handler.getPropertyDelegate().get(3); // Get fluid level from PropertyDelegate
        int fluidIndicatorHeight = (int) (53 * (fluidLevel / ((FluidConstants.BUCKET / 81) * 64.0)));
        int fluidTextureY = y + 53 - fluidIndicatorHeight;
        FluidVariant waterVariant = FluidVariant.of(Fluids.WATER);
        fluidStackRenderer.drawFluid(context, fluidLevel, x + 34, y + 17, 35, 53, 648000, waterVariant);
        energyInfoArea.draw(context); // see if that works

        // Draw the powered state indicator
        boolean isPowered = handler.getPropertyDelegate().get(1) != 0;
        if (isPowered) {
            context.drawTexture(TEXTURE, x + 129, y + 17, 176, 30, 14, 14); // Full red texture
        } else {
            context.drawTexture(TEXTURE, x + 129, y + 17, 176, 14, 14, 14); // Faded red texture
        }
        // Draw the running state indicator
        boolean isRunning = handler.getPropertyDelegate().get(2) != 0; // Index 2 for isRunning
        if (isRunning) {
            context.drawTexture(TEXTURE, x + 129, y + 42, 176, 62, 14, 14); // Full green texture
        } else {
            context.drawTexture(TEXTURE, x + 129, y + 42, 176, 46, 14, 14); // Faded green texture
        }

    }




    private void renderFluidTooltip(DrawContext context, int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            int fluidLevel = handler.getPropertyDelegate().get(3); // Get fluid level from PropertyDelegate
            Text fluidTooltip = Text.literal("Fluid Level: " + fluidLevel + " / " + 648000);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(fluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }
    }


    private void renderEnergyAreaTooltips(DrawContext context, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, 156, 11, 8, 64)) {
            int efficiency = handler.getBlockEntity().getCurrentEfficiency();
            Text tooltip = Text.literal("Efficiency: " + efficiency + "%");
            context.drawTooltip(Screens.getTextRenderer(this), List.of(tooltip), Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }


}
