package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
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
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.screen.renderer.FluidStackRenderer;
import net.stardust.circuitmod.util.MouseUtil;
import java.util.List;
import java.util.Optional;

public class RefineryScreen extends HandledScreen<RefineryScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(CircuitMod.MOD_ID, "textures/gui/refinery_gui.png");

    public RefineryScreen(RefineryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private FluidStackRenderer fluidStackRenderer;
    private FluidStackRenderer outputFluidStackRenderer;

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
        assignFluidStackRenderer();
        assignOutputFluidStackRenderer();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if (isMouseAboveArea(mouseX, mouseY, x, y, 35, 17, 8, 53)) {
            int fluidLevel = handler.getFluidLevel();
            Text fluidTooltip = Text.literal("Fluid Level: " + fluidLevel + " / " + RefineryScreenHandler.FLUID_CAPACITY);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(fluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }

        if (isMouseAboveArea(mouseX, mouseY, x, y, 45, 17, 8, 53)) {
            int outputFluidLevel = handler.getOutputFluidLevel();
            Text outputFluidTooltip = Text.literal("Output Fluid Level: " + outputFluidLevel + " / " + RefineryScreenHandler.OUTPUT_FLUID_CAPACITY);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(outputFluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(RefineryScreenHandler.FLUID_CAPACITY, true, 15, 53);
    }

    private void assignOutputFluidStackRenderer() {
        outputFluidStackRenderer = new FluidStackRenderer(RefineryScreenHandler.OUTPUT_FLUID_CAPACITY, true, 15, 53);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        int fluidLevel = handler.getFluidLevel();
        int outputFluidLevel = handler.getOutputFluidLevel();

        fluidStackRenderer.drawFluid(context, fluidLevel, x + 35, y + 17, 8, 53, RefineryScreenHandler.FLUID_CAPACITY, FluidVariant.of(Fluids.WATER)); // Example fluid
        outputFluidStackRenderer.drawFluid(context, outputFluidLevel, x + 45, y + 17, 8, 53, RefineryScreenHandler.OUTPUT_FLUID_CAPACITY, FluidVariant.of(ModFluids.STILL_CRUDE_OIL)); // Example output fluid
    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }
}
