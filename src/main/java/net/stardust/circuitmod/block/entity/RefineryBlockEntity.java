package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.block.custom.refinery.RefineryRedstoneSlaveBlock;
import net.stardust.circuitmod.block.entity.slave.refinery.RefineryEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.refinery.RefineryRedstoneSlaveBlockEntity;
import net.stardust.circuitmod.screen.RefineryScreenHandler;
import org.jetbrains.annotations.Nullable;
import net.minecraft.inventory.SidedInventory;

public class RefineryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IFluidConsumer, SidedInventory {
    private static final int[] INPUT_SLOTS = new int[]{0}; // Slot 0 for input
    private static final int[] OUTPUT_SLOTS = new int[]{1}; // Slot 1 for output
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY); // 2 slots: 0 (input), 1 (output)
    private int energy = 0;
    private int fluidLevel1 = 0;
    private int fluidLevel2 = 0;
    private String fluidType1 = null;
    private String fluidType2 = null;
    private boolean isPowered;

    private static final int MAX_ENERGY = 100000;
    private static final int FLUID_CAPACITY = 100000;

    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_BE, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Refinery");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RefineryScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        printInputSlotContents();

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        BlockPos redstoneSlavePos = pos.offset(facing.rotateYClockwise(), 1);
        BlockEntity redstoneSlaveEntity = world.getBlockEntity(redstoneSlavePos);

        if (redstoneSlaveEntity instanceof RefineryRedstoneSlaveBlockEntity) {
            RefineryRedstoneSlaveBlockEntity redstoneSlave = (RefineryRedstoneSlaveBlockEntity) redstoneSlaveEntity;
            isPowered = redstoneSlave.getCachedState().get(RefineryRedstoneSlaveBlock.POWERED);
        }

        if (isPowered) {
            BlockPos energySlavePos = pos.offset(facing.rotateYCounterclockwise(), 1);
            BlockEntity energySlaveEntity = world.getBlockEntity(energySlavePos);

            if (energySlaveEntity instanceof RefineryEnergySlaveBlockEntity) {
                RefineryEnergySlaveBlockEntity energySlave = (RefineryEnergySlaveBlockEntity) energySlaveEntity;
                energySlave.reduceEnergy(100);
                this.energy = energySlave.getDirectEnergy();
                markDirty();
            }
        }
    }

    public void updatePoweredState(boolean powered) {
        this.isPowered = powered;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Energy", energy);
        nbt.putInt("FluidLevel1", fluidLevel1);
        nbt.putInt("FluidLevel2", fluidLevel2);
        nbt.putBoolean("IsPowered", isPowered);
        if (fluidType1 != null) {
            nbt.putString("FluidType1", fluidType1);
        }
        if (fluidType2 != null) {
            nbt.putString("FluidType2", fluidType2);
        }
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        energy = nbt.getInt("Energy");
        fluidLevel1 = nbt.getInt("FluidLevel1");
        fluidLevel2 = nbt.getInt("FluidLevel2");
        isPowered = nbt.getBoolean("IsPowered");
        fluidType1 = nbt.contains("FluidType1") ? nbt.getString("FluidType1") : null;
        fluidType2 = nbt.contains("FluidType2") ? nbt.getString("FluidType2") : null;
        Inventories.readNbt(nbt, inventory);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    public int getOutputFluidLevel() {
        return 0; // TODO: Implement this method
    }

    public int getFluidLevel() {
        return 0; // TODO: Implement this method
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return OUTPUT_SLOTS;
        } else {
            return INPUT_SLOTS;
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN && slot == INPUT_SLOTS[0]; // Allow insertion only in input slot from sides
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == OUTPUT_SLOTS[0]; // Allow extraction only from output slot at bottom
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    public boolean canInsert(int slot, ItemStack stack) {
        // Define conditions for insertion here
        return slot == INPUT_SLOTS[0]; // Example condition
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        System.out.println("Item added to RefineryBlockEntity at slot " + slot + ": " + stack);
        printInputSlotContents();
        this.markDirty();
    }

    private void printInputSlotContents() {
        ItemStack inputStack = this.inventory.get(INPUT_SLOTS[0]);
        System.out.println("Current contents of input slot: " + inputStack);
    }

    @Override
    public void addFluid(int fluidAmount, String fluidType) {
        if (fluidType1 == null || fluidType1.equals(fluidType)) {
            fluidType1 = fluidType;
            fluidLevel1 += fluidAmount;
            if (fluidLevel1 > FLUID_CAPACITY) {
                fluidLevel1 = FLUID_CAPACITY;
            }
        } else if (fluidType2 == null || fluidType2.equals(fluidType)) {
            fluidType2 = fluidType;
            fluidLevel2 += fluidAmount;
            if (fluidLevel2 > FLUID_CAPACITY) {
                fluidLevel2 = FLUID_CAPACITY;
            }
        } else {
            System.out.println("Cannot add fluid. Both fluid tanks are occupied by different types.");
        }
        markDirty();
    }

    @Override
    public boolean canReceiveFluid(String fluidType) {
        return (fluidType1 == null || fluidType1.equals(fluidType) || fluidType2 == null || fluidType2.equals(fluidType))
                && (fluidLevel1 < FLUID_CAPACITY || fluidLevel2 < FLUID_CAPACITY);
    }
}
