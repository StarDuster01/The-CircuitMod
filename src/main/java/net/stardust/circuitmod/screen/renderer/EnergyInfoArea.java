package net.stardust.circuitmod.screen.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense" (FORGE VERSION)
 *  Modified for Fabric by: Kaupenjoe
 */
public class EnergyInfoArea {
    private final Rect2i area;
    private final int fuelLevel;
    private final int maxFuelLevel;

    public EnergyInfoArea(int xMin, int yMin, int fuelLevel, int maxFuelLevel, int width, int height) {
        this.area = new Rect2i(xMin, yMin, width, height);
        this.fuelLevel = fuelLevel;
        this.maxFuelLevel = maxFuelLevel;
    }

    public List<Text> getTooltips() {
        return List.of(Text.literal(fuelLevel + " / " + maxFuelLevel + " Fuel"));
    }

    public void draw(DrawContext context) {
        final int height = area.getHeight();
        int filled = (int)(height * ((float)fuelLevel / maxFuelLevel));
        context.fillGradient(
                area.getX(), area.getY() + (height - filled),
                area.getX() + area.getWidth(), area.getY() + area.getHeight(),
                0xffb51500, 0xff600b00
        );
    }
}