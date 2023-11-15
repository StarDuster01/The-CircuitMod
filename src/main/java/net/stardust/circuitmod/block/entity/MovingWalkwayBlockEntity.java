package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MovingWalkwayBlockEntity extends BlockEntity implements EnergyStorage {

    public class MovingWalkwayEnergyStorage extends SimpleEnergyStorage {
        public MovingWalkwayEnergyStorage(long capacity, long maxInsert, long maxExtract) {
            super(capacity, maxInsert, maxExtract);
        }

        public void setAmountDirectly(long newAmount) {
            this.amount = Math.min(newAmount, this.capacity);
        }
    }

    public boolean isPowered() {
        return this.energyStorage.getAmount() > 0;
    }






    public final MovingWalkwayEnergyStorage energyStorage =new MovingWalkwayEnergyStorage(300,300,300) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if(world != null)
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    };

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

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        long inserted = energyStorage.insert(maxAmount, transaction);
        System.out.println("Energy inserted: " + inserted);
        if (inserted > 0) {

            markDirty();
        }
        return inserted;
    }
    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long extracted = energyStorage.extract(maxAmount, transaction);
        if (extracted > 0) {

            markDirty();
        }
        return extracted;
    }
    @Override
    public long getAmount() {
        return energyStorage.amount;
    }
    @Override
    public long getCapacity() {
        return energyStorage.getCapacity();
    }
    private void extractEnergy(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(amount, transaction);
            markDirty();
            transaction.commit();
        }
    }
    private static final double WALKWAY_SPEED = 0.5; // Set the desired movement speed



    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            return;
        }
        extractEnergy(1);
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

}

