package net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator;

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
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class EfficientCoalGeneratorEnergySlaveBlockEntity extends BlockEntity{
    public EfficientCoalGeneratorEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    // Define fuel values and burn times
    private static final Map<Item, Long> ENERGY_VALUES = new HashMap<>();
    private static final Map<Item, Integer> BURN_TIMES = new HashMap<>();
    public static Map<Item, Integer> getBurnTimes() {
        return BURN_TIMES;
    }

    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;

    static {
        // Energy values for different fuels
        ENERGY_VALUES.put(Items.COAL, 5000L); // Energy value for one coal
        // Add energy values for different types of wood
        long plankEnergyValue = 1000L; // Example value for all types of planks
        long logEnergyValue = plankEnergyValue * 4; // Assuming a log is worth four planks

        // Other Energy Values
        ENERGY_VALUES.put(Items.COAL_BLOCK, 5000L * 9); // 9 times the energy of coal
        ENERGY_VALUES.put(Items.BAMBOO, 200L); // Lower energy value than coal
        ENERGY_VALUES.put(Items.BAMBOO_BLOCK, 200L * 9);

        // Add energy values for all types of planks
        ENERGY_VALUES.put(Items.OAK_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.SPRUCE_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.BIRCH_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.JUNGLE_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.ACACIA_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.DARK_OAK_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.CRIMSON_PLANKS, plankEnergyValue);
        ENERGY_VALUES.put(Items.WARPED_PLANKS, plankEnergyValue);

        // Add energy values for all types of logs
        ENERGY_VALUES.put(Items.OAK_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.SPRUCE_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.BIRCH_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.JUNGLE_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.ACACIA_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.DARK_OAK_LOG, logEnergyValue);
        ENERGY_VALUES.put(Items.CRIMSON_STEM, logEnergyValue);
        ENERGY_VALUES.put(Items.WARPED_STEM, logEnergyValue);

        // Burn times for different fuels
        BURN_TIMES.put(Items.COAL, 200); // 10 seconds for coal
        int plankBurnTime = 100; // Example burn time for all types of planks
        int logBurnTime = plankBurnTime * 2; // Assuming a log burns longer than a plank

        // Other Burn Times
        BURN_TIMES.put(Items.COAL_BLOCK, 200 * 9);
        BURN_TIMES.put(Items.BAMBOO, 50);
        BURN_TIMES.put(Items.BAMBOO_BLOCK, 50 * 9);

        // Add burn times for all types of planks
        BURN_TIMES.put(Items.OAK_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.SPRUCE_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.BIRCH_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.JUNGLE_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.ACACIA_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.DARK_OAK_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.CRIMSON_PLANKS, plankBurnTime);
        BURN_TIMES.put(Items.WARPED_PLANKS, plankBurnTime);

        // Add burn times for all types of logs
        BURN_TIMES.put(Items.OAK_LOG, logBurnTime);
        BURN_TIMES.put(Items.SPRUCE_LOG, logBurnTime);
        BURN_TIMES.put(Items.BIRCH_LOG, logBurnTime);
        BURN_TIMES.put(Items.JUNGLE_LOG, logBurnTime);
        BURN_TIMES.put(Items.ACACIA_LOG, logBurnTime);
        BURN_TIMES.put(Items.DARK_OAK_LOG, logBurnTime);
        BURN_TIMES.put(Items.CRIMSON_STEM, logBurnTime);
        BURN_TIMES.put(Items.WARPED_STEM, logBurnTime);
    }
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
            if (!fuelStack.isEmpty()) {
                Item fuelItem = fuelStack.getItem();
                Map<Item, Integer> burnTimes = getBurnTimes();
                int burnTime = burnTimes.getOrDefault(fuelItem, 200);
                ticksRemainingOnFuel = burnTime;

                fuelStack.decrement(1);
                fuelLevel = MAX_FUEL_LEVEL;
                return true;
            }
        }
        return false;
    }
    private List<IEnergyConsumer> findEnergyConsumers(BlockPos currentPosition, @Nullable Direction fromDirection) {
        List<IEnergyConsumer> consumers = new ArrayList<>();

        System.out.println("Visiting position: " + currentPosition); // Print current position being visited

        if (!visitedPositions.add(currentPosition)) {
            System.out.println("Already visited: " + currentPosition); // Print if position was already visited
            return consumers; // Early return if already visited
        }

        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue; // Skip the opposite direction of the incoming direction
            }

            BlockPos nextPos = currentPosition.offset(direction);
            System.out.println("Checking next position: " + nextPos + " in direction: " + direction); // Print next position and direction being checked

            if (!visitedPositions.contains(nextPos)) {
                BlockEntity blockEntity = world.getBlockEntity(nextPos);

                if (blockEntity instanceof IEnergyConsumer) {
                    System.out.println("Found consumer at: " + nextPos); // Print when a consumer is found
                    consumers.add((IEnergyConsumer) blockEntity);
                } else if (blockEntity instanceof ConductorBlockEntity) {
                    System.out.println("Found conductor at: " + nextPos + ". Continuing search."); // Print when a conductor is found
                    // If it's a conductor, continue searching in the same direction
                    consumers.addAll(findEnergyConsumers(nextPos, direction));
                } else {
                    System.out.println("No valid energy consumer or conductor at: " + nextPos); // Print when neither a consumer nor conductor is found
                }
            } else {
                System.out.println("Position already visited or blocked: " + nextPos); // Print if the next position was already visited or is not accessible
            }
        }
        return consumers;
    }




    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        visitedPositions.clear();
        if (masterPos != null) {
            BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
            if (masterBlockEntity instanceof EfficientCoalGeneratorBlockEntity) {
                EfficientCoalGeneratorBlockEntity master = (EfficientCoalGeneratorBlockEntity) masterBlockEntity;
                master.updateFuelLevel(this.fuelLevel);

                boolean isPowered = master.getPoweredState();
                if (isPowered) {
                    return;
                }
                // Check if there's remaining fuel to burn
                if (ticksRemainingOnFuel > 0) {
                    currentEnergy += energyPerTick;
                    ticksRemainingOnFuel--; // Decrement the remaining burn time

                    fuelLevel = (int) (((float)ticksRemainingOnFuel / (float)BURN_TIMES.getOrDefault(master.getFuelItem().getItem(), 0)) * MAX_FUEL_LEVEL);
                    master.updateFuelLevel(this.fuelLevel);
                    List<IEnergyConsumer> consumers = findEnergyConsumers(pos, null);
                    distributeEnergy(consumers);
                } else {
                    // Attempt to consume new fuel if there's no remaining fuel
                    if (master.isFuel() && consumeFuel(master)) {
                        // Fuel consumed, update energy per tick and reset burn time
                        ItemStack fuelStack = master.getFuelItem();
                        Item fuelItem = fuelStack.getItem();
                        long energyPerFuel = ENERGY_VALUES.getOrDefault(fuelItem, 1000L);
                        int burnTime = BURN_TIMES.getOrDefault(fuelItem, 200);

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
    private void distributeEnergy(List<IEnergyConsumer> consumers) {
        for (IEnergyConsumer consumer : consumers) {
            if (currentEnergy > 0) {
                int energyToGive = (int) Math.min(100, currentEnergy);
                consumer.addEnergy(energyToGive);
                currentEnergy -= energyToGive;
                System.out.println("Energy transferred: " + energyToGive);
            }
        }
    }

    private Set<BlockPos> visitedPositions = new HashSet<>();
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("efficient_coal_generator_energy_slave.energy", currentEnergy);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("efficient_coal_generator_energy_slave.energy")) {
            currentEnergy = nbt.getLong("efficient_coal_generator_energy_slave.energy");
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
