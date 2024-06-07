package net.stardust.circuitmod.block.entity.oiltower;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.block.entity.FluidPipeBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.ModBlocks;

public class OilTowerResidueBlockEntity extends BlockEntity implements IFluidConsumer {

    private int oilAmount;
    private int maxOilCapacity = 100000; // Example maximum oil capacity, adjust as needed

    private int residueAmount;
    private int maxResidueCapacity = 50000; // Example maximum residue capacity, adjust as needed
    private int tickCounter = 0;

    public OilTowerResidueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_RESIDUE_BE, pos, state);
        this.oilAmount = 0;
        this.residueAmount = 0;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                if (this.oilAmount > 0 && this.residueAmount < this.maxResidueCapacity) {
                    decreaseOil(1); // Decrease oil by 1 unit
                    increaseResidue(1); // Increase residue by 1 unit
                }
                passResidueToPipe(); // Attempt to pass residue to an adjacent pipe
            }
        }
    }

    private void passResidueToPipe() {
        if (world == null || world.isClient) return;

        for (Direction direction : Direction.values()) {
            if (!direction.getAxis().isHorizontal()) continue;

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if (adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentEntity;

                if (pipe.canReceiveFluid("RESIDUE") && this.residueAmount > 0) {
                    int transferredAmount = Math.min(this.residueAmount, 20);
                    decreaseResidue(transferredAmount);
                    pipe.increaseFluidLevel(transferredAmount, "RESIDUE");
                    if (this.residueAmount == 0) break;
                }
            }
        }
    }

    public boolean hasSpecificBlocksAbove(World world) {
        // Assuming the pattern starts two blocks above and repeats every two blocks
        for (int i = 2; i <= 6; i += 2) { // Start from 2 blocks above and check every two blocks
            BlockPos checkPos = pos.up(i);
            BlockState stateAtPos = world.getBlockState(checkPos);

            // Check if the block at the position is the required lube block
            if (stateAtPos.getBlock() != ModBlocks.OIL_TOWER_LUBE_BLOCK) {
                return false; // If any block in the pattern is not a lube block, return false
            }
        }
        return true; // If all required positions have lube blocks, return true
    }

    @Override
    public void addFluid(int fluidAmount, String fluidType) {
        if ("CRUDEOIL".equalsIgnoreCase(fluidType)) {
            increaseOil(fluidAmount);
        }
    }

    public void increaseOil(int amount) {
        this.oilAmount += amount;
        if (this.oilAmount > this.maxOilCapacity) {
            this.oilAmount = this.maxOilCapacity;
        }
        markDirty();
    }

    public void decreaseOil(int amount) {
        this.oilAmount -= amount;
        if (this.oilAmount < 0) {
            this.oilAmount = 0;
        }
        markDirty();
    }

    public void increaseResidue(int amount) {
        this.residueAmount += amount;
        if (this.residueAmount > this.maxResidueCapacity) {
            this.residueAmount = this.maxResidueCapacity;
        }
        markDirty();
    }

    public void decreaseResidue(int amount) {
        this.residueAmount -= amount;
        if (this.residueAmount < 0) {
            this.residueAmount = 0;
        }
        markDirty();
    }

    public int getOilAmount() {
        return this.oilAmount;
    }

    public int getResidueAmount() {
        return this.residueAmount;
    }

    @Override
    public boolean canReceiveFluid(String fluidType) {
        boolean hasCapacityForMoreOil = this.oilAmount < this.maxOilCapacity;
        return "CRUDEOIL".equalsIgnoreCase(fluidType) && hasCapacityForMoreOil;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("CRUDEOIL", this.oilAmount);
        nbt.putInt("ResidueAmount", this.residueAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.oilAmount = nbt.getInt("CRUDEOIL");
        this.residueAmount = nbt.getInt("ResidueAmount");
    }
}
