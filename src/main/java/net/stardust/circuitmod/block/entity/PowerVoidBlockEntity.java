package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class PowerVoidBlockEntity extends BlockEntity implements GeoBlockEntity, IEnergyConsumer {
    protected static final RawAnimation DEPLOY = RawAnimation.begin();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private long energyGained = 0;
    private long energyStored = 0;
    private static final int MAX_ENERGY = 1000000000;
    public PowerVoidBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWER_VOID_CUBE_BE, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::predicate));
    }



    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.powercube.loop", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void tick(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
        this.energyGained = this.energyStored;
        this.energyStored = 0;
        System.out.println("Power voided at " + pos + " : " + energyGained + " For a total of: " + energyGained*20 + "/s");
    }

    @Override
    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY; // Cap the energy at the maximum limit
        }
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }
}
