package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowerCubeBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected static final RawAnimation DEPLOY = RawAnimation.begin();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private long currentEnergy = 0;
    private long energyToGenerate = 1000000;

    private static final long MAX_ENERGY = 1000000000;
    private Set<BlockPos> visitedPositions = new HashSet<>();

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
        if (currentEnergy > 0) {
            visitedPositions.clear(); // Reset visited positions before search
            System.out.println("Condition energy > 0 met with : " + currentEnergy);
            // Find and distribute energy to IEnergyConsumer instances
            List<IEnergyConsumer> consumers = findEnergyConsumers(pos, null);
            distributeEnergy(consumers);
        }

        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }
    }

    private List<IEnergyConsumer> findEnergyConsumers(BlockPos currentPosition, @Nullable Direction fromDirection) {
        List<IEnergyConsumer> consumers = new ArrayList<>();

        if (!visitedPositions.add(currentPosition)) {
            return consumers; // Early return if already visited
        }

        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue;
            }

            BlockPos nextPos = currentPosition.offset(direction);

            if (!visitedPositions.contains(nextPos)) {
                BlockEntity blockEntity = world.getBlockEntity(nextPos);

                // Check if the block entity is an instance of IEnergyConsumer
                if (blockEntity instanceof IEnergyConsumer) {
                    consumers.add((IEnergyConsumer) blockEntity);
                } else if (blockEntity instanceof ConductorBlockEntity) {
                    // If it's a conductor, continue searching in the same direction
                    consumers.addAll(findEnergyConsumers(nextPos, direction));
                }
            }
        }
        return consumers;
    }
    private void distributeEnergy(List<IEnergyConsumer> consumers) {
        System.out.println("Distribute energy method called:: " + consumers.size());

        for (IEnergyConsumer consumer : consumers) {
            if (currentEnergy > 0) {
                int energyToGive = (int) Math.min(100000, currentEnergy);
                consumer.addEnergy(energyToGive);
                currentEnergy -= energyToGive;
                System.out.println("Energy transferred: " + energyToGive);
            }
        }
    }
}
