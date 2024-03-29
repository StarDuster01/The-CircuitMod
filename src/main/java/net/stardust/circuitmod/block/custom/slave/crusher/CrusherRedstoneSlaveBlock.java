package net.stardust.circuitmod.block.custom.slave.crusher;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.CrusherBlock;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherRedstoneSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrusherRedstoneSlaveBlock extends BlockWithEntity {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public CrusherRedstoneSlaveBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrusherRedstoneSlaveBlockEntity) {
                BlockPos masterPos = ((CrusherRedstoneSlaveBlockEntity) be).getMasterPos();
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                if (masterEntity instanceof CrusherBlockEntity) {
                    player.openHandledScreen((NamedScreenHandlerFactory)masterEntity);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.CONSUME;
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
        if (be instanceof CrusherRedstoneSlaveBlockEntity) {
            return ((CrusherRedstoneSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherRedstoneSlaveBlockEntity(pos,state);
    }
    // Override this method to handle the block being broken
    // This should be in your slave block class, not the block entity.
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrusherRedstoneSlaveBlockEntity) {
                BlockPos masterPos = ((CrusherRedstoneSlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    // Check if it is the correct instance of the master block
                    if (masterState.getBlock() instanceof CrusherBlock) {
                        // Trigger the drops and remove the block
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        // Set the master block position to air
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3); // 3 for Block.UPDATE_ALL flag
                    }
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }
    // This method will be used to check if the block is powered
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
                System.out.println("neighborUpdate: Power state changed at " + pos + " to " + isPowered);
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
                BlockPos masterPos = findMaster(pos, world);
                if (masterPos != null) {
                    BlockEntity masterEntity = world.getBlockEntity(masterPos);
                    if (masterEntity instanceof CrusherBlockEntity) {
                        ((CrusherBlockEntity) masterEntity).updatePoweredState(isPowered);
                    }
                }
            }
        }
    }



    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }
}
