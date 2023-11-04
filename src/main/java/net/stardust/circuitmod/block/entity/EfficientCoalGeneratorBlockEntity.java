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
import net.minecraft.world.World;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class EfficientCoalGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    public EfficientCoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_BE, pos, state);
    }

    private static final int INPUT_SLOT = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);


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

    public boolean isFuel() {
        ItemStack stack = getItems().get(INPUT_SLOT);
        return !stack.isEmpty() && (stack.isOf(Items.COAL) || stack.isOf(Items.COAL_BLOCK));
    }

    public boolean isNoFuel() {
        return !isFuel();
    }
    private int tickCounter = 0;

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        tickCounter++;

    }
    public boolean consumeFuel() {
        ItemStack fuelStack = inventory.get(INPUT_SLOT);
        if (!fuelStack.isEmpty() && (fuelStack.isOf(Items.COAL) || fuelStack.isOf(Items.COAL_BLOCK))) {
            fuelStack.decrement(1);
            markDirty();
            return true;
        }
        return false;
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

    public boolean hasCoal() {
        ItemStack stackInSlot = inventory.get(INPUT_SLOT);
        return !stackInSlot.isEmpty() && stackInSlot.isOf(Items.COAL);
    }

    public boolean hasCoalBlock() {
        ItemStack stackInSlot = inventory.get(INPUT_SLOT);
        return !stackInSlot.isEmpty() && stackInSlot.isOf(Items.COAL_BLOCK);
    }

}
