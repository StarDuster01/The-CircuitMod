package net.stardust.circuitmod.block.entity.slave;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;

public class EfficientCoalGeneratorRedstoneSlaveBlockEntity extends BlockEntity {
    public EfficientCoalGeneratorRedstoneSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFICIENT_COAL_GENERATOR_REDSTONE_SLAVE_BE,pos, state);
    }


    private int tickCounter = 0;


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
            // Here you can call other methods that need to know the state changed, or mark the block entity as dirty.
            markDirty();
            // If you want to send an update to the client, you can do it here too.
        }
    }
}

