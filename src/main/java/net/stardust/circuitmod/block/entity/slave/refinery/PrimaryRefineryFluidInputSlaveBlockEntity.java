package net.stardust.circuitmod.block.entity.slave.refinery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.api.IFluidConsumer;
import org.jetbrains.annotations.Nullable;

public class PrimaryRefineryFluidInputSlaveBlockEntity extends BlockEntity {

    private BlockPos masterPos;

    public PrimaryRefineryFluidInputSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRIMARY_REFINERY_FLUID_INPUT_SLAVE_BE, pos, state);
    }

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // Implement the logic to handle fluid transfer to the master refinery block here.
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("MasterPosX") && nbt.contains("MasterPosY") && nbt.contains("MasterPosZ")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            masterPos = new BlockPos(x, y, z);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
}
