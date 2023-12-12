package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.recipe.PCBStationRecipe;
import net.stardust.circuitmod.screen.PCBStationScreenHandler;
import net.stardust.circuitmod.screen.slot.RecipeSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PCBStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    protected final PropertyDelegate propertyDelegate;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);
    private static final int SLOT_0 = 0;
    private static final int SLOT_1 = 1;
    private static final int SLOT_2 = 2;
    private static final int SLOT_3 = 3;
    private static final int SLOT_4 = 4;
    private static final int SLOT_5 = 5;
    private static final int SLOT_6 = 6;
    private static final int SLOT_7 = 7;
    private static final int SLOT_8 = 8;
    // row 1
    private static final int SLOT_9 = 9;
    private static final int SLOT_10 = 10;
    private static final int OUTPUT_SLOT = 11;

    public PCBStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PCBSTATION_BLOCK_BE, pos, state);


        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {

                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {

                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }


    public void tick(World world, BlockPos pos, BlockState state, PCBStationBlockEntity blockEntity) {

    }



    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    @Override
    public Text getDisplayName() {
        return Text.literal("PCBStation");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PCBStationScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);

    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        }

        //////////////////// RECIPIE LOGIC ////////////////////

    private final List<RecipeSlot> recipe1 = List.of(
                        // MAIN ROW ON TOP LEFT TO RIGHT
            new RecipeSlot(Items.DIAMOND, 1, false), //slot 0
            new RecipeSlot(Items.DIAMOND, 2, false), //slot 1
            new RecipeSlot(Items.DIAMOND, 3, false), //slot 2
            new RecipeSlot(Items.DIAMOND, 4, false), //slot 3
            new RecipeSlot(Items.DIAMOND, 5, false), //slot 4
            new RecipeSlot(Items.DIAMOND, 6, false), //slot 5
            new RecipeSlot(Items.DIAMOND, 7, false), //slot 6
            new RecipeSlot(Items.DIAMOND, 8, false), //slot 7
            new RecipeSlot(Items.DIAMOND, 9, false), //slot 8
                        // TWO PATTERN SLOTS LEFT OF OUTPUT
            new RecipeSlot(Items.DIAMOND, 10, false), //slot 9
            new RecipeSlot(Items.DIAMOND, 11, false), // slot 10
                            // OUTPUT MUST HAVE TRUE AS LAST ARGUMENT
            new RecipeSlot(Items.NETHER_STAR, 1, true)

    );
    private final List<RecipeSlot> recipe2 = List.of(
            new RecipeSlot(Items.COAL, 1, false),
            new RecipeSlot(Items.COAL, 2, false),
            new RecipeSlot(Items.COAL, 3, false),
            new RecipeSlot(Items.COAL, 4, false),
            new RecipeSlot(Items.COAL, 5, false),
            new RecipeSlot(Items.COAL, 6, false),
            new RecipeSlot(Items.COAL, 7, false),
            new RecipeSlot(Items.COAL, 8, false),
            new RecipeSlot(Items.COAL, 9, false),
            new RecipeSlot(null, 10, false),
            new RecipeSlot(Items.NETHER_STAR, 1, true)

    );
    public void attemptCraft() {
        if (!world.isClient()) {
            if (canCraftRecipe(recipe1)) {
                doRecipe(recipe1);
            } else if (canCraftRecipe(recipe2)) {
                doRecipe(recipe2);
            }
        }
    }

    private boolean canCraftRecipe(List<RecipeSlot> recipe) {
        for (RecipeSlot slot : recipe) {
            if (!slot.isOutput) {
                ItemStack stackInSlot = inventory.get(recipe.indexOf(slot));
                if (!stackInSlot.isOf(slot.item) || stackInSlot.getCount() != slot.count) {
                    return false; // Return false if any input slot does not match
                }
            }
        }
        return true; // Return true if all input slots match
    }
    private void doRecipe(List<RecipeSlot> recipe) {
        // Check if all input slots are correct
        for (RecipeSlot slot : recipe) {
            if (!slot.isOutput) {
                ItemStack stackInSlot = inventory.get(recipe.indexOf(slot));
                if (!stackInSlot.isOf(slot.item) || stackInSlot.getCount() != slot.count) {
                    return; // Return early if any input slot does not match
                }
            }
        }

        // Craft the item
        for (RecipeSlot slot : recipe) {
            if (!slot.isOutput) {
                inventory.get(recipe.indexOf(slot)).decrement(slot.count);
            } else {
                inventory.set(OUTPUT_SLOT, new ItemStack(slot.item, slot.count)); // Set output
            }
        }
    }


}


