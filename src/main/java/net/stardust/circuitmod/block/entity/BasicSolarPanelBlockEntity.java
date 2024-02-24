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
    private static final long MAX_ENERGY_GENERATION = 100; //The max wanted energy per second
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
                timeAdjusted += 800; //Was 780
            } else if (facing == Direction.WEST) {
                timeAdjusted -= 800;
            }
        }
        if (isNorthSouthAligned) {
            if (facing == Direction.SOUTH) {

            } else if (facing == Direction.NORTH) {
            }
        }


        double primaryMultiplier = Math.cos((1.997515 * Math.PI / 24000) * timeAdjusted);
        primaryMultiplier = Math.max(primaryMultiplier, 0);


        double efficiencyMultiplier = primaryMultiplier;

        final long BASE_ENERGY_PER_SECOND = 102; //Should be a bit higher than the wanted max energy per second
        long energyToGenerate = (long) (BASE_ENERGY_PER_SECOND * efficiencyMultiplier);

        // Assuming currentEnergy and MAX_ENERGY are defined elsewhere
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        //Since the Base Energy per second is greater than 100, capping generation at 100 will make the
        //generation plateau at 100 for a bit at it's peak
        if (energyToGenerate > MAX_ENERGY_GENERATION) {
            energyToGenerate = MAX_ENERGY_GENERATION;
        }

        System.out.println("time " + world.getTimeOfDay() % 24000 + " | power " + energyToGenerate + " | multiplier " + efficiencyMultiplier);
        //System.out.println("Current World Time Is " + timeOfDay);
       // System.out.println("Generated " + energyToGenerate + " energy this second. Total energy: " + currentEnergy + ". Facing: " + facing);
        //System.out.println("Panel Facing " + facing + " has a multiplier of"+ efficiencyMultiplier);

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
