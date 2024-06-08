package net.stardust.circuitmod.block.entity.slave.refinery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.stardust.circuitmod.block.entity.ModBlockEntities;

public class RefineryOutputSlaveBlockEntity extends BlockEntity {
    private BlockPos masterPos;

    public RefineryOutputSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_OUTPUT_SLAVE_BE, pos, state);
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (masterPos != null) {
            nbt.putLong("MasterPos", masterPos.asLong());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("MasterPos")) {
            masterPos = BlockPos.fromLong(nbt.getLong("MasterPos"));
        }
    }
}
