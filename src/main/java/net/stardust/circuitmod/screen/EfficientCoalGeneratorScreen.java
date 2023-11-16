package net.stardust.circuitmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;

public class EfficientCoalGeneratorScreen extends HandledScreen<EfficientCoalGeneratorScreenHandler> {

    private static final Identifier TEXTURE =
            new Identifier(CircuitMod.MOD_ID,"textures/gui/efficient_coal_generator_gui.png");


    public EfficientCoalGeneratorScreen(EfficientCoalGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

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
        // Get the current fuel level
        int fuelLevel = handler.getPropertyDelegate().get(0);
        // Assuming 100 is the max fuel level and the fuel indicator is 14 pixels in height
        int fuelIndicatorHeight = (int) (14 * (fuelLevel / 10.0));
        // Adjust the y-coordinate and height of the texture section
        int textureY = 28 + (14 - fuelIndicatorHeight); // Adjust the y-coordinate based on the fuel level
        context.drawTexture(TEXTURE, x+81, y + textureY, 176, 14 - fuelIndicatorHeight, 14, fuelIndicatorHeight); // FIRE COOKING TEXTURE

        boolean isPowered = handler.getPropertyDelegate().get(1) != 0;
        if (isPowered) {
            System.out.println("Attemtping to draw the Full Red texture");
            context.drawTexture(TEXTURE, x+129, y + 17, 176, 30, 14, 14); // FULL RED
        } else {
            System.out.println("Attemtping to draw the Faded Red texture");
            context.drawTexture(TEXTURE, x+129, y + 17, 176, 14, 14, 14); // FADED RED
        }

    }

}
