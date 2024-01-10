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
import net.stardust.circuitmod.block.entity.QuantumTeleporterBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import org.joml.Vector2i;

public class QuantumTeleporterScreen extends HandledScreen<QuantumTeleporterScreenHandler> {



    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/quantum_teleporter_gui.png");

    public QuantumTeleporterScreen(QuantumTeleporterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public ButtonWidget button1;

    @Override
    protected void init() {
        super.init();
        titleY = 10;
        playerInventoryTitleY = 10;
        button1 = ButtonWidget.builder(Text.literal("TELEPORT"), button -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(handler.getBlockEntity().getPos());
            ClientPlayNetworking.send(ModMessages.TOGGLE_MINING_ID, buf);
        }).dimensions(width / 2 -100, 20, 200, 20).tooltip(Tooltip.of(Text.literal("Click this Button to Teleport"))).build();
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
     QuantumTeleporterBlockEntity blockEntity = this.handler.getBlockEntity();
     long energyAmount = (int) blockEntity.getEnergyStored();
     drawPowerInfo(context, blockEntity);
 }

    private void drawPowerInfo(DrawContext context, QuantumTeleporterBlockEntity blockEntity) {
        long energyAmount = (int) blockEntity.getEnergyStored();
        long energyPerBlock = (int) QuantumTeleporterBlockEntity.getEnergyPerBlock();
        System.out.println("Screen seen Energy Amount: " + energyAmount);
        Text powertext;
        int powercolor;

        if (energyAmount > 0) {
            powertext = Text.of("Stored: " + energyAmount + "J" + " Can teleport up to " + energyAmount / energyPerBlock + "Blocks");
            powercolor = 0x00FF00; // GREEN in RGB

        } else {
            powertext = Text.of("NO POWER");
            powercolor = 0xFF0000; // RED in RGB
        }

        int powerTextWidth = textRenderer.getWidth(powertext);
        int powerX = 139 - powerTextWidth / 2; // adjust as needed
        int powerY = -10; // adjust as needed
        context.drawCenteredTextWithShadow(textRenderer, powertext, powerX, powerY, powercolor);
    }


    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2; // Centering the background on the screen
        int y = (height - backgroundHeight) / 2; // Centering the background on the screen
        int backgroundWidth = 175;
        int backgroundHeight = 165;
        int u = 0;
        int v = 0;
        context.drawTexture(TEXTURE, x, y, u, v, backgroundWidth, backgroundHeight);
    }


}
