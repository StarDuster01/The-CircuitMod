package net.stardust.circuitmod.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    //public static final FoodComponent GUM = new FoodComponent.Builder().alwaysEdible().build();
    public static final FoodComponent MINEBAR = new FoodComponent.Builder().hunger(8).saturationModifier(0.7F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 6000), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 6000), 1.0F)
            .build();
}
