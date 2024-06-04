package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.ConductorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ConductorBlock extends BlockWithEntity implements BlockEntityProvider {
    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public ConductorBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    //public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
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

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        // This is the list of blocks which the model will adapt too to appear as if it is 'connected' to the block
        List<Block> detectableBlocks = Arrays.asList(this, ModBlocks.QUARRY_BLOCK, ModBlocks.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK,
                ModBlocks.MOVING_WALKWAY_BLOCK, ModBlocks.FUEL_GENERATOR_ENERGY_SLAVE_BLOCK, ModBlocks.PUMP_JACK_ENERGY_SLAVE_BLOCK,
                ModBlocks.BASIC_SOLAR_PANEL_BLOCK,ModBlocks.ADVANCED_SOLAR_PANEL_BASE_BLOCK, ModBlocks.CRUSHER_ENERGY_SLAVE_BLOCK, ModBlocks.POWER_VOID, ModBlocks.POWER_CUBE);


        boolean north = detectableBlocks.contains(world.getBlockState(pos.north()).getBlock());
        boolean east = detectableBlocks.contains(world.getBlockState(pos.east()).getBlock());
        boolean south = detectableBlocks.contains(world.getBlockState(pos.south()).getBlock());
        boolean west = detectableBlocks.contains(world.getBlockState(pos.west()).getBlock());
        boolean up = detectableBlocks.contains(world.getBlockState(pos.up()).getBlock());
        boolean down = detectableBlocks.contains(world.getBlockState(pos.down()).getBlock());


        world.setBlockState(pos, state.with(NORTH, north)
                .with(EAST, east)
                .with(SOUTH, south)
                .with(WEST, west)
                .with(UP, up)
                .with(DOWN, down));
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConductorBlockEntity(pos, state);
    }
}
