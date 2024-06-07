package net.stardust.circuitmod.block.custom.refinery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.RefineryBlockEntity;
;
import net.stardust.circuitmod.block.entity.slave.refinery.PrimaryRefineryFluidInputSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.refinery.RefineryEnergySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RefineryBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public RefineryBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RefineryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.REFINERY_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing);

        List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, facing);
        boolean areaClear = slaveBlockPositions.stream().allMatch(blockPos ->
                world.isAir(blockPos) || world.getBlockState(blockPos).canReplace(ctx)
        );

        if (!world.isClient && !areaClear) {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                player.sendMessage(Text.literal("The area is not clear for the Refinery. Please clear any obstructing blocks."), false);
            }
            return null; // Cancel the block placement by returning null
        }

        if (!world.isClient) {
            BlockPos energySlavePos = pos.offset(facing.getOpposite(), 1);
            BlockPos fluidInputSlavePos1 = pos.offset(facing.getOpposite(), 1).offset(facing.rotateYCounterclockwise(), 1);
            BlockPos fluidInputSlavePos2 = pos.offset(facing, 1).offset(facing.rotateYCounterclockwise(), 1);
            BlockPos redstoneSlavePos = pos.offset(facing.getOpposite(), 1).offset(facing.rotateYClockwise(), 1);
            placeEnergySlaveBlock(world, ctx, energySlavePos, pos);
            placeFluidInputSlaveBlock(world, ctx, fluidInputSlavePos1, pos);
            placeFluidInputSlaveBlock(world, ctx, fluidInputSlavePos2, pos);
        }
        return state;
    }

    private List<BlockPos> calculateSlaveBlockPositions(BlockPos masterPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();
        positions.add(masterPos.offset(facing.getOpposite(), 1)); // Energy slave position
        positions.add(masterPos.offset(facing, 1)); // Fluid input slave position
        positions.add(masterPos.offset(facing.getOpposite(), 1).offset(facing.rotateYClockwise(), 1)); // Fluid input slave position
        positions.add(masterPos.offset(facing, 1).offset(facing.rotateYCounterclockwise(), 1)); // Fluid input slave position
        return positions;
    }

    protected void placeEnergySlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState energySlaveBlockState = ModBlocks.REFINERY_ENERGY_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, energySlaveBlockState, 3);
            RefineryEnergySlaveBlockEntity energySlaveEntity = (RefineryEnergySlaveBlockEntity) world.getBlockEntity(slavePos);
            if (energySlaveEntity != null) {
                energySlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed a Refinery Energy Slave Block at " + slavePos);
        } else {
            System.out.println("Could not place a Refinery Energy Slave Block at " + slavePos + " as the position is not replaceable");
        }
    }

    protected void placeFluidInputSlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState fluidInputSlaveBlockState = ModBlocks.PRIMARY_REFINERY_FLUID_INPUT_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, fluidInputSlaveBlockState, 3);
            PrimaryRefineryFluidInputSlaveBlockEntity fluidInputSlaveEntity = (PrimaryRefineryFluidInputSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (fluidInputSlaveEntity != null) {
                fluidInputSlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed a Primary Refinery Fluid Input Slave Block at " + slavePos);
        } else {
            System.out.println("Could not place a Primary Refinery Fluid Input Slave Block at " + slavePos + " as the position is not replaceable");
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, state.get(FACING));

            for (BlockPos slavePos : slaveBlockPositions) {
                BlockState slaveState = world.getBlockState(slavePos);
                if (slaveState.getBlock() instanceof RefineryEnergySlaveBlock || slaveState.getBlock() instanceof PrimaryRefineryFluidInputSlaveBlock) {
                    world.breakBlock(slavePos, true, player);
                }
            }

            if (!player.isCreative()) {
                ItemStack itemStack = new ItemStack(this.asItem());
                Block.dropStack(world, pos, itemStack);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
}
