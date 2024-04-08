package net.stardust.circuitmod.block.entity.slave;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class GenericMachineFillerBlockEntity extends BlockEntity {

    public GenericMachineFillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERIC_MACHINE_FILLER_BE, pos, state);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    public BlockPos getMasterPos() {
        return this.masterPos;
    }
    private BlockPos masterPos;
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

}
