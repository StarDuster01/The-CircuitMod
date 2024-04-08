package net.stardust.circuitmod.block.entity.oiltower;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.FluidPipeBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class OilTowerResidueSlaveBlockEntity extends BlockEntity{


    public OilTowerResidueSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_RESIDUE_SLAVE_BE,pos, state);
    }

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {

    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
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
