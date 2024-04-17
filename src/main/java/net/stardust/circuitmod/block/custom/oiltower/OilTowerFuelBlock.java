package net.stardust.circuitmod.block.custom.oiltower;

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
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerFuelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class OilTowerFuelBlock extends BlockWithEntity {
    public OilTowerFuelBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OilTowerFuelBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OIL_TOWER_FUEL_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof OilTowerFuelBlockEntity) {
                OilTowerFuelBlockEntity slaveBE = (OilTowerFuelBlockEntity) be;
                BlockPos masterPos = slaveBE.getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof OilTowerResidueBlock) {
                        world.removeBlock(masterPos, false);
                    }
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }
}