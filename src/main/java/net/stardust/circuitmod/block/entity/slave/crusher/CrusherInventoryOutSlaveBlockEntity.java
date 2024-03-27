package net.stardust.circuitmod.block.entity.slave.crusher;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.ImplementedInventory;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class CrusherInventoryOutSlaveBlockEntity extends BlockEntity implements ImplementedInventory {
    public CrusherInventoryOutSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_INVENTORY_OUT_SLAVE_BE,pos, state);
    }
    private static final int INPUT_SLOT = 0;
    private int tickCounter = 0;


    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }


    public void tick(World world, BlockPos pos, BlockState state) {
        //System.out.println("Inventory Slave Block is Ticking");
        if (world == null || world.isClient) return;

           //TODO Put Logic to get items from the master if items are requested
    }
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);



    public BlockPos getMasterPos() {
        return this.masterPos;
    }


    public boolean transferItemsToMaster(World world) {
        if (world.isClient) return false;

        BlockEntity blockEntity = world.getBlockEntity(this.masterPos);
        if (!(blockEntity instanceof CrusherBlockEntity)) {
            return false;
        }

        CrusherBlockEntity master = (CrusherBlockEntity) blockEntity;

        // Attempt to transfer item from the first non-empty slot in the slave's inventory to the first slot in the master's inventory
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack itemStack = this.inventory.get(i);

            if (!itemStack.isEmpty() && isValid(INPUT_SLOT, itemStack)) {
                // Try to insert the item into the first slot of the master's inventory
                ItemStack remainder = master.tryInsertItem(itemStack.copy(), false);

                // Update the slave's inventory slot with the remainder
                this.inventory.set(i, remainder);
                markDirty(); // Mark the slave entity as dirty

                if (remainder.isEmpty()) {
                    // The entire stack was transferred
                    return true;
                } else {
                    // Part of the stack couldn't be transferred, exit the method
                    return false;
                }
            }
        }

        // No suitable items found for transfer
        return false;
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
