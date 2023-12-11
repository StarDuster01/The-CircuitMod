package net.stardust.circuitmod.block.custom.slave;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.PCBStationBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.PCBStationBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PCBStationBaseSlaveBlock extends AbstractTechSlaveBlock{
    public PCBStationBaseSlaveBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof PCBStationBaseSlaveBlockEntity) {
            return ((PCBStationBaseSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PCBStationBaseSlaveBlockEntity(pos,state);
    }



    @Override
    protected Class<? extends BlockEntity> getMasterBlockEntityClass() {
        return PCBStationBlockEntity.class;
    }
}
