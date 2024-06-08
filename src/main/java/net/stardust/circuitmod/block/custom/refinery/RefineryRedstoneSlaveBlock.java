package net.stardust.circuitmod.block.custom.refinery;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.RefineryBlockEntity;
import net.stardust.circuitmod.block.entity.slave.refinery.RefineryRedstoneSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class RefineryRedstoneSlaveBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public RefineryRedstoneSlaveBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1F;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    private BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof RefineryRedstoneSlaveBlockEntity) {
            return ((RefineryRedstoneSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RefineryRedstoneSlaveBlockEntity(pos, state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RefineryRedstoneSlaveBlockEntity) {
                BlockPos masterPos = ((RefineryRedstoneSlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof RefineryBlock) {
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    public boolean isPowered(World world, BlockPos pos) {
        return world.getBlockState(pos).get(POWERED);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            boolean currentState = state.get(POWERED);
            if (isPowered != currentState) {
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
                BlockPos masterPos = findMaster(pos, world);
                if (masterPos != null) {
                    BlockEntity masterEntity = world.getBlockEntity(masterPos);
                    if (masterEntity instanceof RefineryBlockEntity) {
                        ((RefineryBlockEntity) masterEntity).updatePoweredState(isPowered);
                    }
                }
            }
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }
}
