package net.stardust.circuitmod.block.entity.slave;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class EfficientCoalGeneratorEnergySlaveBlockEntity extends BlockEntity implements EnergyStorage{
    public EfficientCoalGeneratorEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;

    private int tickCounter = 0;
    private long energyPerTick = 0;
    private long ticksPerFuel = 0;
    private long ticksRemainingOnFuel = 0;


    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
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

                            //System.out.println("Target" + " accepted " + (extracted - remainingForTarget) + " energy. Remaining for target: " + remainingForTarget);

                            // if the target does not accept all the energy, add to incompleteTargets list
                            if (remainingForTarget > 0) {
                                // insert(remainingForTarget, transaction);
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
    private List<EnergyStorage> findEnergyTargets(BlockPos currentPosition, @Nullable Direction fromDirection) {
        List<EnergyStorage> targets = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue;
            }

            BlockPos nextPos = currentPosition.offset(direction);
            BlockEntity nextEntity = world.getBlockEntity(nextPos);

            if (nextEntity instanceof ConductorBlockEntity) {
                targets.addAll(findEnergyTargets(nextPos, direction));
            } else if (nextEntity != null) {
                EnergyStorage target = EnergyStorage.SIDED.find(world, nextPos, direction.getOpposite());
                if (target != null && !(nextEntity instanceof EfficientCoalGeneratorBlockEntity)) {
                    targets.add(target);
                }
            }
        }

        return targets;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        if (masterPos != null) {
            BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
            if (masterBlockEntity instanceof EfficientCoalGeneratorBlockEntity) {
                EfficientCoalGeneratorBlockEntity master = (EfficientCoalGeneratorBlockEntity) masterBlockEntity;
                if (ticksRemainingOnFuel > 0) {
                    // Still generating energy from the last fuel.
                    currentEnergy += energyPerTick;
                    ticksRemainingOnFuel--;
                    distributeEnergyToTargets();
                } else if (master.isFuel()) {
                    // No fuel is being processed, check for new fuel.
                    if (master.consumeFuel()) {
                        if (master.hasCoalBlock()) {
                            energyPerTick = ENERGY_PER_COAL_BLOCK / (20 * 9); // Spread over 9 times 10 seconds
                            ticksPerFuel = 20 * 9; // 9 times 10 seconds worth of ticks
                        } else {
                            energyPerTick = ENERGY_PER_COAL / 20; // Spread over 10 seconds
                            ticksPerFuel = 20; // 10 seconds worth of ticks
                        }
                        ticksRemainingOnFuel = ticksPerFuel;
                    }
                }
                // This will ensure we do not exceed MAX_ENERGY
                if (currentEnergy > MAX_ENERGY) {
                    currentEnergy = MAX_ENERGY;
                }
                markDirty();
            }
        }
    }

    private static final long ENERGY_PER_COAL = 5000; // Example energy value for one coal
    private static final long ENERGY_PER_COAL_BLOCK = ENERGY_PER_COAL * 9; // Energy for a block of coal (9 times more)




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
        nbt.putLong("efficient_coal_generator_energy_slave.energy", currentEnergy);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("efficient_coal_generator_energy_slave.energy")) {
            currentEnergy = nbt.getLong("efficient_coal_generator_energy_slave.energy");
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
