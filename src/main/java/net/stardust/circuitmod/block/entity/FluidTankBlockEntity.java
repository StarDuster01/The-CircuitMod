package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankBlockEntity extends BlockEntity {

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK_BE, pos, state);
    }

    public void tick(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
    }
}
