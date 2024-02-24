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

        //Few notes on the logic to keep in mind:
        //Minecraft's Daylight cycle is kinda BS, sunrise begins at 23000 ticks, and sunset begins at
        //12000 ticks (Effectively we need the end of sunset, which is 13000). So, instead of starting
        //power generation at 0 ticks we need to be doing so at 23000 or -1000 from the current day.

        //A few extra numbers of the cycle:
        //Sunrise starts at 23000t
        //Solar zenith angle of 0 (rising) is at 23216t
        //(0 ticks is here, the technical start of the day/when the player wakes up from a bed)
        //Noon is at 6000t
        //Sunset begins at 12000t
        //Solar zenith angle of 0 (setting) is at 12786t
        //Sun is below the horizon at 13000t (Also the start of night)

        //Essentially, the time calcs need to start at 23000/-1000 ticks, peak at 6000, and end at 13000
        //Plus taking into account that the peak will be different depending on if it's facing East or West, since
        //The panels are at a 22.5 degree angle ;)


        long timeOfDay = world.getTimeOfDay() % 24000;

        double primaryMultiplier = Math.cos((2 * Math.PI / 24000) * (timeOfDay - 6000));
        // Ensure that the multiplier is never negative (as cosine can be negative)
        primaryMultiplier = Math.max(primaryMultiplier, 0);

        double orientationMultiplier = isOptimallyAligned ? 1.0 : 0.5;
        double efficiencyMultiplier = primaryMultiplier * orientationMultiplier;

        final long BASE_ENERGY_PER_SECOND = 100;
        long energyToGenerate = (long) (BASE_ENERGY_PER_SECOND * efficiencyMultiplier);

        // Assuming currentEnergy and MAX_ENERGY are defined elsewhere
        currentEnergy += energyToGenerate;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }

        System.out.println("Current World Time Is " + timeOfDay);
        System.out.println("Generated " + energyToGenerate + " energy this second. Total energy: " + currentEnergy + ". Facing: " + facing);
        System.out.println(efficiencyMultiplier + " is the multiplier currently");

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
