package net.stardust.circuitmod.block.entity.slave.fuelgenerator;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.*;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class FuelGeneratorEnergySlaveBlockEntity extends BlockEntity implements EnergyStorage{
    public FuelGeneratorEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;
    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    public void burnFuel(int fuelAmount, float efficiency) {
        long energyToGenerate = (long) (fuelAmount * efficiency);
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        // Clear the visited positions set at the beginning of each tick
        visitedPositions.clear();
        BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
        if (!(masterBlockEntity instanceof FuelGeneratorBlockEntity)) {
            System.out.println("Master block entity is not a FuelGeneratorBlockEntity or is null");
            return;
        }
        FuelGeneratorBlockEntity master = (FuelGeneratorBlockEntity) masterBlockEntity;
        boolean isPowered = master.getPoweredState();
        if (isPowered) {
            return;
        }
        if (masterPos != null) {
            if (currentEnergy > MAX_ENERGY) {
                currentEnergy = MAX_ENERGY;
            }
            if (currentEnergy > 0) {
                // Find and distribute energy to PumpJackBlockEntity instances
                List<PumpJackEnergySlaveBlockEntity> pumpJacks = findPumpJacks(pos, null);
                distributeEnergyToPumpJacks(pumpJacks);
            }
            markDirty();
        }
    }

    private void distributeEnergyToPumpJacks(List<PumpJackEnergySlaveBlockEntity> pumpJacks) {
        for (PumpJackEnergySlaveBlockEntity pumpJack : pumpJacks) {
            if (currentEnergy > 0) {
                int energyToGive = (int) Math.min(100, currentEnergy); // Adjust the energy amount as needed
                pumpJack.addEnergy(energyToGive); // Directly add energy to the pump jack
                currentEnergy -= energyToGive; // Reduce the energy from the generator
                System.out.println("Direct energy transfer to PumpJackBlockEntity: " + energyToGive);
            }
        }
    }


    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    private Set<BlockPos> visitedPositions = new HashSet<>();

    private List<PumpJackEnergySlaveBlockEntity> findPumpJacks(BlockPos currentPosition, @Nullable Direction fromDirection) {
        // Initialize a list to store found pump jacks
        List<PumpJackEnergySlaveBlockEntity> pumpJacks = new ArrayList<>();

        // Add the current position to the visited set to avoid revisiting
        if (!visitedPositions.add(currentPosition)) {
            return pumpJacks; // Early return if already visited
        }

        // Iterate through all directions except the opposite of the direction we came from
        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue;
            }

            // Calculate the next position to check
            BlockPos nextPos = currentPosition.offset(direction);

            // Check if the next position is already visited to avoid loops
            if (!visitedPositions.contains(nextPos)) {
                BlockEntity blockEntity = world.getBlockEntity(nextPos);

                // Check if the block entity is a PumpJackBlockEntity
                if (blockEntity instanceof PumpJackEnergySlaveBlockEntity) {
                    System.out.println("Found PumpJackBlockEnergySlaveEntity at: " + nextPos);
                    pumpJacks.add((PumpJackEnergySlaveBlockEntity) blockEntity);
                } else if (blockEntity instanceof ConductorBlockEntity) {
                    // If it's a conductor, continue searching in the same direction
                    pumpJacks.addAll(findPumpJacks(nextPos, direction));
                }
            }
        }

        // Return the list of found pump jacks
        return pumpJacks;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        long inserted = Math.min(maxAmount, MAX_ENERGY - currentEnergy);
        currentEnergy += inserted;
        markDirty();
        return inserted;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long extracted = Math.min(maxAmount, currentEnergy);
        currentEnergy -= extracted;
        markDirty();
        return extracted;
    }

    @Override
    public long getAmount() {
        return currentEnergy;
    }

    @Override
    public long getCapacity() {
        return MAX_ENERGY;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("fuel_generator_energy_slave.energy", currentEnergy);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("fuel_generator_energy_slave.energy")) {
            currentEnergy = nbt.getLong("fuel_generator_energy_slave.energy");
        }
        if (nbt.contains("MasterPosX") && nbt.contains("MasterPosY") && nbt.contains("MasterPosZ")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            masterPos = new BlockPos(x, y, z);
        }
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
}
