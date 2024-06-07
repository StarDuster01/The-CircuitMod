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

public class OilTowerFuelBlockEntity extends BlockEntity {

    private int fuelAmount;
    private final int maxFuelCapacity = 10000;
    private final int productionRate = 1;

    public OilTowerFuelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_FUEL_BE, pos, state);
    }

    private BlockPos masterPos;

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        System.out.println("Fuel Amount: " + this.fuelAmount);

        BlockEntity belowEntity = world.getBlockEntity(pos.down(6));
        if (belowEntity instanceof OilTowerResidueBlockEntity) {
            OilTowerResidueBlockEntity residueBlock = (OilTowerResidueBlockEntity) belowEntity;

            // Check if there's enough oil to convert and capacity for more fuel
            if (residueBlock.getOilAmount() > 0 && this.fuelAmount < this.maxFuelCapacity) {
                int oilToConvert = Math.min(productionRate, residueBlock.getOilAmount());
                int possibleFuelProduction = Math.min(oilToConvert, maxFuelCapacity - this.fuelAmount);

                // Convert oil to fuel
                residueBlock.decreaseOil(possibleFuelProduction);
                this.increaseFuel(possibleFuelProduction);
            }
        }
        passFuelToPipe();
    }

    private void passFuelToPipe() {
        if (world == null || world.isClient) return;

        for (Direction direction : Direction.values()) {
            if (!direction.getAxis().isHorizontal()) continue;

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if (adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentEntity;

                if (pipe.canReceiveFluid("LIQUIDFUEL") && this.fuelAmount > 0) {
                    int transferredAmount = Math.min(this.fuelAmount, 20);
                    decreaseFuel(transferredAmount);
                    pipe.increaseFluidLevel(transferredAmount, "LIQUIDFUEL");
                    if (this.fuelAmount == 0) break;
                }
            }
        }
    }


    public void decreaseFuel(int amount) {
        this.fuelAmount -= amount;
        if (this.fuelAmount < 0) {
            this.fuelAmount = 0;
        }
        markDirty();
    }

    public void increaseFuel(int amount) {
        this.fuelAmount += amount;
        if (this.fuelAmount > this.maxFuelCapacity) {
            this.fuelAmount = this.maxFuelCapacity;
        }
        markDirty();
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("FuelAmount", this.fuelAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.fuelAmount = nbt.getInt("FuelAmount");
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