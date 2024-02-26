package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdvancedSolarPanelBaseBlockEntity extends BlockEntity {


    public AdvancedSolarPanelBaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_SOLAR_PANEL_BASE_BE, pos, state);
    }
    private static final long MAX_ENERGY = 100000;
    private long currentEnergy = 0;

    private Set<BlockPos> visitedPositions = new HashSet<>();
    private int tickCounter = 0;


    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return; // Skip client-side execution

        if (++tickCounter >= 20) {
            tickCounter = 0; // Reset the counter
            burnSunlight(world, pos, state); // Execute burnSunlight every 10th tick
        }

        if (currentEnergy > 0) {
            visitedPositions.clear(); // Reset visited positions before search
            List<IEnergyConsumer> consumers = findEnergyConsumers(pos, null);
            distributeEnergy(consumers);
        }
    }


    public void burnSunlight(World world, BlockPos pos, BlockState state) { //TODO Update Energy Logic for rotation
        if (!world.isDay() || world.isRaining() || world.isThundering()) {
            System.out.println("No energy generation due to weather or night.");
            return;
        }


        long timeOfDay = world.getTimeOfDay() % 24000;
        if (timeOfDay < 6000) {
            timeOfDay += 24000;
        }

        double primaryMultiplier = Math.cos((2 * Math.PI / 24000) * 1);
        primaryMultiplier = Math.max(primaryMultiplier, 0);


        double efficiencyMultiplier = primaryMultiplier;

        final long BASE_ENERGY_PER_SECOND = 100;
        long energyToGenerate = (long) (BASE_ENERGY_PER_SECOND * efficiencyMultiplier);

        // Assuming currentEnergy and MAX_ENERGY are defined elsewhere
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        System.out.println("Current World Time Is " + timeOfDay);
       // System.out.println("Generated " + energyToGenerate + " energy this second. Total energy: " + currentEnergy + ". Facing: " + facing);
        System.out.println(" has a multiplier of"+ efficiencyMultiplier);

        // Assuming markDirty() is defined elsewhere
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
        System.out.println("Visiting position: " + currentPosition + ", from direction: " + fromDirection);

        if (!visitedPositions.add(currentPosition)) {
            System.out.println("Already visited position: " + currentPosition);
            return consumers; // Early return if already visited
        }

        for (Direction direction : Direction.values()) {
            if (fromDirection != null && direction == fromDirection.getOpposite()) {
                continue; // Skip the opposite direction to prevent backtracking
            }

            BlockPos nextPos = currentPosition.offset(direction);
            if (!visitedPositions.contains(nextPos)) {
                BlockEntity blockEntity = world.getBlockEntity(nextPos);

                // Check for IEnergyConsumer or continue with conductors
                if (blockEntity instanceof IEnergyConsumer) {
                    consumers.add((IEnergyConsumer) blockEntity);
                } else if (blockEntity instanceof ConductorBlockEntity) {
                    // Recursive call to follow conductor path
                    consumers.addAll(findEnergyConsumers(nextPos, direction));
                }
            }
        }
        return consumers;
    }


}
