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
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.screen.slot.LiquidFuelSlot;
import net.stardust.circuitmod.screen.slot.LubeSlot;


public class CrusherScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    final CrusherBlockEntity blockEntity;

    public CrusherBlockEntity getBlockEntity() {
        return blockEntity;
    }


    public CrusherScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(1));
    }
    public CrusherScreenHandler(int syncId, PlayerInventory playerInventory,
                                BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.CRUSHER_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 7);
        this.inventory = (Inventory)blockEntity;
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((CrusherBlockEntity) blockEntity);
        this.addSlot(new Slot(inventory, 0, 44, 18)); // Input

        this.addSlot(new Slot(inventory, 1, 62+72, 18)); // Output 1
        this.addSlot(new Slot(inventory, 2, 80+72, 18)); // Output 2
        this.addSlot(new Slot(inventory, 3, 62+72, 36)); // Output 3
        this.addSlot(new Slot(inventory, 4, 80+72, 36)); // Output 4
        this.addSlot(new Slot(inventory, 5, 62+72, 54)); // Output 5
        this.addSlot(new Slot(inventory, 6, 80+72, 54)); // Output 6

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
    }
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
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



