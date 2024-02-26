package net.stardust.circuitmod.block.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.networking.ModMessages;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class AdvancedSolarPanelBlockEntity extends BlockEntity implements GeoBlockEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public AdvancedSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_SOLAR_PANEL_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }



    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        // Put Energy Logic Here
    }
    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    boolean shouldPanelAnimate = false;
    public void setShouldPanelAnimate(boolean shouldAnimate) {
        this.shouldPanelAnimate = shouldAnimate;
        if (this.world != null && this.world.isClient) {
        }
    }
    private void sendAnimationUpdate() { //TODO Need to call this from somewhere
        if (this.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.world;
            serverWorld.getPlayers().stream()
                    .filter(player -> player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) < 64 * 64) // within 64 blocks
                    .forEach(player -> {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBlockPos(this.pos);
                        buf.writeBoolean(this.shouldPanelAnimate);
                        ServerPlayNetworking.send(player, ModMessages.PUMP_JACK_ANIMATION_UPDATE_ID, buf);
                    });
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));

    }
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

        if (shouldPanelAnimate) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.running", Animation.LoopType.LOOP)); //TODO Find animation names and plugin
        }
        else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));

        }
        return PlayState.CONTINUE;
    }

    public Direction getFacing() {
        return this.getCachedState().contains(Properties.HORIZONTAL_FACING) ? this.getCachedState().get(Properties.HORIZONTAL_FACING) : Direction.NORTH;
    }
}
