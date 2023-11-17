package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Map;

public class EfficientCoalGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE

    public EfficientCoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        this.energySlavePos = pos.offset(facing).up(2);
    }

    private static final int INPUT_SLOT = 0;
    private static final int FLUID_SLOT = 1;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public ItemStack getFuelItem() {
        return inventory.get(INPUT_SLOT);
    }
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Efficient Coal Generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EfficientCoalGeneratorScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    public ItemStack tryInsertItem(ItemStack stackToInsert, boolean simulate) {
        return insertItem(INPUT_SLOT, stackToInsert, simulate);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    private boolean isRunning = false;
    private static final int POWERED_INDEX = 1;
    /////////////////////// PROPERTY DELEGATE ////////////////////////
    private static final int RUNNING_INDEX = 2; // New index for isRunning

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return fuelLevel;
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


    ////////////////ALL ADDITIONAL ENERGY FUNCTION HERE //////////////////

    public boolean isFuel() {
        ItemStack stack = getItems().get(INPUT_SLOT);
        return !stack.isEmpty() && (stack.isOf(Items.COAL) || stack.isOf(Items.COAL_BLOCK));
    }
    private static final Map<Item, Integer> EFFICIENCY_VALUES = Map.of(
            Items.COAL, 100,
            Items.OAK_PLANKS, 50
            // Add other fuels as needed
    );
    public int getCurrentEfficiency() {
        ItemStack fuelStack = getFuelItem();
        return EFFICIENCY_VALUES.getOrDefault(fuelStack.getItem(), 0);
    }


    private int fuelLevel = 0;
    private BlockPos energySlavePos; // Position of the energy slave block entity
    private int lastEnergy = 0; // Tracks the last energy level for comparison

    private int tickCounter = 0;
    private static final int FLUID_LEVEL_INDEX = 3; // Assign an appropriate index
    private int fluidLevel = 0; // Add a field to store the fluid level
    private static final int FLUID_USAGE_INTERVAL = 20; // Number of ticks in one second
    private int fluidUsageCounter = 0;
    public void setEnergySlavePos(BlockPos pos) {
        this.energySlavePos = pos;
    }
    public void updateFluidLevel(int newFluidLevel) {
        this.fluidLevel = newFluidLevel;
        markDirty();
    }

    public int getFluidLevel() {
        return this.fluidLevel;
    }
    private int getCurrentEnergy() {
        if (energySlavePos != null) {
            BlockEntity be = world.getBlockEntity(energySlavePos);
            if (be instanceof EfficientCoalGeneratorEnergySlaveBlockEntity) {
                return (int)((EfficientCoalGeneratorEnergySlaveBlockEntity) be).getAmount();
            }
        }
        return 0;
    }

    // Update your tick method to decrease fuel level

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        int currentEnergy = getCurrentEnergy();
        isRunning = (currentEnergy != lastEnergy);
        lastEnergy = currentEnergy;
        fillUpOnFluid();

        if (isRunning) {
            fluidUsageCounter++;
            if (fluidUsageCounter >= FLUID_USAGE_INTERVAL) {
                if (fluidLevel > 0) {
                    fluidLevel -= 1; // Decrease fluid level by 1 every second
                }
                fluidUsageCounter = 0; // Reset the counter
            }
        } else {
            fluidUsageCounter = 0; // Reset the counter if not running
        }
        // Update the fluid level in the property delegate
        propertyDelegate.set(FLUID_LEVEL_INDEX, fluidLevel);
    }

    public void updateFuelLevel(int newFuelLevel) {
        this.fuelLevel = newFuelLevel;
        markDirty();
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

    //////////////// NBT DATA /////////////
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("FluidLevel", fluidLevel);
        Inventories.writeNbt(nbt, getItems());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fluidLevel = nbt.getInt("FluidLevel");
        Inventories.readNbt(nbt, getItems());
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
    public void updatePoweredState(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            markDirty();
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
        return this.getStack(fluidItemSlot).getItem() == Items.WATER_BUCKET;
    }

    private void transferItemFluidToTank(int fluidItemSlot) {
        if (fluidLevel <= 648000) {
            fluidLevel += FluidConstants.BUCKET;
            this.setStack(fluidItemSlot, new ItemStack(Items.BUCKET)); // Replace water bucket with empty bucket
            markDirty();
            System.out.println("Water bucket processed. New Fluid Level: " + fluidLevel);
        }
    }




    public boolean getPoweredState() {
        return this.isPowered;
    }


}
