package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.renderer.FluidStackRenderer;
import net.stardust.circuitmod.util.MouseUtil;

import java.util.List;
import java.util.Optional;

public class PumpJackScreen extends HandledScreen<PumpJackScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/pumpjack_gui.png");

    private FluidStackRenderer fluidStackRenderer;
    public PumpJackScreen(PumpJackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        assignFluidStackRenderer();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if (isMouseAboveArea(mouseX, mouseY, x, y, 8, 18, 24, 52)) { // Adjust these values to match the fuel bar
            int oilLevel = handler.getPropertyDelegate().get(3); // 3 for fuel level index
            Text fluidTooltip = Text.literal("Oil Level: " + oilLevel + " / " + 64800);
            context.drawTooltip(Screens.getTextRenderer(this), List.of(fluidTooltip), Optional.empty(), mouseX - x, mouseY - y);
        }
    }



    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

    }


    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);



        context.drawTexture(TEXTURE, 1760, 2039, 80, 52, 9, 9);

        boolean isRunning = handler.getPropertyDelegate().get(2) != 0;
        if (isRunning) {
            context.drawTexture(TEXTURE, x + 151, y + 42, 176, 30, 14, 14); // Full Green texture
        } else {
            context.drawTexture(TEXTURE, x + 151, y + 42, 176, 62, 14, 14); // Faded green texture
        }
        int oilLevel = handler.getPropertyDelegate().get(3);
        if (oilLevel < 64800) {
            context.drawTexture(TEXTURE,x + 151, y + 17, 176, 14, 14, 14 );
        }
        else{
            context.drawTexture(TEXTURE,x + 151, y + 17, 176, 30, 14, 14 );
        }
        fluidStackRenderer.drawFluid(context, oilLevel, x + 8, y + 17, 24, 52, 64800, FluidVariant.of(ModFluids.STILL_CRUDE_OIL));
    }



    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(64800, true, 25, 52 );
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
