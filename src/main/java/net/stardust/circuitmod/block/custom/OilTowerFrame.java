package net.stardust.circuitmod.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.stardust.circuitmod.block.entity.OilTowerFrameBlockEntity;
import net.stardust.circuitmod.block.entity.slave.GenericMachineFillerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class OilTowerFrame extends BlockWithEntity implements BlockEntityProvider {
    public OilTowerFrame(Settings settings) {
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OilTowerFrameBlockEntity(pos, state);
    }
}
