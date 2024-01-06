package net.stardust.circuitmod.block.custom.slave.pumpjack;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.PumpJackBlock;
import net.stardust.circuitmod.block.custom.slave.AbstractTechSlaveBlock;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;

import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PumpJackBaseSlaveBlock extends AbstractTechSlaveBlock {
    public PumpJackBaseSlaveBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }


    @Override
    protected BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof PumpJackBaseSlaveBlockEntity) {
            return ((PumpJackBaseSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }

    @Override
    protected Class<? extends BlockEntity> getMasterBlockEntityClass() {
        return PumpJackBlockEntity.class;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PumpJackBaseSlaveBlockEntity) {
                BlockPos masterPos = ((PumpJackBaseSlaveBlockEntity) be).getMasterPos();
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                if (masterEntity instanceof PumpJackBlockEntity) {
                    player.openHandledScreen((NamedScreenHandlerFactory)masterEntity);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PumpJackBaseSlaveBlockEntity(pos,state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PumpJackBaseSlaveBlockEntity) {
                BlockPos masterPos = ((PumpJackBaseSlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof PumpJackBlock) {
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3); // 3 for Block.UPDATE_ALL flag
                    }
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }




}
