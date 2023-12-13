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

import net.stardust.circuitmod.block.custom.FuelGeneratorBlock;

import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.screen.FuelGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FuelGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE

    private static final int INPUT_SLOT = 0;
    private static final int FLUID_SLOT = 1;
    private BlockPos energySlavePos; // Position of the energy slave block entity
    private static final int POWERED_INDEX = 1;
    private int fuelLevel = 0;
    private static final int RUNNING_INDEX = 2; // New index for isRunning
    private int fluidLevel = 0; // Add a field to store the fluid level
    private static final int FLUID_USAGE_INTERVAL = 20; // Number of ticks in one second
    private int fluidUsageCounter = 0;

    private static final int FLUID_LEVEL_INDEX = 3; // Assign an appropriate index
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private boolean isRunning = false;
    public ItemStack getFuelItem() {
        return inventory.get(INPUT_SLOT);
    }

    public FuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUEL_GENERATOR_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        this.energySlavePos = pos.offset(facing).up(2);
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
        // Add the custom logic here soon

    }

    public boolean isFuel() {
        ItemStack stack = getItems().get(INPUT_SLOT);
        return !stack.isEmpty() && (
                stack.isOf(Items.COAL)

        );
    }

    private static final Map<Item, Integer> EFFICIENCY_VALUES = Map.ofEntries(
            Map.entry(Items.COAL, 80)
    );
    public int getCurrentEfficiency() {
        ItemStack fuelStack = getFuelItem();
        return EFFICIENCY_VALUES.getOrDefault(fuelStack.getItem(), 0);
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
    private boolean hasFluidSourceItemInFluidSlot(int fluidItemSlot) {
        return this.getStack(fluidItemSlot).getItem() == ModFluids.CRUDE_OIL_BUCKET; // Change this to fuel later
    }
    private void transferItemFluidToTank(int fluidItemSlot) {
        if (fluidLevel <= 648000) {
            fluidLevel += FluidConstants.BUCKET;
            this.setStack(fluidItemSlot, new ItemStack(Items.BUCKET)); // Replace oil bucket with empty bucket
            markDirty();
            System.out.println("Oil bucket processed. New Fluid Level: " + fluidLevel);
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
