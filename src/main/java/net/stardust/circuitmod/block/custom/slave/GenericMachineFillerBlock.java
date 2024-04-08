package net.stardust.circuitmod.block.custom.slave;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.CrusherBlock;
import net.stardust.circuitmod.block.entity.BasicSolarPanelBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.GenericMachineFillerBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GenericMachineFillerBlock extends BlockWithEntity implements BlockEntityProvider {



    public GenericMachineFillerBlock(Settings settings) {
        super(settings);
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GenericMachineFillerBlockEntity(pos, state);
    }

    protected BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof CrusherBaseSlaveBlockEntity) {
            return ((CrusherBaseSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GenericMachineFillerBlockEntity) {
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GenericMachineFillerBlockEntity) {
                BlockPos masterPos = ((GenericMachineFillerBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3); // 3 for Block.UPDATE_ALL flag
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }
}
