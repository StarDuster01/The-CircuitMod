package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.stardust.circuitmod.networking.ModMessages;

public class PCBStationScreen extends HandledScreen<PCBStationScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/pcbstation_gui.png");


    public PCBStationScreen(PCBStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    public ButtonWidget button1;

    @Override
    protected void init() {
        super.init();
        button1 = ButtonWidget.builder(Text.literal("Craft"), button -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(handler.getBlockEntity().getPos());
            ClientPlayNetworking.send(ModMessages.PCB_CRAFT, buf);
        }).dimensions(width / 2 -85, height/2 -40, 40, 20).tooltip(Tooltip.of(Text.literal("Craft a Circuit"))).build(); //  You want it proportional to width and hiehgt because that changes based on screen size
        addDrawableChild(button1);

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
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

    }
}
