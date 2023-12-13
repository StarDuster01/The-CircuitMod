package net.stardust.circuitmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGeneratorBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    protected AbstractGeneratorBlock(Settings settings) {
        super(settings);
     //   this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }
    protected abstract void placeBaseSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos);
    protected abstract void placeEnergySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos);
    protected abstract void placeInventorySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos);
    protected abstract void placeRedstoneSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos);
    protected abstract void removeSlaveBlocks(World world, BlockPos pos);

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Abstract method that must be implemented by each specific generator
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // The specific ticker for the generator will be provided by the subclass
        return null;
    }
}