package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(RAILED_X, false)
                .with(RAILED_Y, false));
    }

    public static final VoxelShape CUSTOM_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CUSTOM_SHAPE;
    }
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty RAILED_X = BooleanProperty.of("railed_x");
    public static final BooleanProperty RAILED_Y = BooleanProperty.of("railed_y");

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(RAILED_X, false)
                .with(RAILED_Y, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, RAILED_X, RAILED_Y);
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
