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
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_INVENTORY_SLAVE_BE,pos, state);
    }

    // In EfficientCoalGeneratorInventorySlaveBlockEntity.java
    private static final int INPUT_SLOT = 0;
    private int tickCounter = 0;


    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    @Override
    public boolean isValid(int slot, ItemStack stack) {
        // Check if the item is either coal or a coal block
        return stack.isOf(Items.COAL) || stack.isOf(Items.COAL_BLOCK);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        //System.out.println("Inventory Slave Block is Ticking");
        if (world == null || world.isClient) return;
            transferItemsToMaster(world);
    }
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);



    public BlockPos getMasterPos() {
        return this.masterPos;
    }


    public boolean transferItemsToMaster(World world) {
        if (world.isClient) return false;

        //System.out.println("Transfer attempt: " + world.getTime() + " at position " + this.pos);

        BlockEntity blockEntity = world.getBlockEntity(this.masterPos);
        if (!(blockEntity instanceof EfficientCoalGeneratorBlockEntity)) {
            //System.out.println("Transfer failed: Master block entity is not an instance of EfficientCoalGeneratorBlockEntity");
            return false;
        }

        EfficientCoalGeneratorBlockEntity master = (EfficientCoalGeneratorBlockEntity) blockEntity;

        // Iterate through the slave's inventory
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack itemStack = this.inventory.get(i);

            if (!itemStack.isEmpty() && isValid(INPUT_SLOT, itemStack)) { // Check if the stack is valid
                //System.out.println("Attempting to transfer: " + itemStack);

                // Try to insert into the master and get the remainder
                ItemStack remainder = master.tryInsertItem(itemStack.copy(), false);

                //System.out.println("Remainder after transfer: " + remainder);

                // Update the slave's inventory slot with the remainder
                this.inventory.set(i, remainder);
                markDirty(); // Mark the slave entity as dirty

                if (remainder.isEmpty()) {
                    // The entire stack was transferred, proceed to next slot
                    continue;
                } else {
                    // Part of the stack (or all of it) couldn't be transferred, exit the method
                   // System.out.println("Transfer incomplete: Stack could not be fully transferred.");
                    return false;
                }
            } else if (!itemStack.isEmpty()) {
                //System.out.println("Item in slot " + i + " is not valid for transfer: " + itemStack);
            }
        }

        //System.out.println("Transfer successful: All items transferred.");
        return true; // All items were transferred successfully
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
        if (this.masterPos != null) {
            nbt.putInt("MasterPosX", this.masterPos.getX());
            nbt.putInt("MasterPosY", this.masterPos.getY());
            nbt.putInt("MasterPosZ", this.masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // Deserialize inventory
        NbtCompound inventoryTag = nbt.getCompound("Inventory");
        inventory = DefaultedList.ofSize(inventoryTag.getList("Items", 10).size(), ItemStack.EMPTY);
        Inventories.readNbt(inventoryTag, inventory);
        if (nbt.contains("MasterPosX")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            this.masterPos = new BlockPos(x, y, z);
        }
    }

}
