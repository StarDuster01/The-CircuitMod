package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
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

import net.stardust.circuitmod.block.custom.FuelGeneratorBlock;

import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.screen.FuelGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FuelGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE

    // Change to different fuel types
    private static final float WATER_EFFICIENCY = 0.5f; // 50% efficiency
    private static final float LAVA_EFFICIENCY = 1.0f; // 100% efficiency
    private static final float CRUDE_OIL_EFFICIENCY = 1.0f; // 100% efficiency
    //

    private static final int INPUT_SLOT = 0;
    private static final int FLUID_SLOT = 1;
    private BlockPos energySlavePos; // Position of the energy slave block entity
    private static final int POWERED_INDEX = 1;
    private int fuelLevel = 0;
    private static final int RUNNING_INDEX = 2; // New index for isRunning
    private int fluidLevel = 0; // Add a field to store the fluid level
    private static final int FLUID_USAGE_INTERVAL = 20; // Number of ticks in one second

    private static final int FLUID_LEVEL_INDEX = 3; // Assign an appropriate index
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private boolean isRunning = true;
    private static final int FLUID_USAGE = 10; // Amount of fluid used to produce energy each operation
    private static final int ENERGY_PER_OPERATION = 100; // Energy produced each operation
    public ItemStack getFuelItem() {
        return inventory.get(INPUT_SLOT);
    }

    public FuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        this.energySlavePos = pos.offset(facing).up().offset(facing.rotateYClockwise()).offset(facing.getOpposite());
    }
    private FluidType currentFluidType = FluidType.NONE;

    public enum FluidType {
        WATER,
        LAVA,
        CRUDE_OIL,
        NONE // Represents no fluid or a fluid that is not tracked/used
    }
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
               // case 0: return fuelLevel; This may be a problem but I am trying to comment it out and see if it works
                case POWERED_INDEX: return isPowered ? 1 : 0;
                case RUNNING_INDEX: return isRunning ? 1 : 0;
                case FLUID_LEVEL_INDEX: return fluidLevel;
                default: return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case FLUID_LEVEL_INDEX: fluidLevel = value; break;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public int getFluidLevel() {
        return this.fluidLevel;
    }
    private int getCurrentEnergy() {
        if (energySlavePos != null) {
            BlockEntity be = world.getBlockEntity(energySlavePos);
            if (be instanceof FuelGeneratorEnergySlaveBlockEntity) {
                return (int)((FuelGeneratorEnergySlaveBlockEntity) be).getAmount();
            }
        }
        return 0;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        updateLitProperty();
        fillUpOnFluid();
        produceEnergyFromFluid();
        // Add the custom logic here soon
    }
    private float getFuelEfficiency(FluidType fluidType) {
        switch (fluidType) {
            case WATER:
                return WATER_EFFICIENCY;
            case LAVA:
                return LAVA_EFFICIENCY;
            case CRUDE_OIL:
                return CRUDE_OIL_EFFICIENCY;
            default:
                return 0; // No energy production for unrecognized or no fluid
        }
    }

    private void produceEnergyFromFluid() {
        if (fluidLevel >= FLUID_USAGE && currentFluidType != FluidType.NONE) {
            fluidLevel -= FLUID_USAGE;
            System.out.println("FluidType is" + currentFluidType);
            System.out.println("Fluid Level is"+fluidLevel);
            FuelGeneratorEnergySlaveBlockEntity energySlave =
                    (FuelGeneratorEnergySlaveBlockEntity) world.getBlockEntity(energySlavePos);

            if (energySlave != null) {
                System.out.println("Energy slave is present, that's not the problem ");
                float efficiency = getFuelEfficiency(currentFluidType);
                energySlave.burnFuel(ENERGY_PER_OPERATION, efficiency);
            }
            if (energySlave == null){
                System.out.println("No energy slave found");

            }
            isRunning = true;
        }
        else {
            System.out.println("Conditions not met to produce energy");
            isRunning = false; // Generator is not running. Fluid is either exhausted or unsupported.
        }
        System.out.println("Finished master class method produceEnergyFromFluid "+ getCurrentEnergy());
    }

    public void updateFuelLevel(int newFuelLevel) {
        this.fuelLevel = newFuelLevel;
        markDirty();
    }
    public void updatePoweredState(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            markDirty();
        }
    }
    public ItemStack tryInsertItem(ItemStack stackToInsert, boolean simulate) {
        return insertItem(INPUT_SLOT, stackToInsert, simulate);
    }
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot < 0 || slot >= this.size() || stack.isEmpty() || !this.canInsert(slot, stack, null)) {
            return stack;
        }
        ItemStack slotStack = this.getStack(slot);
        int m;
        if (slotStack.isEmpty()) {
            m = Math.min(this.getMaxCountPerStack(), stack.getCount());

            if (!simulate) {
                this.setStack(slot, stack.split(m));
                this.markDirty();
            } else {
                // When simulating, we just return the remainder without actually inserting.
                ItemStack copy = stack.copy();
                if (copy.getCount() <= m) {
                    return ItemStack.EMPTY;
                } else {
                    copy.decrement(m);
                    return copy;
                }
            }
        } else if (ItemStack.canCombine(stack, slotStack)) {
            m = Math.min(this.getMaxCountPerStack() - slotStack.getCount(), stack.getCount());

            if (!simulate) {
                slotStack.increment(m);
                stack.decrement(m);
                this.markDirty();
            } else {
                // When simulating, we just return the remainder without actually inserting.
                ItemStack copy = stack.copy();
                if (copy.getCount() <= m) {
                    return ItemStack.EMPTY;
                } else {
                    copy.decrement(m);
                    return copy;
                }
            }
        }

        if (stack.getCount() == 0) {
            return ItemStack.EMPTY;
        } else {
            return stack;
        }
    }

    private void fillUpOnFluid() {
        if (hasFluidSourceItemInFluidSlot(FLUID_SLOT)) {
            System.out.println("fillUpOnFluid called. Current fluid level: " + fluidLevel);
            transferItemFluidToTank(FLUID_SLOT);
            System.out.println("After fillUpOnFluid. New fluid level: " + fluidLevel);
        }
    }
    private FluidType getFluidTypeFromBucket(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == ModFluids.CRUDE_OIL_BUCKET) {
            return FluidType.CRUDE_OIL;
        } else if (item == Items.WATER_BUCKET) {
            return FluidType.WATER;
        } else if (item == Items.LAVA_BUCKET) {
            return FluidType.LAVA;
        } else {
            return FluidType.NONE;
        }
    }

    private boolean hasFluidSourceItemInFluidSlot(int fluidItemSlot) {
        FluidType fluidTypeInBucket = getFluidTypeFromBucket(this.getStack(fluidItemSlot));
        return fluidTypeInBucket != FluidType.NONE && (currentFluidType == FluidType.NONE || currentFluidType == fluidTypeInBucket);
    }


    private void transferItemFluidToTank(int fluidItemSlot) {
        if (fluidLevel <= 648000) {
            FluidType fluidTypeInBucket = getFluidTypeFromBucket(this.getStack(fluidItemSlot));
            if ((currentFluidType == FluidType.NONE || currentFluidType == fluidTypeInBucket) && hasFluidSourceItemInFluidSlot(fluidItemSlot)) {
                fluidLevel += FluidConstants.BUCKET;
                currentFluidType = fluidTypeInBucket; // Update the current fluid type
                this.setStack(fluidItemSlot, new ItemStack(Items.BUCKET)); // Replace fluid bucket with empty bucket
                markDirty();
                System.out.println("Fluid bucket processed. New Fluid Level: " + fluidLevel + ", Fluid Type: " + currentFluidType);
            } else {
                System.out.println("Fluid types do not match or tank is full. Fluid not transferred.");
            }
        }
    }


    public boolean getPoweredState() {
        return this.isPowered;
    }






    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Fuel Generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FuelGeneratorScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private void updateLitProperty() {
        if (world != null && !world.isClient) {
            BlockState state = world.getBlockState(getPos());
            if (state.getBlock() instanceof FuelGeneratorBlock) {
                boolean currentLitState = state.get(FuelGeneratorBlock.LIT);
                if (currentLitState != isRunning) {
                    world.setBlockState(getPos(), state.with(FuelGeneratorBlock.LIT, isRunning), 3);
                }
            }
        }
    }
    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
        updateLitProperty();
    }
}
