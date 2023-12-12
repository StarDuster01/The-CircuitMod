package net.stardust.circuitmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FuelGeneratorBlock extends AbstractGeneratorBlock{
    protected FuelGeneratorBlock(Settings settings) {
        super(settings.luminance((state) -> 10));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FuelGeneratorBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FUEL_GENERATOR_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing).with(LIT,false);

        if (!world.isClient) {
            // above main block
            BlockPos upPos = pos.up();
            // back front left right
            BlockPos backPos = pos.offset(facing.getOpposite());
            BlockPos frontPos = pos.offset(facing);
            BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
            BlockPos rightPos = pos.offset(facing.rotateYClockwise());
            // ABOVE back front left right
            BlockPos backPosup = pos.offset(facing.getOpposite()).up();
            BlockPos frontPosup = pos.offset(facing).up();
            BlockPos leftPosup = pos.offset(facing.rotateYCounterclockwise()).up();
            BlockPos rightPosup = pos.offset(facing.rotateYClockwise()).up();
            // DIAGONAL frontleft, frontright, backleft, backright
            BlockPos frontLeftPos = leftPos.offset(facing);
            BlockPos frontRightPos = rightPos.offset(facing);
            BlockPos backLeftPos = leftPos.offset(facing.getOpposite());
            BlockPos backRightPos = rightPos.offset(facing.getOpposite());
            // ABOVE DIAGONAL above frontleft, frontright, backleft, backright
            BlockPos frontLeftPosup = leftPos.offset(facing).up();
            BlockPos frontRightPosup = rightPos.offset(facing).up();
            BlockPos backLeftPosup = leftPos.offset(facing.getOpposite()).up();
            BlockPos backRightPosup = rightPos.offset(facing.getOpposite()).up();
        }
        return super.getPlacementState(ctx);
    }

    @Override
    protected void placeBaseSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState baseSlaveBlockState = ModBlocks.FUEL_GENERATOR_BASE_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, baseSlaveBlockState, 3);
            world.updateNeighborsAlways(slavePos, baseSlaveBlockState.getBlock()); // This should fix lighting issue?
            FuelGeneratorBaseSlaveBlockEntity baseSlaveEntity = (FuelGeneratorBaseSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (baseSlaveEntity != null) {
                baseSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed a FuelGeneratorBaseSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place an FuelGeneratorBaseSlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }
    @Override
    protected void placeEnergySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        // Check if we can place the energy slave block
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState energySlaveBlockState = ModBlocks.FUEL_GENERATOR_ENERGY_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, energySlaveBlockState, 3);
            FuelGeneratorEnergySlaveBlockEntity energySlaveEntity = (FuelGeneratorEnergySlaveBlockEntity) world.getBlockEntity(slavePos);
            if (energySlaveEntity != null) {
                energySlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed an EfficientCoalGeneratorEnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place an EfficientCoalGeneratorEnergySlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }
    @Override
    protected void placeInventorySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState inventorySlaveBlockState = ModBlocks.FUEL_GENERATOR_INVENTORY_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, inventorySlaveBlockState, 3);
            FuelGeneratorInventorySlaveBlockEntity inventorySlaveEntity = (FuelGeneratorInventorySlaveBlockEntity) world.getBlockEntity(slavePos);
            if (inventorySlaveEntity != null) {
                inventorySlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed an EfficientCoalGeneratorEnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place an EfficientCoalGeneratorEnergySlaveBlock at " + slavePos + " as the position is not replaceable");
        }
    }
    @Override
    protected void placeRedstoneSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState redstoneSlaveBlockState = ModBlocks.FUEL_GENERATOR_REDSTONE_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, redstoneSlaveBlockState, 3);
            FuelGeneratorRedstoneSlaveBlockEntity redstoneSlaveEntity = (FuelGeneratorRedstoneSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (redstoneSlaveEntity != null) {
                redstoneSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed a FuelGeneratorRedstoneSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place a FuelGeneratorRedstoneSlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (!player.isCreative()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                Item item = asItem();
                ItemStack itemStack = new ItemStack(item);
                Block.dropStack(world, pos, itemStack);
            }
            int radius = 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos checkPos = pos.add(dx, dy, dz);
                        Block block = world.getBlockState(checkPos).getBlock();

                        if (block instanceof FuelGeneratorBaseSlaveBlock ||
                                block instanceof FuelGeneratorEnergySlaveBlock ||
                                block instanceof FuelGeneratorInventorySlaveBlock ||
                                block instanceof FuelGeneratorRedstoneSlaveBlock) {
                            world.removeBlock(checkPos, false);
                        }
                    }
                }
            }
          //  removeSlaveBlocks(); // Will implement later for specific removal logic
        }
        super.onBreak(world, pos, state, player);
    }
    @Override
    protected void removeSlaveBlocks(World world, BlockPos pos) {

    }

}
