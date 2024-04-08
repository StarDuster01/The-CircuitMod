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

public class OilTowerLubeBlockEntity extends BlockEntity{

    private int lubeAmount;
    private final int maxLubeCapacity = 10000;
    private final int productionRate = 1;
    public OilTowerLubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_LUBE_BE,pos, state);
    }

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        System.out.println("Lube Amount" + this.lubeAmount);

        BlockEntity belowEntity = world.getBlockEntity(pos.down().down());
        if (belowEntity instanceof OilTowerResidueBlockEntity) {
            OilTowerResidueBlockEntity residueBlock = (OilTowerResidueBlockEntity) belowEntity;

            // Check if there's enough oil to convert and capacity for more lube
            if (residueBlock.getOilAmount() > 0 && this.lubeAmount < this.maxLubeCapacity) {
                int oilToConvert = Math.min(productionRate, residueBlock.getOilAmount());
                int possibleLubeProduction = Math.min(oilToConvert, maxLubeCapacity - this.lubeAmount);

                // Convert oil to lube
                residueBlock.decreaseOil(possibleLubeProduction);
                this.increaseLube(possibleLubeProduction);
            }
        }
        passLubeToPipe();
    }

    private void passLubeToPipe() {
        if(world == null || world.isClient) return;

        for(Direction direction : Direction.values()) {
            if(!direction.getAxis().isHorizontal()) continue;

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if(adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentEntity;

                if(pipe.canReceiveFluid() && this.lubeAmount > 0) {
                    int transferredAmount = Math.min(this.lubeAmount, 20);
                    decreaseLube(transferredAmount);
                    pipe.increaseFluidLevel(transferredAmount, "LUBE");
                    if(this.lubeAmount == 0) break;
                }
            }
        }
    }

    public void decreaseLube(int amount) {
        this.lubeAmount -= amount;
        if (this.lubeAmount < 0) {
            this.lubeAmount = 0;
        }
        markDirty();
    }
    public void increaseLube(int amount) {
        this.lubeAmount += amount;
        if (this.lubeAmount > this.maxLubeCapacity) {
            this.lubeAmount = this.maxLubeCapacity;
        }
        markDirty();
    }


    public BlockPos getMasterPos() {
        return this.masterPos;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("LubeAmount", this.lubeAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lubeAmount = nbt.getInt("LubeAmount");
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
