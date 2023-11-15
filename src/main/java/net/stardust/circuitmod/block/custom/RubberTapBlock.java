package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.MovingWalkwayBlockEntity;
import net.stardust.circuitmod.block.entity.QuarryBlockEntity;
import net.stardust.circuitmod.block.entity.RubberTapBlockEntity;
import org.jetbrains.annotations.Nullable;

public class RubberTapBlock extends BlockWithEntity implements BlockEntityProvider {
    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public RubberTapBlock(Settings settings) {

        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FILL_LEVEL, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);

        VoxelShape shape = VoxelShapes.empty();

        // Calculate the offset based on the facing direction
        double offsetX = (direction == Direction.WEST) ? 0.5 : (direction == Direction.EAST) ? -0.5 : 0;
        double offsetY = (direction == Direction.DOWN) ? 0.5 : (direction == Direction.UP) ? -0.5 : 0;
        double offsetZ = (direction == Direction.NORTH) ? 0.5 : (direction == Direction.SOUTH) ? -0.5 : 0;

        // Element 1 (quarter-size cube)
        double minX1 = 0.25 + offsetX;
        double minY1 = 0.25 + offsetY;
        double minZ1 = 0.25 + offsetZ;
        double maxX1 = 0.75 + offsetX;
        double maxY1 = 0.75 + offsetY;
        double maxZ1 = 0.75 + offsetZ;
        VoxelShape element1 = VoxelShapes.cuboid(minX1, minY1, minZ1, maxX1, maxY1, maxZ1);

        // Add element to the overall shape
        shape = VoxelShapes.union(shape, element1);

        return shape;
    }
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty FILL_LEVEL = IntProperty.of("fill_level", 0, 3);
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                // Get the block position where the block will be placed
                BlockPos offsetPos = blockPos.offset(direction);
                BlockState offsetState = worldView.getBlockState(offsetPos);

                // Check if the opposite side of the block is solid
                if (offsetState.isSideSolidFullSquare(worldView, offsetPos, direction.getOpposite())) {
                    return this.getDefaultState().with(FACING, direction.getOpposite()).with(FILL_LEVEL, 0);
                }
            }
        }
        return null;
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FILL_LEVEL);
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RubberTapBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((RubberTapBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.RUBBER_TAP_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
