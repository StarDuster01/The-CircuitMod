package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;

public class FluidTankBlockEntity extends BlockEntity implements IFluidConsumer {

    private static final int MAX_FLUID_AMOUNT = 4000; // 4 buckets (1000 mB each)
    private int currentFluidAmount = 0;
    private String currentFluidType = null;

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK_BE, pos, state);
    }

    public void tick(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
    }

    @Override
    public void addFluid(int fluidAmount, String fluidType) {
        if (currentFluidType == null) {
            currentFluidType = fluidType;
        }

        if (!currentFluidType.equals(fluidType)) {
            return; // Cannot mix different types of fluids
        }

        currentFluidAmount += fluidAmount;
        if (currentFluidAmount > MAX_FLUID_AMOUNT) {
            currentFluidAmount = MAX_FLUID_AMOUNT; // Cap the fluid amount to the max capacity
        }

        markDirty(); // Mark the block entity as dirty to ensure it is saved correctly
    }

    @Override
    public boolean canReceiveFluid(String fluidType) {
        return (currentFluidAmount < MAX_FLUID_AMOUNT) && (currentFluidType == null || currentFluidType.equals(fluidType));
    }

    public int getCurrentFluidAmount() {
        return currentFluidAmount;
    }

    public String getCurrentFluidType() {
        return currentFluidType;
    }

    public void setCurrentFluidAmount(int amount) {
        currentFluidAmount = amount;
        if (currentFluidAmount == 0) {
            currentFluidType = null; // Reset the fluid type if the tank is empty
        }
    }
}
