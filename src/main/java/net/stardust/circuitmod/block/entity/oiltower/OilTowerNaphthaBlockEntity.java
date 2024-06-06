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

public class OilTowerNaphthaBlockEntity extends BlockEntity {
    private int naphthaAmount;
    private final int maxNaphthaCapacity = 10000;
    private final int productionRate = 1;

    public OilTowerNaphthaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_NAPHTHA_BE, pos, state);
    }

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        System.out.println("Naphtha Amount" + this.naphthaAmount);

        BlockEntity belowEntity = world.getBlockEntity(pos.down(9));
        if (belowEntity instanceof OilTowerResidueBlockEntity) {
            OilTowerResidueBlockEntity residueBlock = (OilTowerResidueBlockEntity) belowEntity;

            if (residueBlock.getOilAmount() > 0 && this.naphthaAmount < this.maxNaphthaCapacity) {
                int oilToConvert = Math.min(productionRate, residueBlock.getOilAmount());
                int possibleNaphthaProduction = Math.min(oilToConvert, maxNaphthaCapacity - this.naphthaAmount);

                residueBlock.decreaseOil(possibleNaphthaProduction);
                this.increaseNaphtha(possibleNaphthaProduction);
            }
        }
        passNaphthaToPipe();
    }

    private void passNaphthaToPipe() {
        if (world == null || world.isClient) return;

        for (Direction direction : Direction.values()) {
            if (!direction.getAxis().isHorizontal()) continue;

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if (adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentEntity;

                if (pipe.canReceiveFluid("NAPHTHA") && this.naphthaAmount > 0) {
                    int transferredAmount = Math.min(this.naphthaAmount, 20);
                    decreaseNaphtha(transferredAmount);
                    pipe.increaseFluidLevel(transferredAmount, "NAPHTHA");
                    if (this.naphthaAmount == 0) break;
                }
            }
        }
    }


    public void decreaseNaphtha(int amount) {
        this.naphthaAmount -= amount;
        markDirty();
    }

    public void increaseNaphtha(int amount) {
        this.naphthaAmount += amount;
        if (this.naphthaAmount > maxNaphthaCapacity) {
            this.naphthaAmount = maxNaphthaCapacity;
        }
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("NaphthaAmount", this.naphthaAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.naphthaAmount = nbt.getInt("NaphthaAmount");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }
}
