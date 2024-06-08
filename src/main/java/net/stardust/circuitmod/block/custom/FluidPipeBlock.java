package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.FluidPipeBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PipeBlockEntity;
import net.stardust.circuitmod.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class FluidPipeBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public boolean isPowered(World world, BlockPos pos) {
        return world.getBlockState(pos).get(POWERED);
    }

    public FluidPipeBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    private static final VoxelShape CORE_SHAPE = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 5, 5, 5, 11, 11);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 5, 11);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(5, 11, 5, 11, 16, 11);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            CORE_SHAPE,
            WEST_SHAPE,
            EAST_SHAPE,
            NORTH_SHAPE,
            SOUTH_SHAPE,
            BOTTOM_SHAPE,
            TOP_SHAPE
    );


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);


        //List<Block> detectableBlocks = Arrays.asList(this, ModBlocks.PUMP_JACK_EXTRA_SLAVE_BLOCK, ModBlocks.FUEL_GENERATOR_INVENTORY_SLAVE_BLOCK);

        TagKey<Block> detectableBlocksTag = ModTags.Blocks.FLUID_PIPE_CONNECTABLE;

        boolean north = world.getBlockState(pos.north()).isIn(detectableBlocksTag);
        boolean east = world.getBlockState(pos.east()).isIn(detectableBlocksTag);
        boolean south = world.getBlockState(pos.south()).isIn(detectableBlocksTag);
        boolean west = world.getBlockState(pos.west()).isIn(detectableBlocksTag);
        boolean up = world.getBlockState(pos.up()).isIn(detectableBlocksTag);
        boolean down = world.getBlockState(pos.down()).isIn(detectableBlocksTag);


        world.setBlockState(pos, state.with(NORTH, north)
                .with(EAST, east)
                .with(SOUTH, south)
                .with(WEST, west)
                .with(UP, up)
                .with(DOWN, down));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
        builder.add(POWERED);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                //.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false);

    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = CORE_SHAPE;

        if (state.get(NORTH)) {
            shape = VoxelShapes.union(shape, NORTH_SHAPE);
        }
        if (state.get(EAST)) {
            shape = VoxelShapes.union(shape, EAST_SHAPE);
        }
        if (state.get(SOUTH)) {
            shape = VoxelShapes.union(shape, SOUTH_SHAPE);
        }
        if (state.get(WEST)) {
            shape = VoxelShapes.union(shape, WEST_SHAPE);
        }
        if (state.get(UP)) {
            shape = VoxelShapes.union(shape, TOP_SHAPE);
        }
        if (state.get(DOWN)) {
            shape = VoxelShapes.union(shape, BOTTOM_SHAPE);
        }

        return shape;
    }


    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FLUID_PIPE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }
}
