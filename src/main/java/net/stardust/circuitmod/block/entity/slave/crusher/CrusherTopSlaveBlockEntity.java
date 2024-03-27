package net.stardust.circuitmod.block.entity.slave.crusher;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.AbstractTechSlaveBlockEntity;
import net.stardust.circuitmod.screen.CrusherScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrusherTopSlaveBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public CrusherTopSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_TOP_SLAVE_BE,pos, state);
    }
    private static final int INPUT_SLOT = 0;
    private int tickCounter = 0;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    public BlockPos getMasterPos() {
        return this.masterPos;
    }


    private BlockPos masterPos;
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

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

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

    public void tick(World world1, BlockPos pos, BlockState state1) {
        if (this.world == null || this.world.isClient) return;

        tickCounter++;
        if (tickCounter % 20 == 0) { // Perform the check every second
          //  System.out.println("Tick method called, checking for items...");

            // Expand the detection box to cover more area around the top of the block entity
            Box detectionBox = new Box(this.pos.up()).expand(1, 0.5, 1);
          //  System.out.println("Detection Box Coordinates: " + detectionBox);

            // Use the world object to find entities within the box
            List<ItemEntity> items = this.world.getEntitiesByClass(ItemEntity.class, detectionBox, entity -> true);

            if(items.isEmpty()) {
             //   System.out.println("No items detected.");
            }

            for (ItemEntity itemEntity : items) {
                ItemStack itemStack = itemEntity.getStack();
             //   System.out.println("Detected item: " + itemStack.getItem().toString() + " x" + itemStack.getCount());

                // Implement your logic to check if the item can be added to the master's inventory
                boolean added = tryToAddToMastersInventory(itemStack);

                if (added) {
                    itemEntity.discard(); // Remove the item entity from the world if successfully added
                }
            }
        }
    }


    private boolean tryToAddToMastersInventory(ItemStack itemStack) {
      //  System.out.println("tryToAddToMastersInventory called with item: " + itemStack.getItem().toString() + " x" + itemStack.getCount());

        assert this.world != null;
        BlockEntity blockEntity = this.world.getBlockEntity(masterPos);

        if (!(blockEntity instanceof CrusherBlockEntity)) {
         //   System.out.println("Block entity is not an instance of CrusherBlockEntity.");
            return false;
        }
        if ((blockEntity instanceof CrusherBlockEntity)) {
        //    System.out.println("Master Entity IS the right kind");
        }

        CrusherBlockEntity master = (CrusherBlockEntity) blockEntity;
        DefaultedList<ItemStack> inventory = master.getItems();
      //  System.out.println("Master inventory accessed.");

        // Focus on the 0th slot
        ItemStack slot = inventory.get(0);
     //   System.out.println("Slot 0 contains: " + slot.getItem().toString() + " x" + slot.getCount());

        if (slot.isEmpty()) {
       //    System.out.println("Slot 0 is empty, adding item: " + itemStack.getItem().toString() + " x" + itemStack.getCount());
            inventory.set(0, itemStack.copy());
            itemStack.setCount(0); // Empty the original stack
            master.markDirty();
        //    System.out.println("Item added successfully to slot 0.");
            return true;
        } else if (ItemStack.canCombine(slot, itemStack) && slot.getCount() < slot.getMaxCount()) {
            int transferAmount = Math.min(itemStack.getCount(), slot.getMaxCount() - slot.getCount());
         //   System.out.println("Combining stacks. Transfer amount: " + transferAmount);
            slot.increment(transferAmount);
            itemStack.decrement(transferAmount);
            master.markDirty(); // Ensure changes are saved and synchronized
          //  System.out.println("Items combined successfully. Remaining items: " + itemStack.getCount());

            if (itemStack.isEmpty()) {
                return true;
            }
        } else {
         //   System.out.println("Item cannot be added or combined in slot 0.");
        }

        return false;
    }






    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        // Serialize inventory
        NbtCompound inventoryTag = new NbtCompound();
        Inventories.writeNbt(inventoryTag, inventory);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
        nbt.put("Inventory", inventoryTag);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // Deserialize inventory
        if (nbt.contains("MasterPosX") && nbt.contains("MasterPosY") && nbt.contains("MasterPosZ")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            masterPos = new BlockPos(x, y, z);
        }
        NbtCompound inventoryTag = nbt.getCompound("Inventory");
        inventory = DefaultedList.ofSize(inventoryTag.getList("Items", 10).size(), ItemStack.EMPTY);
        Inventories.readNbt(inventoryTag, inventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {

    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Fuel Generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CrusherScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }


}

