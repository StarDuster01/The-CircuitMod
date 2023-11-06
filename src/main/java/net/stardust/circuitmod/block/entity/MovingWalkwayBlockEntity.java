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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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



    private static final UUID MOVING_WALKWAY_SPEED_BOOST_ID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private Set<UUID> playersOnWalkway = new HashSet<>();
    private static final double SPEED_BOOST_AMOUNT = 5.0; // Change this to set the desired speed boost

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            extractEnergy(1);
            Box areaAbove = new Box(pos).expand(0, 1, 0);
            List<PlayerEntity> playersCurrentlyOnBlock = world.getNonSpectatingEntities(PlayerEntity.class, areaAbove);

            Set<UUID> currentPlayersUUIDs = new HashSet<>();
            for (PlayerEntity player : playersCurrentlyOnBlock) {
                UUID playerUUID = player.getUuid();
                currentPlayersUUIDs.add(playerUUID);
                EntityAttributeInstance movementSpeedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (movementSpeedAttribute != null && isPowered()) {
                    EntityAttributeModifier modifier = new EntityAttributeModifier(MOVING_WALKWAY_SPEED_BOOST_ID, "MovingWalkwaySpeedBoost", SPEED_BOOST_AMOUNT, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                    if (movementSpeedAttribute.getModifier(MOVING_WALKWAY_SPEED_BOOST_ID) == null) {
                        movementSpeedAttribute.addPersistentModifier(modifier);
                    }
                }
                // Add player to the set if not already present
                playersOnWalkway.add(playerUUID);
            }
            // Check for players who have left the walkway
            playersOnWalkway.removeIf(playerUUID -> {
                if (!currentPlayersUUIDs.contains(playerUUID)) {
                    // Player has left the walkway
                    PlayerEntity player = world.getPlayerByUuid(playerUUID);
                    if (player != null) {
                        EntityAttributeInstance movementSpeedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                        if (movementSpeedAttribute != null && movementSpeedAttribute.getModifier(MOVING_WALKWAY_SPEED_BOOST_ID) != null) {
                            movementSpeedAttribute.removeModifier(MOVING_WALKWAY_SPEED_BOOST_ID);
                        }
                    }
                    return true;
                }
                return false;
            });
        }
    }

}

