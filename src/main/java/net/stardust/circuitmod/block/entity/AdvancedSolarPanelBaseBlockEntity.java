package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.CircuitMod;
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


    public void burnSunlight(World world, BlockPos pos, BlockState state) {
        if (!world.isDay() || world.isRaining() || world.isThundering()) {
            System.out.println("No energy generation due to weather or night.");
            return;
        }

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        boolean isEastWestAligned = facing == Direction.EAST || facing == Direction.WEST;
        boolean isNorthSouthAligned = facing == Direction.NORTH || facing == Direction.SOUTH;

        long timeOfDay = world.getTimeOfDay() % 24000;
        if (timeOfDay < 6000) {
            timeOfDay += 24000;
        }

        // Adjust for the direction of the solar panel
        double timeAdjusted = timeOfDay - 6000;
        if (isEastWestAligned) {
            if (facing == Direction.EAST) {
                timeAdjusted += 780;
            } else if (facing == Direction.WEST) {
                timeAdjusted -= 780;
            }
        }
        if (isNorthSouthAligned) {
            if (facing == Direction.SOUTH) {
                // Adjustments for SOUTH facing, if necessary
            } else if (facing == Direction.NORTH) {
                // Adjustments for NORTH facing, if necessary
            }
        }

        double primaryMultiplier = 0.1 * Math.cos((2 * Math.PI / 24000) * timeAdjusted) + 0.9;
        primaryMultiplier = Math.max(primaryMultiplier, 0.9); // Ensuring it doesn't drop below 0.9

        double efficiencyMultiplier = primaryMultiplier;
        int solarModeMultiplier = 0;

        // Gets the Solar Mode config enum, and turns it into a numerical value.
        String solarMode = CircuitMod.CONFIG.powerScaling.solarMode().toString();
        if (solarMode == "NORMAL") {
            solarModeMultiplier = 25;
        } else if (solarMode == "REALISTIC") {
            solarModeMultiplier = 1;
        } else if (solarMode == "BUFFED") {
            solarModeMultiplier = 50;
        }

        // SET MAX ENERGY HERE
        final long BASE_ENERGY_PER_SECOND = 2250; //250×9

        // Power output code
        // Energy to Generate is [MAX OUTPUT] × [TIME OF DAY] × [SOLAR MODE(Config)] × [POWER SCALING(Config)]
        // MAX OUTPUT is defined above, and is the max output the panel will generate (Based on realistic values)
        // TIME OF DAY is the function that will get the current time and output a value between 0 and 1
        // SOLAR MODE is the value from the config that make solar panels output REALISTIC, NORMAL, or BUFFED amounts of power (1×, 25×, 50×, respectively)
        // POWER SCALING is the config value that multiplies power output from the user's input.
        // Effectively, we take the power generation from the solar panel, nerf it a bit for the basic panel, and multiply it by config settings.
        long energyToGenerate = (long) (BASE_ENERGY_PER_SECOND * efficiencyMultiplier * solarModeMultiplier * CircuitMod.CONFIG.powerScaling.powerGenerationScale());


        // Assuming currentEnergy and MAX_ENERGY are defined elsewhere
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        //System.out.println("Current World Time Is " + timeOfDay);
        //System.out.println("Panel Facing " + facing + " has a multiplier of" + efficiencyMultiplier);
        //System.out.println("BASE = " + (BASE_ENERGY_PER_SECOND * efficiencyMultiplier) + " SMM = " + solarModeMultiplier + " SCALING = " + CircuitMod.CONFIG.powerScaling.powerGenerationScale() + " ADV TOTAL = " + energyToGenerate);
        //System.out.println(efficiencyMultiplier);

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
        //System.out.println("Visiting position: " + currentPosition + ", from direction: " + fromDirection);

        if (!visitedPositions.add(currentPosition)) {
            //System.out.println("Already visited position: " + currentPosition);
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
