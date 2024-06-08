package net.stardust.circuitmod.block.entity.slave.refinery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ModBlockEntities;

public class RefineryRedstoneSlaveBlockEntity extends BlockEntity {
    private BlockPos masterPos;

    public RefineryRedstoneSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_REDSTONE_SLAVE_BE, pos, state);
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, RefineryRedstoneSlaveBlockEntity be) {
        // Your tick logic here, if necessary
    }
}
