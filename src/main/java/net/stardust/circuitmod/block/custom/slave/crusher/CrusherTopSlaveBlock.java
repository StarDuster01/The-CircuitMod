package net.stardust.circuitmod.block.custom.slave.crusher;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.CrusherBlock;
import net.stardust.circuitmod.block.custom.slave.AbstractTechSlaveBlock;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherTopSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrusherTopSlaveBlock extends AbstractTechSlaveBlock {
    public CrusherTopSlaveBlock(Settings settings) {
        super(settings);
    }


    @Override
    protected BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof CrusherTopSlaveBlockEntity) {
            return ((CrusherTopSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }

    @Override
    protected Class<? extends BlockEntity> getMasterBlockEntityClass() {
        return CrusherBlockEntity.class;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrusherTopSlaveBlockEntity) {
                BlockPos masterPos = ((CrusherTopSlaveBlockEntity) be).getMasterPos();
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                if (masterEntity instanceof CrusherBlockEntity) {
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
        return new CrusherTopSlaveBlockEntity(pos,state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CRUSHER_TOP_SLAVE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrusherTopSlaveBlockEntity) {
                BlockPos masterPos = ((CrusherTopSlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof CrusherBlock) {
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3); // 3 for Block.UPDATE_ALL flag
                    }
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }
}
