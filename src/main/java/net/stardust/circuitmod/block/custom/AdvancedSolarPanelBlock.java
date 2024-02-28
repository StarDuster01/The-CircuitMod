package net.stardust.circuitmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.AdvancedSolarPanelBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import org.jetbrains.annotations.Nullable;

public class AdvancedSolarPanelBlock extends BlockWithEntity {
    public AdvancedSolarPanelBlock(Settings settings) {
        super(settings.luminance((state) -> 10));
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            // Block is being destroyed or replaced, remove the base block below
            BlockPos belowPos = pos.down();
            if (world.getBlockState(belowPos).getBlock() instanceof AdvancedSolarPanelBaseBlock) {
                // Ensure you only remove the specific base block related to this panel
                world.removeBlock(belowPos, false);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
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
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedSolarPanelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ADVANCED_SOLAR_PANEL_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }
}
