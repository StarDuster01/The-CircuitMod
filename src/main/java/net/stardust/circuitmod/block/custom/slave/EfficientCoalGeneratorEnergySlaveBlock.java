package net.stardust.circuitmod.block.custom.slave;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorEnergySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EfficientCoalGeneratorEnergySlaveBlock extends BlockWithEntity {
    public EfficientCoalGeneratorEnergySlaveBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EfficientCoalGeneratorEnergySlaveBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }


}
