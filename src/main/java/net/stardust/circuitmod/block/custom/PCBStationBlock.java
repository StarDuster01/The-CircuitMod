package net.stardust.circuitmod.block.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.slave.PCBStationBaseSlaveBlock;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PCBStationBlockEntity;
import net.stardust.circuitmod.block.entity.QuarryBlockEntity;
import net.stardust.circuitmod.block.entity.slave.PCBStationBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PCBStationBlock extends BlockWithEntity implements BlockEntityProvider {
    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }
    public PCBStationBlock(Settings settings) {
        super(settings);
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;


    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }




    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PCBStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((PCBStationBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.PCBSTATION_BLOCK_BE, (world1, pos, state1, be) -> {
            if (be instanceof PCBStationBlockEntity) { // This ensures the right type before casting
                ((PCBStationBlockEntity) be).tick(world1, pos, state1, (PCBStationBlockEntity) be);
            }
        });
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            Direction facing = state.get(FACING);
            BlockPos rightPos = pos.offset(facing.rotateYClockwise().getOpposite());
            world.setBlockState(rightPos, ModBlocks.PCBSTATION_BASE_SLAVE_BLOCK.getDefaultState(), 3);

            BlockEntity slaveEntity = world.getBlockEntity(rightPos);
            // This logging line will print out the actual instance of the entity at 'rightPos'
            System.out.println("[Debug] Entity at " + rightPos + " is: " + slaveEntity);
            if (slaveEntity instanceof PCBStationBaseSlaveBlockEntity) {
                ((PCBStationBaseSlaveBlockEntity) slaveEntity).setMasterPos(pos); // Set the master's position
                System.out.println("[Debug] Setting master position for slave at " + rightPos + " to " + pos);
            }
        }
    }



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            Direction facing = state.get(FACING);
            BlockPos rightPos = pos.offset(facing.rotateYClockwise().getOpposite());
            BlockState rightState = world.getBlockState(rightPos);
            System.out.println("[Debug] PCBStationBlock being broken at: " + pos);
            if (rightState.getBlock() instanceof PCBStationBaseSlaveBlock) {
                world.setBlockState(rightPos, Blocks.AIR.getDefaultState(), 35); // Remove the slave block
            }
        }
        super.onBreak(world, pos, state, player);
    }

}
