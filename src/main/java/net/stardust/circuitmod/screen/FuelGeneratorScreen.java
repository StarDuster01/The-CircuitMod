package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.renderer.FluidStackRenderer;
import net.stardust.circuitmod.util.MouseUtil;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class FuelGeneratorScreen extends HandledScreen<FuelGeneratorScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/fuel_generator_gui.png");


    public FuelGeneratorScreen(FuelGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private ButtonWidget convertToggleButton;

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
        assignFluidStackRenderer();
        assignLubeFluidStackRenderer();
        convertToggleButton = ButtonWidget.builder(Text.literal("Toggle"), button -> {
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeBlockPos(handler.getBlockEntity().getPos());
                    ClientPlayNetworking.send(ModMessages.TOGGLE_CONVERT_ID, passedData);
                })
                .dimensions(width / 2 - 0, height / 2 - 40, 40, 20)
                .tooltip(Tooltip.of(Text.literal("Toggle Fluid Conversion")))
                .build();

        // Add the button to the screen
        addDrawableChild(convertToggleButton);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if (isMouseAboveArea(mouseX, mouseY, x, y, 35, 17, 8, 53)) { // Adjust these values to match the fuel bar
            int fluidLevel = handler.getPropertyDelegate().get(3); // 3 for fuel level index
            Text fluidTooltip = Text.literal("Fuel Level: " + fluidLevel + " / " + 648000);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(fluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }

        // Lubricant Tooltip
        if (isMouseAboveArea(mouseX, mouseY, x, y, 45, 17, 4, 53)) { // Assuming you want a 4 pixel wide lubricant bar at (x+45, y+17)
            int lubricantLevel = handler.getPropertyDelegate().get(5); // 5 for lubricant level index
            Text lubricantTooltip = Text.literal("Lubricant Level: " + lubricantLevel + " / " + 648000);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(lubricantTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }


    }

    private FluidStackRenderer fluidStackRenderer;
    private FluidStackRenderer lubeStackRenderer;
    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(648000, true, 15, 53 );
    }
    private void assignLubeFluidStackRenderer() {
        lubeStackRenderer = new FluidStackRenderer(648000, true, 15, 53 );
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

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
        int lubricantLevel = handler.getPropertyDelegate().get(5); // Get lubricant level
        int fluidIndicatorHeight = (int) (53 * (fluidLevel / ((FluidConstants.BUCKET / 81) * 64.0)));
        int lubricantIndicatorHeight = (int) (53 * (lubricantLevel / ((FluidConstants.BUCKET / 81) * 64.0)));
        int fluidTextureY = y + 53 - fluidIndicatorHeight;
        int lubricantTextureY = y + 53 - lubricantIndicatorHeight;
        FuelGeneratorBlockEntity.FluidType fluidType = handler.getCurrentFluidType();
        FluidVariant fluidVariant = FluidVariant.of(Fluids.WATER); // default to water
        FluidVariant lubricantVariant = FluidVariant.of(Fluids.WATER);
        if (fluidType == FuelGeneratorBlockEntity.FluidType.CRUDE_OIL) {
            fluidVariant = FluidVariant.of(ModFluids.STILL_CRUDE_OIL);
        } else if (fluidType == FuelGeneratorBlockEntity.FluidType.WATER) {
            fluidVariant = FluidVariant.of(Fluids.WATER);
        } else if (fluidType == FuelGeneratorBlockEntity.FluidType.LAVA) {
            fluidVariant = FluidVariant.of(Fluids.LAVA);
        }
        fluidStackRenderer.drawFluid(context, fluidLevel, x + 35, y + 17, 8, 53, 648000, fluidVariant);
        lubeStackRenderer.drawFluid(context, lubricantLevel, x + 47, y +17, 2, 53, 648000, lubricantVariant);




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
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }


}
