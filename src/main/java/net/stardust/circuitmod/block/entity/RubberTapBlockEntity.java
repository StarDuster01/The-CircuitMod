package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
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
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.RubberLog;
import net.stardust.circuitmod.block.custom.RubberTapBlock;
import net.stardust.circuitmod.screen.RubberTapScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class RubberTapBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private boolean isPowered;

    private static final int ADD_INTERVAL = 120; // 60 seconds * 20 ticks
    private int tickCounter = 0;

    public RubberTapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUBBER_TAP_BE, pos, state);
    }
    private static final int INPUT_SLOT = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Rubber Tap");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RubberTapScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }


    /////////////////////// PROPERTY DELEGATE ////////////////////////
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:

                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:

                    break;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    ////////////////ALL ADDITIONAL ENERGY FUNCTION HERE //////////////////





    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        tickCounter++;

        System.out.println("Tick: " + tickCounter); // Print current tick count

        if (state.contains(RubberTapBlock.FACING)) {
            Direction facing = state.get(RubberTapBlock.FACING);
            BlockState oppositeBlockState = world.getBlockState(pos.offset(facing.getOpposite()));

            if (oppositeBlockState.isOf(ModBlocks.RUBBER_LOG) && oppositeBlockState.get(RubberLog.NATURAL)) {
                int fillLevel = getFillLevel();
                world.setBlockState(pos, state.with(RubberTapBlock.FILL_LEVEL, fillLevel));
                System.out.println("Fill Level: " + fillLevel); // Print current fill level

                if (tickCounter >= ADD_INTERVAL) {
                    System.out.println("Adding Diamond"); // Print when adding a diamond
                    addItemToInventory(new ItemStack(Items.DIAMOND));
                    tickCounter = 0;
                }
            }
        }
    }
    private int getFillLevel() {
        if (tickCounter < ADD_INTERVAL / 3) {
            return 1;
        } else if (tickCounter < 2 * ADD_INTERVAL / 3) {
            return 2;
        } else {
            return 3;
        }
    }



    private void addItemToInventory(ItemStack itemStack) {
        System.out.println("Inserting Item: " + itemStack); // Print item being inserted
        insertItem(INPUT_SLOT, itemStack, false);
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
        Inventories.writeNbt(nbt, getItems());

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
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

}
