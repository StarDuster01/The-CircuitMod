package net.stardust.circuitmod.block.custom.slave.fuelgenerator;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
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
import net.stardust.circuitmod.block.custom.FuelGeneratorBlock;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorInventorySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FuelGeneratorInventorySlaveBlock extends BlockWithEntity {
    public FuelGeneratorInventorySlaveBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FuelGeneratorInventorySlaveBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FUEL_GENERATOR_INVENTORY_SLAVE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FuelGeneratorInventorySlaveBlockEntity) {
                BlockPos masterPos = ((FuelGeneratorInventorySlaveBlockEntity) be).getMasterPos();
                BlockEntity masterEntity = world.getBlockEntity(masterPos);

                if (masterEntity instanceof FuelGeneratorBlockEntity) {
                    player.openHandledScreen((NamedScreenHandlerFactory)masterEntity);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FuelGeneratorInventorySlaveBlockEntity) {
                BlockPos masterPos = ((FuelGeneratorInventorySlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    // Check if it is the correct instance of the master block
                    if (masterState.getBlock() instanceof FuelGeneratorBlock) {
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




}
