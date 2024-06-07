package net.stardust.circuitmod.block.entity.slave.refinery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.entity.ModBlockEntities;

public class RefineryEnergySlaveBlockEntity extends BlockEntity implements IEnergyConsumer {
    private int directEnergy = 0;
    private BlockPos masterPos;
    private static final int MAX_DIRECT_ENERGY = 100000;
    public RefineryEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_ENERGY_SLAVE_BE, pos, state);
    }

    public int getDirectEnergy() {
        return directEnergy;
    }

    public void addEnergy(int amount) {
        directEnergy += amount;
        directEnergy = Math.min(directEnergy, MAX_DIRECT_ENERGY);
    }
    public void reduceEnergy(int amount) {
        directEnergy = Math.max(0, directEnergy - amount);
        markDirty();
    }

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Energy", directEnergy);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        directEnergy = nbt.getInt("Energy");
        if (nbt.contains("MasterPosX") && nbt.contains("MasterPosY") && nbt.contains("MasterPosZ")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            masterPos = new BlockPos(x, y, z);
        }
    }


}
