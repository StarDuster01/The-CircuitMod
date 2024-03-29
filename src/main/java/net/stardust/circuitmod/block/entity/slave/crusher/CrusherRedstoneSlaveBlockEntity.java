package net.stardust.circuitmod.block.entity.slave.crusher;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class CrusherRedstoneSlaveBlockEntity extends BlockEntity {
    public CrusherRedstoneSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_REDSTONE_SLAVE_BE,pos, state);
    }

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:

                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:

                    break;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public BlockPos getMasterPos() {
        return this.masterPos;
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

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtCompound inventoryTag = new NbtCompound();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

    }
    private boolean isPowered = false;
    public void setPowered(boolean powered) {
        if (isPowered != powered) {
            isPowered = powered;
            markDirty();
        }
    }
}

