package net.stardust.circuitmod.block.entity.slave;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
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
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class EfficientCoalGeneratorEnergySlaveBlockEntity extends BlockEntity implements EnergyStorage{
    public EfficientCoalGeneratorEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    // Define fuel values and burn times
    private static final Map<Item, Long> ENERGY_VALUES = new HashMap<>();
    private static final Map<Item, Integer> BURN_TIMES = new HashMap<>();
    public static Map<Item, Integer> getBurnTimes() {
        return BURN_TIMES;
    }

    static {
        ENERGY_VALUES.put(Items.COAL, 5000L); // Energy value for one coal
        ENERGY_VALUES.put(Items.OAK_PLANKS, 1000L); // Energy value for wooden plank
        // Add more fuels as needed

        BURN_TIMES.put(Items.COAL, 200); // 10 seconds for coal
        BURN_TIMES.put(Items.OAK_PLANKS, 100); // 5 seconds for wooden plank
        // Add more fuels as needed
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

    private static final int MAX_FUEL_LEVEL = 10;
    private int fuelLevel = 0;



    public boolean consumeFuel(EfficientCoalGeneratorBlockEntity master) {
        if (master != null) {
            ItemStack fuelStack = master.getFuelItem();
            if (!fuelStack.isEmpty() && (fuelStack.isOf(Items.COAL) || fuelStack.isOf(Items.COAL_BLOCK))) {
                Item fuelItem = fuelStack.getItem();
                Map<Item, Integer> burnTimes = getBurnTimes();
                int burnTime = burnTimes.getOrDefault(fuelItem, 0);
                ticksRemainingOnFuel = burnTime;

                fuelStack.decrement(1);
                fuelLevel = MAX_FUEL_LEVEL;
                return true;
            }
        }
        return false;
    }



    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        if (masterPos != null) {
            BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
            if (masterBlockEntity instanceof EfficientCoalGeneratorBlockEntity) {
                EfficientCoalGeneratorBlockEntity master = (EfficientCoalGeneratorBlockEntity) masterBlockEntity;
                master.updateFuelLevel(this.fuelLevel);

                boolean isPowered = master.getPoweredState();
                if (isPowered) {
                    return; // Stop processing if the master block is powered off
                }

                // Check if there's remaining fuel to burn
                if (ticksRemainingOnFuel > 0) {
                    currentEnergy += energyPerTick;
                    ticksRemainingOnFuel--; // Decrement the remaining burn time

                    fuelLevel = (int) (((float)ticksRemainingOnFuel / (float)BURN_TIMES.getOrDefault(master.getFuelItem().getItem(), 0)) * MAX_FUEL_LEVEL);
                    master.updateFuelLevel(this.fuelLevel);
                    distributeEnergyToTargets();
                } else {
                    // Attempt to consume new fuel if there's no remaining fuel
                    if (master.isFuel() && consumeFuel(master)) {
                        // Fuel consumed, update energy per tick and reset burn time
                        ItemStack fuelStack = master.getFuelItem();
                        Item fuelItem = fuelStack.getItem();
                        long energyPerFuel = ENERGY_VALUES.getOrDefault(fuelItem, 0L);
                        int burnTime = BURN_TIMES.getOrDefault(fuelItem, 0);

                        energyPerTick = energyPerFuel / burnTime;
                        ticksRemainingOnFuel = burnTime;
                    }
                }
            }
            if (currentEnergy > MAX_ENERGY) {
                currentEnergy = MAX_ENERGY;
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
                if (target != null && !(nextEntity instanceof EfficientCoalGeneratorBlockEntity)) {
                    targets.add(target);
                }
            }
        }

        return targets;
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
