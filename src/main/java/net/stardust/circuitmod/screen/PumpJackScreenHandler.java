package net.stardust.circuitmod.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.stardust.circuitmod.block.entity.PCBStationBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;


public class PumpJackScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    final PumpJackBlockEntity blockEntity;

    public PumpJackBlockEntity getBlockEntity() {

        return blockEntity;
    }


    public PumpJackScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(3));
    }

    public PumpJackScreenHandler(int syncId, PlayerInventory playerInventory,
                                 BlockEntity blockEntity, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.PUMP_JACK_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 4);
        this.inventory = (Inventory) blockEntity;
        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((PumpJackBlockEntity) blockEntity);

        // Align a row of 8 slots for the PCB Station
        int slotsPerRow = 9;
        int startX = 8; // Adjust the starting X coordinate as needed
        int yPosition = 18; // Adjust the Y position as needed to align with your GUI

        // Create a row of slots
        for (int i = 0; i < slotsPerRow; i++) {
            if (i == 0) {
                // Add an instance of CoalSlot for slot 0
                this.addSlot(new Slot(inventory, i, startX + i * 18, yPosition));
            } else if (i == 2) {
                // Add an instance of DiamondSlot for slot 2
                this.addSlot(new Slot(inventory, i, startX + i * 18, yPosition));
            } else {
                // Add a regular Slot for all other slots
                this.addSlot(new Slot(inventory, i, startX + i * 18, yPosition));
            }
        }

        this.addSlot(new Slot(inventory, 9, 44 , 49));
        this.addSlot(new Slot(inventory, 10, 44+18 , 49));
        this.addSlot(new Slot(inventory, 11, 116 , 49));



        // Keep the rest of the player inventory and hotbar setup unchanged
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(propertyDelegate);
    }




    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }



/////////////////////////// COPY PASTA TO ALLOW SHIFT CLICKING //////////////////
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    //////////// END COPY PASTA /////////////////
}



