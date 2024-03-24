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
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherEnergySlaveBlockEntity;
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
            BlockPos extraSlavePos = pos.offset(facing, 2);
            BlockPos energySlavePos = pos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYCounterclockwise());
            BlockPos baseSlavePos1 = pos.offset(facing, 1);


            // Place Slave Blocks Here
            placeEnergySlaveBlock(world, ctx, energySlavePos, pos);

        }
        return state;
    }
    private List<BlockPos> calculateSlaveBlockPositions(BlockPos masterPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();
        // AAdd all slave block positions here for the placement check
        positions.add(masterPos.offset(facing.getOpposite(), 2).up().offset(facing.rotateYCounterclockwise()));

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

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            // Calculate the positions of the slave blocks
            List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, state.get(FACING));

            // Break each slave block
            for (BlockPos slavePos : slaveBlockPositions) {
                BlockState slaveState = world.getBlockState(slavePos);
                if (slaveState.getBlock() instanceof CrusherEnergySlaveBlock || slaveState.getBlock() instanceof CrusherBaseSlaveBlock) {
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
