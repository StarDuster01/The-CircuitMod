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

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        visitedPositions.clear();
        burnSunlight(world,pos,state);
        if (currentEnergy > 0) {
            // Find and distribute energy to IEnergyConsumer instances
            List<IEnergyConsumer> consumers = findEnergyConsumers(pos, null);
            distributeEnergy(consumers);
        }
    }

    public void burnSunlight(World world, BlockPos pos, BlockState state) {
        if (!world.isDay() || world.isRaining() || world.isThundering()) {
            System.out.println("No energy generation due to weather or night.");
            return; // No energy generation during night, rain, or thunder
        }

        // Determine the solar panel's facing direction
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        boolean isOptimallyAligned = facing == Direction.EAST || facing == Direction.WEST;

        // Calculate energy based on the time of day, more energy at noon
        float celestialAngle = world.getSkyAngle(1.0f);
        float efficiencyMultiplier = 1.0f - Math.abs(celestialAngle - 0.5f) * 2;
        efficiencyMultiplier = Math.max(0.1f, efficiencyMultiplier);

        // Adjust efficiency based on panel alignment and tilt
        float alignmentMultiplier = isOptimallyAligned ? (float) Math.cos(Math.toRadians(22.5)) : 0.8f; // Assuming non-optimal alignment captures 80% as efficiently
        efficiencyMultiplier *= alignmentMultiplier;

        // Additional tilt adjustment for when the sun is not directly overhead
        // This simulates reduced efficiency in the morning and late afternoon due to the panel's tilt
        float tiltAdjustment = calculateTiltAdjustment(celestialAngle, facing);
        efficiencyMultiplier *= tiltAdjustment;

        // Define a base energy generation value for each tick
        final long BASE_ENERGY_PER_TICK = 1; // Adjust this value as needed
        long energyToGenerate = (long) (BASE_ENERGY_PER_TICK * efficiencyMultiplier);

        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        // Print statement showing how much energy was generated in this tick
        System.out.println("Generated " + energyToGenerate + " energy this tick. Total energy: " + currentEnergy + ". Facing: " + facing);

        markDirty(); // Notify the world that this block entity's data has changed
    }

    private float calculateTiltAdjustment(float celestialAngle, Direction facing) {
        // Simulate the effect of the panel's tilt reducing efficiency in the morning and late afternoon
        // This is a simplified model; a more complex model could take into account the exact solar elevation angle
        float angleFromZenith = Math.abs(celestialAngle - 0.5f) * 2;
        float tiltEffect = 1.0f - (0.4f * angleFromZenith); // Assume up to 40% efficiency reduction based on the sun's angle
        // Adjust the tilt effect based on the panel's facing direction
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            tiltEffect *= 0.9; // Further reduce efficiency for north and south facing panels
        }

        return Math.max(0.6f, tiltEffect); // Ensure at least 60% efficiency to prevent negative values
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
