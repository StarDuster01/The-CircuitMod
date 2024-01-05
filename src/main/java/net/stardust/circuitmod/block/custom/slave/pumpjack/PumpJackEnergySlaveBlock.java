package net.stardust.circuitmod.block.custom.slave.pumpjack;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.FuelGeneratorBlock;
import net.stardust.circuitmod.block.custom.PumpJackBlock;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PumpJackEnergySlaveBlock extends BlockWithEntity {
    public PumpJackEnergySlaveBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PumpJackEnergySlaveBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.PUMP_JACK_ENERGY_SLAVE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PumpJackEnergySlaveBlockEntity) {
                BlockPos masterPos = ((PumpJackEnergySlaveBlockEntity) be).getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    // Check if it is the correct instance of the master block
                    if (masterState.getBlock() instanceof PumpJackBlock) {
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
