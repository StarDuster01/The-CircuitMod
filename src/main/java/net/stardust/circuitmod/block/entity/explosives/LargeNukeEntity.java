package net.stardust.circuitmod.block.entity.explosives;


import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.entity.ModEntities;

public class LargeNukeEntity extends Entity implements Ownable {
    private static final int FUSE_DURATION = 60; // 3 seconds at 20 ticks per second
    private int fuseTimer = FUSE_DURATION;
    private int currentRadius = 1;
    private static final int MAX_RADIUS = 128;
    private boolean exploded = false;

    @org.jetbrains.annotations.Nullable
    private LivingEntity causingEntity;
    public LargeNukeEntity(EntityType<? extends LargeNukeEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    public LargeNukeEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(ModEntities.LARGE_NUKE_ENTITY, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.causingEntity = igniter;
    }
    public static LargeNukeEntity create(EntityType<LargeNukeEntity> type, World world) {
        return new LargeNukeEntity(world, 0, 0, 0, null);
    }

    @Override
    protected void initDataTracker() {

    }
    public int getFuseTimer() {
        return this.fuseTimer;
    }

    @Override
    public void tick() {
        // Stop movement if explosion has started
        if (fuseTimer <= 0) {
            this.setVelocity(Vec3d.ZERO); // Stop movement by setting velocity to zero
            if (!this.getWorld().isClient) {
                if (currentRadius <= MAX_RADIUS) {
                    this.explodeFromCenter(currentRadius);
                    currentRadius++;
                } else {
                    // Only remove the entity after the entire explosion has completed
                    this.exploded = true;
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        } else {
            // Existing movement logic
            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.98));
            if (this.isOnGround()) {
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
            }

            // Countdown logic
            fuseTimer--;
        }

        // Update water state and particle effects
        this.updateWaterState();
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        }
    }


    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    protected void explodeFromCenter(int currentRadius) {
        World world = this.getWorld();
        BlockPos explosionCenter = new BlockPos((int) this.getX(), (int) this.getBodyY(0.0625D), (int) this.getZ());

        int blocksRemoved = 0; // debug
        for (int x = -currentRadius; x <= currentRadius; x++) {
            for (int y = -currentRadius; y <= currentRadius; y++) {
                for (int z = -currentRadius; z <= currentRadius; z++) {
                    BlockPos currentPos = explosionCenter.add(x, y, z);
                    double distanceSquared = explosionCenter.getSquaredDistance(currentPos);

                    if (distanceSquared <= currentRadius * currentRadius) {
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 2 | 16);
                        blocksRemoved++;
                    }
                }
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Entity getOwner() {
        return null;
    }
}
