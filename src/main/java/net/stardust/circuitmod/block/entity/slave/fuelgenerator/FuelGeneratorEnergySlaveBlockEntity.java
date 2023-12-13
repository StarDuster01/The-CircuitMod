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
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class FuelGeneratorEnergySlaveBlockEntity extends BlockEntity implements EnergyStorage{
    public FuelGeneratorEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    // Define fuel values and burn times
    private static final Map<Item, Long> ENERGY_VALUES = new HashMap<>();

    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    private static final int MAX_FUEL_LEVEL = 10;
    private int fuelLevel = 0;

    public void burnFuel(int fuelAmount, float efficiency) {
        long energyToGenerate = (long) (fuelAmount * efficiency);
        System.out.println("Energy Slave believes the amount of energy to create to be" + energyToGenerate);
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        if (masterPos != null) {
            BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
            if (masterBlockEntity instanceof FuelGeneratorBlockEntity) {
                FuelGeneratorBlockEntity master = (FuelGeneratorBlockEntity) masterBlockEntity;
                master.updateFuelLevel(this.fuelLevel);

            }
            if (currentEnergy > MAX_ENERGY) {
                currentEnergy = MAX_ENERGY;
            }
            if (currentEnergy > 0) {
                distributeEnergyToTargets();
            }
            markDirty();
        }
    }


    public BlockPos getMasterPos() {
        return this.masterPos;
    }
    private void distributeEnergyToTargets() {
        List<EnergyStorage> targets = findEnergyTargets(this.pos, null);

        if (!targets.isEmpty() && currentEnergy > 0) {
            long totalEnergyToDistribute = Math.min(currentEnergy, 100000);
            long remainingEnergy = totalEnergyToDistribute;
            long actualExtractedTotal = 0;

            while (!targets.isEmpty() && remainingEnergy > 0) {
                long energyToEachTarget = remainingEnergy / targets.size();
                List<EnergyStorage> incompleteTargets = new ArrayList<>();

                for (EnergyStorage target : targets) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        long extracted = extract(energyToEachTarget, transaction);
                        if (extracted > 0) {
                            long remainingForTarget = target.insert(extracted, transaction);
                            if (remainingForTarget > 0) {
                                incompleteTargets.add(target);
                            }
                            actualExtractedTotal += (extracted - remainingForTarget);
                        }
                        transaction.commit();
                    }
                }

                remainingEnergy = totalEnergyToDistribute - actualExtractedTotal;
                targets = incompleteTargets; // update targets list for next iteration
            }

            currentEnergy -= actualExtractedTotal;
            if (currentEnergy < 0) currentEnergy = 0; // Ensure energy doesn't go negative
            markDirty();
        }
    }







    private Set<BlockPos> visitedPositions = new HashSet<>();
    private List<EnergyStorage> findEnergyTargets(BlockPos currentPosition, @Nullable Direction fromDirection) {
        // Clear visited positions at the beginning of the top-level call
        if (fromDirection == null) {
            visitedPositions.clear();
        }
        // Add the current position to the visited set
        visitedPositions.add(currentPosition);

        List<EnergyStorage> targets = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue;
            }

            BlockPos nextPos = currentPosition.offset(direction);
            // Check if we have already visited this position
            if (visitedPositions.contains(nextPos)) {
                continue;
            }

            BlockEntity nextEntity = world.getBlockEntity(nextPos);

            if (nextEntity instanceof ConductorBlockEntity) {
                targets.addAll(findEnergyTargets(nextPos, direction));
            } else if (nextEntity != null) {
                EnergyStorage target = EnergyStorage.SIDED.find(world, nextPos, direction.getOpposite());
                if (target != null && !(nextEntity instanceof FuelGeneratorBlockEntity)) {
                    targets.add(target);
                }
            }
        }

        return targets;
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
