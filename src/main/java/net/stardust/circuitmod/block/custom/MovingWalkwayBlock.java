package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.MovingWalkwayBlockEntity;
import org.jetbrains.annotations.Nullable;

public class MovingWalkwayBlock extends BlockWithEntity implements BlockEntityProvider {
    public MovingWalkwayBlock(Settings settings) {
        super(settings);
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

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
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MovingWalkwayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        System.out.println("getTicker called for type: " + type);
        return checkType(type, ModBlockEntities.MOVING_WALKWAY_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
