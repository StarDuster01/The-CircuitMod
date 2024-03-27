package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.slave.crusher.CrusherBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.crusher.CrusherEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.crusher.CrusherTopSlaveBlock;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherRedstoneSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherTopSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrusherBlock extends BlockWithEntity implements BlockEntityProvider {

    public CrusherBlock(Settings settings) {
        super(settings.luminance((state)->10));
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing);

        // New logic for checking placement validity
        List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, facing);
        boolean areaClear = slaveBlockPositions.stream().allMatch(blockPos ->
                world.isAir(blockPos) || world.getBlockState(blockPos).canReplace(ctx)
        );

        if (!world.isClient && !areaClear) {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                player.sendMessage(Text.literal("The area is not clear for the Crusher. Please clear any obstructing blocks."), false);
            }
            return null; // Cancel the block placement by returning null
        }

        if (!world.isClient) {
            BlockPos energySlavePos = pos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYCounterclockwise());
            BlockPos redstoneSlavePos = pos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYClockwise());

            BlockPos topSlavePos1 = pos.up();
            BlockPos topSlavePos2 = pos.up().offset(facing);
            BlockPos topSlavePos3 = pos.up().offset(facing.rotateYCounterclockwise());
            BlockPos topSlavePos4 = pos.up().offset(facing.rotateYClockwise());
            BlockPos topSlavePos5 = pos.up().offset(facing.getOpposite());

            BlockPos topSlavePos6 = pos.up().offset(facing).offset(facing.rotateYCounterclockwise());
            BlockPos topSlavePos7 = pos.up().offset(facing).offset(facing.rotateYClockwise());
            BlockPos topSlavePos8 = pos.up().offset(facing.getOpposite()).offset(facing.rotateYCounterclockwise());
            BlockPos topSlavePos9 = pos.up().offset(facing.getOpposite()).offset(facing.rotateYClockwise());


            // Place Slave Blocks Here
            placeEnergySlaveBlock(world, ctx, energySlavePos, pos);
            placeRedstoneSlaveBlocks(world, ctx, redstoneSlavePos, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos1, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos2, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos3, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos4, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos5, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos6, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos7, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos8, pos);
            placeTopSlaveBlocks(world, ctx, topSlavePos9, pos);


        }
        return state;
    }
    private List<BlockPos> calculateSlaveBlockPositions(BlockPos masterPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();

        // Energy and Redstone Slave Blocks
        positions.add(masterPos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYCounterclockwise()));
        positions.add(masterPos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYClockwise()));

        // Top Slave Blocks (including diagonals)
        positions.add(masterPos.up()); // Directly above master
        positions.add(masterPos.up().offset(facing)); // Front
        positions.add(masterPos.up().offset(facing.rotateYCounterclockwise())); // Left
        positions.add(masterPos.up().offset(facing.rotateYClockwise())); // Right
        positions.add(masterPos.up().offset(facing.getOpposite())); // Back

        // Diagonals
        positions.add(masterPos.up().offset(facing).offset(facing.rotateYCounterclockwise())); // Front-Left
        positions.add(masterPos.up().offset(facing).offset(facing.rotateYClockwise())); // Front-Right
        positions.add(masterPos.up().offset(facing.getOpposite()).offset(facing.rotateYCounterclockwise())); // Back-Left
        positions.add(masterPos.up().offset(facing.getOpposite()).offset(facing.rotateYClockwise())); // Back-Right

        return positions;
    }

    protected void placeEnergySlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        // Check if we can place the energy slave block
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState energySlaveBlockState = ModBlocks.CRUSHER_ENERGY_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, energySlaveBlockState, 3);
            CrusherEnergySlaveBlockEntity energySlaveEntity = (CrusherEnergySlaveBlockEntity) world.getBlockEntity(slavePos);
            if (energySlaveEntity != null) {
                energySlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed a crusher EnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place a crusher EnergySlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }
    protected void placeRedstoneSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState redstoneSlaveBlockState = ModBlocks.CRUSHER_REDSTONE_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, redstoneSlaveBlockState, 3);
            CrusherRedstoneSlaveBlockEntity redstoneSlaveEntity = (CrusherRedstoneSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (redstoneSlaveEntity != null) {
                redstoneSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed a CrusherRedstoneSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place a CrusherRedstoneSlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }
    protected void placeTopSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState topSlaveBlockState = ModBlocks.CRUSHER_TOP_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, topSlaveBlockState, 3);
            CrusherTopSlaveBlockEntity topSlaveEntity = (CrusherTopSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (topSlaveEntity != null) {
                System.out.println("Set Top Slave Master Pos at "+ masterPos);
                topSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed a CrusherTopSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place a CrusherTopSlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            // Calculate the positions of the slave blocks
            List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, state.get(FACING));

            // Break each slave block
            for (BlockPos slavePos : slaveBlockPositions) {
                BlockState slaveState = world.getBlockState(slavePos);
                if (slaveState.getBlock() instanceof CrusherEnergySlaveBlock || slaveState.getBlock() instanceof CrusherBaseSlaveBlock|| slaveState.getBlock() instanceof CrusherTopSlaveBlock) {
                    world.breakBlock(slavePos, true, player);
                }
            }

            // If the player is not in creative mode, drop the item
            if (!player.isCreative()) {
                Item item = asItem();
                ItemStack itemStack = new ItemStack(item);
                Block.dropStack(world, pos, itemStack);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // This handles dropping the inventory when it is broken
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrusherBlockEntity) {
                ItemScatterer.spawn(world, pos, (CrusherBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((CrusherBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos,state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CRUSHER_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }
}
