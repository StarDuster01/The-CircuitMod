package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MovingWalkwayBlockEntity extends BlockEntity implements IEnergyConsumer {

    private long energyStored = 0; // The field to track energy
    private static final long MAX_ENERGY = 1000; // Assuming a max energy value
    private static final double WALKWAY_SPEED = 0.5; // Set the desired movement speed

    public boolean isPowered() {
        return this.energyStored > 0;
    }


    public MovingWalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOVING_WALKWAY_BE, pos, state);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }





    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            return;
        }
        extractEnergy();
        Direction facing = state.get(Properties.HORIZONTAL_FACING).getOpposite();
        Vec3d movementVector = new Vec3d(facing.getUnitVector()).multiply(WALKWAY_SPEED);
        Box areaAbove = new Box(pos).expand(0, 1, 0);
        List<PlayerEntity> playersOnBlock = world.getNonSpectatingEntities(PlayerEntity.class, areaAbove);
        for (PlayerEntity player : playersOnBlock) {
            if (isPowered()) {
                Vec3d currentVelocity = player.getVelocity();
                player.setVelocity(currentVelocity.add(movementVector));
                player.velocityModified = true;
            }
        }
    }
    @Override
    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY;
        }
    }
    private void extractEnergy() {
        this.energyStored = Math.max(this.energyStored - (long) 1, 0);
    }
}

