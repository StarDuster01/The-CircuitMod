package net.stardust.circuitmod.block.entity.slave;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
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
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ImplementedInventory;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class EfficientCoalGeneratorInventorySlaveBlockEntity extends BlockEntity implements ImplementedInventory {
    public EfficientCoalGeneratorInventorySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE,pos, state);
    }

    // In EfficientCoalGeneratorInventorySlaveBlockEntity.java

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                  //  return (int) currentEnergy;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    // currentEnergy = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };
    private static final int INPUT_SLOT = 0;
    private int tickCounter = 0;


    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        tickCounter++;

    }
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);



    public BlockPos getMasterPos() {
        return this.masterPos;
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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        // Serialize inventory
        NbtCompound inventoryTag = new NbtCompound();
        Inventories.writeNbt(inventoryTag, inventory);
        nbt.put("Inventory", inventoryTag);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // Deserialize inventory
        NbtCompound inventoryTag = nbt.getCompound("Inventory");
        inventory = DefaultedList.ofSize(inventoryTag.getList("Items", 10).size(), ItemStack.EMPTY);
        Inventories.readNbt(inventoryTag, inventory);
    }

}
