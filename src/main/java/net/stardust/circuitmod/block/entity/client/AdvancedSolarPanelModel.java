package net.stardust.circuitmod.block.entity.client;

import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.AdvancedSolarPanelBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AdvancedSolarPanelModel extends GeoModel<AdvancedSolarPanelBlockEntity> {
    @Override
    public Identifier getModelResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "geo/advanced_solar_panel_tracker.geo.json");
    }

    @Override
    public Identifier getTextureResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID, "textures/block/advanced_solar_panel_tracker.png");
    }

    @Override
    public Identifier getAnimationResource(AdvancedSolarPanelBlockEntity animatable) {
        return new Identifier(CircuitMod.MOD_ID,"animations/pumpjackidle.animation.json"); //TODO This does not exist yet
    }

    @Override
    public void setCustomAnimations(AdvancedSolarPanelBlockEntity animatable, long instanceId, AnimationState<AdvancedSolarPanelBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(animatable.getWorld() != null) {
            Direction facing = animatable.getFacing();
            long timeOfDay = animatable.getWorld().getTimeOfDay() % 24000;
            float rotation = calculateTiltBasedOnTimeOfDay(timeOfDay);

            CoreGeoBone movingPart = getAnimationProcessor().getBone("movingpart");
            if(movingPart != null) {
                movingPart.setRotZ(rotation); // Adjust rotation axis as needed
            }
        }
    }
    // Minecraft time is the most frustrating thing ever, why is sunrise at 23000/24000 and not at 0????
    private float calculateTiltBasedOnTimeOfDay(long timeOfDay) {
        // Define start and end ticks for the rotation period
        final long startTick = 23816;
        final long endTick = 12186;
        final long fullCycle = 24000; // Full day-night cycle

        // Determine if the current time is during the night, outside the active rotation period
        boolean isNight = timeOfDay > endTick && timeOfDay < startTick;
        if (timeOfDay >= startTick || timeOfDay <= endTick) {
            isNight = false; // It's the active rotation period
        }

        if (isNight) {
            // During the night, the panel faces straight up (0°)
            return 0;
        }

        // Adjust the timeOfDay for the rotation calculation
        long adjustedTime = (timeOfDay + fullCycle - startTick) % fullCycle;
        // Calculate the total duration of the rotation cycle
        long rotationDuration = (endTick + fullCycle - startTick) % fullCycle;

        // Normalize the adjusted time to a [0, 1] range representing the rotation cycle
        float normalizedTime;
        if (adjustedTime <= rotationDuration) {
            normalizedTime = (float) adjustedTime / rotationDuration;
        } else {
            // If for any reason adjustedTime is beyond rotationDuration, ensure normalizedTime is within bounds
            normalizedTime = 1.0f;
        }

        // Calculate the rotation angle from 80° to -80° over the rotation period
        float rotationAngle = 160f * normalizedTime - 80f; // Linear interpolation from 80° to -80°

        // Return the rotation angle in radians
        return (float)Math.toRadians(rotationAngle);
    }


}
