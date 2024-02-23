package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.custom.FuelGeneratorBlock;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.screen.FuelGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicSolarPanelBlockEntity extends BlockEntity {


    public BasicSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_SOLAR_PANEL_BE, pos, state);
    }
    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;

    private Set<BlockPos> visitedPositions = new HashSet<>();
    private int tickCounter = 0;


    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return; // Skip client-side execution

        // Increment the tick counter and check if it's time to burn sunlight
        if (++tickCounter >= 20) {
            tickCounter = 0; // Reset the counter
            burnSunlight(world, pos, state); // Execute burnSunlight every 10th tick
        }

        // Your existing energy distribution logic
        if (currentEnergy > 0) {
            // Find and distribute energy to IEnergyConsumer instances
            List<IEnergyConsumer> consumers = findEnergyConsumers(pos, null);
            distributeEnergy(consumers);
        }
    }


    public void burnSunlight(World world, BlockPos pos, BlockState state) {
        if (!world.isDay() || world.isRaining() || world.isThundering()) {
            System.out.println("No energy generation due to weather or night.");
            return;
        }

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        boolean isOptimallyAligned = facing == Direction.EAST || facing == Direction.WEST;

        long timeOfDay = world.getTimeOfDay() % 24000;
        double tSunrise = 0; // Assuming sunrise at the start of the Minecraft day
        double tNoon = 6000;
        double tDurationUntilNoon = tNoon - tSunrise; // Duration from sunrise to noon

        // Ensuring timeOfDay is within the sunrise to sunset range for energy calculation
        if (timeOfDay > tNoon) {
            System.out.println("Past noon, reducing energy generation.");
            tDurationUntilNoon = 12000 - tNoon; // Adjusting for the afternoon phase
            timeOfDay -= tNoon; // Adjust time of day for afternoon calculation
        }

        double fractionOfDay = (double) timeOfDay / tDurationUntilNoon;
        double primaryMultiplier = Math.sin(Math.PI * fractionOfDay);

        double orientationMultiplier = isOptimallyAligned ? 1.0 : 0.5;
        double efficiencyMultiplier = primaryMultiplier * orientationMultiplier;

        final long BASE_ENERGY_PER_SECOND = 100; // Adjust as needed
        long energyToGenerate = (long) (BASE_ENERGY_PER_SECOND * efficiencyMultiplier);

        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        System.out.println("Generated " + energyToGenerate + " energy this tick. Total energy: " + currentEnergy + ". Facing: " + facing);
        System.out.println(efficiencyMultiplier + "IS the multiplier currently");

        markDirty();
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

}
