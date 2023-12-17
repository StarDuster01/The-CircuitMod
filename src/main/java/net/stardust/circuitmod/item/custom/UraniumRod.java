package net.stardust.circuitmod.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class UraniumRod extends Item implements Equipment {


    public UraniumRod(Settings settings) {
        super(settings);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof UraniumRod) {
                applyNightVision(player);
            }
        }
    }

    private void applyNightVision(PlayerEntity player) {
        StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (currentEffect == null || currentEffect.getDuration() <= 1024) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, true, false, true));

        }
    }
}
