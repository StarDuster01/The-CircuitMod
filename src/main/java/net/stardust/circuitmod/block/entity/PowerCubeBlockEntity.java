package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class PowerCubeBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected static final RawAnimation DEPLOY = RawAnimation.begin();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);



    public PowerCubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWER_CUBE_CUBE_BE, pos, state);
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
        Box areaAbove = new Box(pos).expand(0, 1, 0);
        List<PlayerEntity> playersOnBlock = world.getNonSpectatingEntities(PlayerEntity.class, areaAbove);

    }
}
