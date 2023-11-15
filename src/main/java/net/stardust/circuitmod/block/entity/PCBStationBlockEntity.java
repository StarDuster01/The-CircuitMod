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
import org.jetbrains.annotations.Nullable;
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
       // System.out.println("Tick method called for block at " + pos);

        // This method should only run on the server side.
        if (world.isClient()) {
            System.out.println("World is client-side, returning.");
            return;
        }

        // Ensure this cast is safe before proceeding.
        if (!(world instanceof ServerWorld serverWorld)) {
            System.out.println("World is not a ServerWorld, returning.");
            return;
        }

        System.out.println("Server world cast successful.");

        DynamicRegistryManager registryManager = serverWorld.getRegistryManager();
        RecipeManager recipeManager = world.getRecipeManager();
        Inventory inventoryWrapper = new PCBStationInventory(blockEntity.inventory);

        for (int i = 0; i < blockEntity.inventory.size(); i++) {
            inventoryWrapper.setStack(i, blockEntity.inventory.get(i));
        }

        System.out.println("Inventory wrapper set up.");

        Optional<PCBStationRecipe> match = recipeManager.getFirstMatch(PCBStationRecipe.Type.INSTANCE, inventoryWrapper, world);

        if (match.isEmpty()) {
            System.out.println("No matching recipe found, returning.");
            return;
        }

     //   System.out.println("Recipe match found.");

        PCBStationRecipe recipe = match.get();
        ItemStack output = recipe.craft(inventoryWrapper, registryManager);

        System.out.println("Crafted output: " + output);

        ItemStack currentOutput = blockEntity.inventory.get(OUTPUT_SLOT);

        if (currentOutput.isEmpty()) {
            System.out.println("Output slot is empty, setting crafted item to output slot.");
            blockEntity.inventory.set(OUTPUT_SLOT, output.copy());
            consumeInputs(inventoryWrapper);
        } else if (ItemStack.areItemsEqual(currentOutput, output) && ItemStack.areItemsEqual(currentOutput, output)) {
            int newCount = currentOutput.getCount() + output.getCount();
            if (currentOutput.getMaxCount() >= newCount) {
                System.out.println("Increasing stack count of output slot item.");
                currentOutput.increment(output.getCount());
                consumeInputs(inventoryWrapper);
            } else {
                System.out.println("Output slot cannot accept more items, no action taken.");
            }
        } else {
            System.out.println("Output slot has a different item, no action taken.");
        }

        blockEntity.markDirty();
        System.out.println("Block entity marked dirty.");
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
    }


    private void consumeInputs(Inventory inventory) {
        // Consume the inputs required for the recipe.
        // The exact implementation here will depend on your recipe system and which slots should be consumed.
        for (int i = 0; i < inventory.size() - 1; i++) { // Assuming the last slot is the output slot.
            ItemStack stackInSlot = inventory.getStack(i);
            if (!stackInSlot.isEmpty()) {
                // Decrease stack size by one (or by the amount required by your recipe).
                stackInSlot.decrement(1);
            }
        }
        // Mark inventory as dirty if needed. This may depend on your `markDirty` implementation.
        if (inventory instanceof PCBStationInventory) {
            ((PCBStationInventory)inventory).markDirty();
        }
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
    }


