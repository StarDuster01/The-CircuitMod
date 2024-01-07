package net.stardust.circuitmod.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PunchRod extends Item {


    public PunchRod(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && user != null) {
            Vec3d lookVec = user.getRotationVec(1.0F);
            Vec3d reverseVec = lookVec.multiply(-1);

            // Calculate launch vector based on player's looking direction
            double launchPower = 20.0; // Adjust the power as necessary
            Vec3d launchVec = new Vec3d(reverseVec.x, reverseVec.y, reverseVec.z).normalize().multiply(launchPower);
            Vec3d currentVelocity = user.getVelocity();

            // Apply motion to the player
            user.setVelocity(currentVelocity.add(launchVec));
            System.out.println("Launching " + user + " with velocity " + launchPower + launchVec);

            user.velocityModified = true;

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}
