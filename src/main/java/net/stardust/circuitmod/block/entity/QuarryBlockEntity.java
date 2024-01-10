package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.entity.ModEntities;
import net.stardust.circuitmod.block.custom.QuarryBlock;
import net.stardust.circuitmod.screen.QuarryScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;
import net.stardust.circuitmod.networking.ModMessages;

import java.util.List;

public class QuarryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IEnergyConsumer {

    protected final PropertyDelegate propertyDelegate;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    private Vector2i miningAreaDimensions = new Vector2i(4, 4);
    private boolean isMiningActive = false;
    private long energyStored = 0;
    public static final int ENERGY_PER_BLOCK = 500;
    private static final int MAX_ENERGY = 100000;
    private int chestSearchRadius = 5;


    public void setMiningAreaDimensions(Vector2i vec2i) {
        this.miningAreaDimensions = vec2i;
        markDirty();
    }

    public static int getEnergyPerBlock() {
        return ENERGY_PER_BLOCK;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new QuarryScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }


    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUARRY_BLOCK_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 0)
                    return (int) energyStored;
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0)
                    energyStored = value;

            }

            @Override
            public int size() {
                return 1;
            }
        };

        this.currentMiningY = pos.getY() - 1;
    }

    private int tickCounter = 0;

    private int currentMiningY;

    public boolean isMiningActive() {
        markDirty();
        return isMiningActive;
    }

    public void setMiningActive(boolean miningActive) {
        isMiningActive = miningActive;
        markDirty();
    }

    public boolean tryInsertIntoNeighboringChests(ItemStack itemStack) {
        for (int dx = -chestSearchRadius; dx <= chestSearchRadius; dx++) {
            for (int dy = -chestSearchRadius; dy <= chestSearchRadius; dy++) {
                for (int dz = -chestSearchRadius; dz <= chestSearchRadius; dz++) {
                    BlockPos currentPos = pos.add(dx, dy, dz);
                    BlockState currentState = world.getBlockState(currentPos);
                    if (currentState.getBlock() instanceof ChestBlock) {
                        ChestBlockEntity chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(currentPos);
                        if (chestBlockEntity != null) {
                            ChestType chestType = currentState.get(ChestBlock.CHEST_TYPE);
                            if (chestType != ChestType.SINGLE) {
                                BlockPos otherHalfPos = currentPos.offset(ChestBlock.getFacing(currentState));
                                ChestBlockEntity otherHalf = (ChestBlockEntity) world.getBlockEntity(otherHalfPos);
                                if (otherHalf != null) {
                                    DoubleInventory doubleInventory = new DoubleInventory(chestBlockEntity, otherHalf);
                                    if (tryInsertIntoInventory(doubleInventory, itemStack)) {
                                        return true;
                                    }
                                }
                            } else {
                                if (tryInsertIntoInventory(chestBlockEntity, itemStack)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Helper method to handle inserting item into an Inventory
    private boolean tryInsertIntoInventory(net.minecraft.inventory.Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stackInSlot = inventory.getStack(i);
            if (stackInSlot.isEmpty()) {
                inventory.setStack(i, itemStack.copy());
                inventory.markDirty();
                return true;
            } else if (ItemStack.canCombine(stackInSlot, itemStack)) {
                int spaceLeft = stackInSlot.getMaxCount() - stackInSlot.getCount();
                if (spaceLeft >= itemStack.getCount()) {
                    stackInSlot.increment(itemStack.getCount());
                    inventory.markDirty();
                    return true;
                } else if (spaceLeft > 0) {
                    stackInSlot.increment(spaceLeft);
                    itemStack.decrement(spaceLeft);
                    inventory.markDirty();
                }
            }
        }
        return false;
    }
    public void mineBlocks() {
        World currentWorld = this.getWorld();
        if (currentWorld == null) return;
        if (currentWorld.isClient) return;

        Direction facing = getCachedState().get(QuarryBlock.FACING);


        int width = miningAreaDimensions.x;
        int depth = miningAreaDimensions.y;
        BlockPos start;
        BlockPos end;

        switch (facing) {
            case NORTH:
                start = pos.add(-width / 2, currentMiningY, 1);
                end = pos.add(width / 2, currentMiningY, depth);
                break;
            case SOUTH:
                start = pos.add(-width / 2, currentMiningY, -depth);
                end = pos.add(width / 2, currentMiningY, -1);
                break;
            case WEST:
                start = pos.add(1, currentMiningY, -width / 2);
                end = pos.add(depth, currentMiningY, width / 2);
                break;
            case EAST:
                start = pos.add(-depth, currentMiningY, -width / 2);
                end = pos.add(-1, currentMiningY, width / 2);
                break;
            default:
                return;  // Return early for invalid facing directions
        }

        boolean allBlocksMined = true;
        for (BlockPos currentPos : BlockPos.iterate(start, end)) {
            BlockState state = world.getBlockState(currentPos);


            if (canBreak(state, currentPos) && hasEnoughEnergy()) {
                mineBlock(currentPos, state);
            } else if (canBreak(state, currentPos)) {
                allBlocksMined = false;
            }
        }
        if (allBlocksMined) this.currentMiningY--;
    }

    public boolean canBreak(BlockState state, BlockPos pos) {
        Block block = state.getBlock();
        return block != Blocks.BEDROCK
                && block != ModBlocks.QUARRY_BLOCK
                && block != ModBlocks.EFFICIENT_COAL_GENERATOR_BLOCK
                && block != ModBlocks.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK
                && block != Blocks.CHEST
                && block != Blocks.ENDER_CHEST
                && block != Blocks.BARREL
                && block != Blocks.BEACON
                && block != Blocks.BARRIER
                && !(block instanceof net.minecraft.block.FluidBlock) // Allows to break liquid blocks
                && !state.isAir();
    }
    public void validateChestConnections() {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof ChestBlock) {
            }
        }
    }





    private void mineBlock(BlockPos pos, BlockState state) {

        consumeEnergy(ENERGY_PER_BLOCK);

        List<ItemStack> drops = Block.getDroppedStacks(state, (ServerWorld) world, pos, world.getBlockEntity(pos));
        for (ItemStack drop : drops) {
            boolean inserted = insertItem(drop);
            if (!inserted) {
                inserted = tryInsertIntoNeighboringChests(drop);
                if (!inserted) {

                }
            }
        }
        world.removeBlock(pos, false);
        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
        markDirty();
    }
    private boolean insertItem(ItemStack itemStack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stackInSlot = inventory.get(i);
            if (stackInSlot.isEmpty()) {
                inventory.set(i, itemStack.copy());
                markDirty();
                return true;
            } else if (ItemStack.canCombine(stackInSlot, itemStack)) {
                int spaceLeft = stackInSlot.getMaxCount() - stackInSlot.getCount();
                if (spaceLeft >= itemStack.getCount()) {
                    stackInSlot.increment(itemStack.getCount());
                    markDirty();
                    return true;
                } else if (spaceLeft > 0) {
                    stackInSlot.increment(spaceLeft);
                    itemStack.decrement(spaceLeft);
                    markDirty();
                }
            }
        }
        if(itemStack.getCount() > 0) {
            for(int i = 0; i < inventory.size(); i++) {
                if(inventory.get(i).isEmpty()) {
                    inventory.set(i, itemStack.copy());
                    markDirty();
                    return true;
                }
            }
        }
        return false;
    }


 ////////// ENERGY INTERFACE CODE //////////
 private void consumeEnergy(long amount) {
     this.energyStored = Math.max(this.energyStored - amount, 0);
 }

    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY; // Cap the energy at the maximum limit
        }
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }
    public long getEnergyStored() {
        return this.energyStored;
    }
    public void setEnergyStored(long energy) {
        this.energyStored = energy;
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }

    private boolean hasEnoughEnergy() {
        return this.energyStored >= ENERGY_PER_BLOCK;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        System.out.println("Quarry energy: " + this.energyStored);

        Vector2i dimensions = this.getMiningAreaDimensions();
        tickCounter++;
        if(!world.isClient) {
            validateChestConnections();
            for (PlayerEntity playerEntity : world.getPlayers()) {
                if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20*20) {
                    ModMessages.sendQuarryUpdate((ServerPlayerEntity) playerEntity, pos, energyStored, isMiningActive);
                    ModMessages.sendQuarryAreaUpdate((ServerPlayerEntity) playerEntity, pos, dimensions);
                }
            }

        }
        if (isMiningActive && this.energyStored >= ENERGY_PER_BLOCK) {
            mineBlocks();
            markDirty();
        }
    }


    public Vector2i getMiningAreaDimensions() {
        return miningAreaDimensions;

    }

    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    @Override
    public Text getDisplayName() {
        return Text.literal("Quarry");
    }



    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("quarry.energy", this.energyStored);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        if(nbt.contains("quarry.energy")) {
            this.energyStored = nbt.getLong("quarry.energy");
        }
    }

}
