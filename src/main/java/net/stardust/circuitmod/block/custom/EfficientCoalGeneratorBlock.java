package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
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
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EfficientCoalGeneratorBlock extends AbstractGeneratorBlock{
    public EfficientCoalGeneratorBlock(Settings settings) {
        super(settings.luminance((state) -> 10)  // Replace '3' with desired light level for faint glow
        );
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing).with(LIT,false);

        if (!world.isClient) {
            BlockPos backPos = pos.offset(facing.getOpposite());
            BlockPos frontPos = pos.offset(facing);
            BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
            BlockPos rightPos = pos.offset(facing.rotateYClockwise());

            BlockPos backPosup = pos.offset(facing.getOpposite()).up();
           // BlockPos frontPosup = pos.offset(facing).up();
            BlockPos leftPosup = pos.offset(facing.rotateYCounterclockwise()).up();
            BlockPos rightPosup = pos.offset(facing.rotateYClockwise()).up();

            placeBaseSlaveBlock(world, ctx, backPos, pos);
            placeBaseSlaveBlock(world, ctx, frontPos, pos);
            placeBaseSlaveBlock(world, ctx, leftPos, pos);
            placeBaseSlaveBlock(world, ctx, rightPos, pos);
            placeBaseSlaveBlock(world, ctx, backPosup, pos);
            placeBaseSlaveBlock(world, ctx, leftPosup, pos);
            placeBaseSlaveBlock(world, ctx, rightPosup, pos);
            placeBaseSlaveBlock(world, ctx, pos.up(), pos); // Places a base slave block directly above the main block




// Place diagonal blocks (front-left, front-right, back-left, back-right)
            BlockPos frontLeftPos = leftPos.offset(facing);
            BlockPos frontRightPos = rightPos.offset(facing);
            BlockPos backLeftPos = leftPos.offset(facing.getOpposite());
            BlockPos backRightPos = rightPos.offset(facing.getOpposite());
            BlockPos frontLeftPosup = leftPos.offset(facing).up();
            BlockPos frontRightPosup = rightPos.offset(facing).up();
            BlockPos backLeftPosup = leftPos.offset(facing.getOpposite()).up();
            BlockPos backRightPosup = rightPos.offset(facing.getOpposite()).up();

            placeBaseSlaveBlock(world, ctx, frontLeftPos, pos);
            placeBaseSlaveBlock(world, ctx, frontRightPos, pos);
            placeBaseSlaveBlock(world, ctx, backLeftPos, pos);
            placeBaseSlaveBlock(world, ctx, backRightPos, pos);
            placeBaseSlaveBlock(world, ctx, frontLeftPosup, pos);
            placeBaseSlaveBlock(world, ctx, frontRightPosup, pos);
            placeBaseSlaveBlock(world, ctx, backLeftPosup, pos);
            placeBaseSlaveBlock(world, ctx, backRightPosup, pos);
            BlockPos aboveMainPos = pos.up();

            // Position for the energy slave block
            BlockPos energySlavePos = pos.offset(facing).up(2);
            // Check if we can place the energy slave block
            if (world.isAir(energySlavePos) || world.getBlockState(energySlavePos).canReplace(ctx)) {
                BlockState energySlaveBlockState = ModBlocks.EFFICIENT_COAL_GENERATOR_ENERGY_SLAVE_BLOCK.getDefaultState();
                world.setBlockState(energySlavePos, energySlaveBlockState, 3);
                EfficientCoalGeneratorEnergySlaveBlockEntity energySlaveEntity = (EfficientCoalGeneratorEnergySlaveBlockEntity) world.getBlockEntity(energySlavePos);
                if (energySlaveEntity != null) {
                    energySlaveEntity.setMasterPos(pos);
                }

                System.out.println("Placed an EfficientCoalGeneratorEnergySlaveBlock at " + energySlavePos);
            } else {

                System.out.println("Could not place an EfficientCoalGeneratorEnergySlaveBlock at " + energySlavePos + " as the position is not replaceable");
            }
            // Position for the inventory slave block (one block below the energy slave block)
            BlockPos inventorySlavePos = pos.offset(facing.getOpposite()).offset(facing.getOpposite()).up();
            // Check if we can place the inventory slave block
            if (world.isAir(inventorySlavePos) || world.getBlockState(inventorySlavePos).canReplace(ctx)) {
                BlockState inventorySlaveBlockState = ModBlocks.EFFICIENT_COAL_GENERATOR_INVENTORY_SLAVE_BLOCK.getDefaultState();
                world.setBlockState(inventorySlavePos, inventorySlaveBlockState, 3);
                EfficientCoalGeneratorInventorySlaveBlockEntity inventorySlaveEntity = (EfficientCoalGeneratorInventorySlaveBlockEntity) world.getBlockEntity(inventorySlavePos);
                if (inventorySlaveEntity != null) {
                    inventorySlaveEntity.setMasterPos(pos);
                }
                System.out.println("Placed an EfficientCoalGeneratorInventorySlaveBlock at " + inventorySlavePos);
            } else {
                System.out.println("Could not place an EfficientCoalGeneratorInventorySlaveBlock at " + inventorySlavePos + " as the position is not replaceable");
            }
            BlockPos redstoneSlavePos = energySlavePos.down();
            // Check if we can place the inventory slave block
            if (world.isAir(redstoneSlavePos) || world.getBlockState(redstoneSlavePos).canReplace(ctx)) {
                BlockState redstoneSlaveBlockState = ModBlocks.EFFICIENT_COAL_GENERATOR_REDSTONE_SLAVE_BLOCK.getDefaultState();
                world.setBlockState(redstoneSlavePos, redstoneSlaveBlockState, 3);
                EfficientCoalGeneratorRedstoneSlaveBlockEntity redstoneSlaveEntity = (EfficientCoalGeneratorRedstoneSlaveBlockEntity) world.getBlockEntity(redstoneSlavePos);
                if (redstoneSlaveEntity != null) {
                    redstoneSlaveEntity.setMasterPos(pos);
                }
                System.out.println("Placed an EfficientCoalGeneratorRedstoneSlaveBlock at " + inventorySlavePos);
            } else {
                System.out.println("Could not place an EfficientCoalGeneratorRedstoneSlaveBlock at " + inventorySlavePos + " as the position is not replaceable");
            }

            // Final top layer so it does not override important slave blocks
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos gridPos = aboveMainPos.add(x, 1, z);
                    placeBaseSlaveBlock(world, ctx, gridPos, pos);
                }
            }
        }
        return state;
    }

    private void placeBaseSlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState baseSlaveBlockState = ModBlocks.EFFICIENT_COAL_GENERATOR_BASE_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, baseSlaveBlockState, 3);
            world.updateNeighborsAlways(slavePos, baseSlaveBlockState.getBlock()); // This should fix lighting issue?
            EfficientCoalGeneratorBaseSlaveBlockEntity baseSlaveEntity = (EfficientCoalGeneratorBaseSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (baseSlaveEntity != null) {
                baseSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed an EfficientCoalGeneratorBaseSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place an EfficientCoalGeneratorBaseSlaveBlock at " + slavePos + " as the position is not replaceable");
        }
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EfficientCoalGeneratorBlockEntity(pos, state);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.EFFICIENT_COAL_GENERATOR_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            // Drop the item if the player is not in creative mode
            if (!player.isCreative()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                Item item = asItem();
                ItemStack itemStack = new ItemStack(item);
                Block.dropStack(world, pos, itemStack);
            }

            // Define the range for the 3-block radius
            int radius = 2;

            // Iterate over the cube defined by the radius
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos checkPos = pos.add(dx, dy, dz);
                        Block block = world.getBlockState(checkPos).getBlock();

                        // Check if the block is one of the slave blocks
                        if (block instanceof EfficientCoalGeneratorBaseSlaveBlock ||
                                block instanceof EfficientCoalGeneratorEnergySlaveBlock ||
                                block instanceof EfficientCoalGeneratorInventorySlaveBlock||
                                block instanceof EfficientCoalGeneratorRedstoneSlaveBlock) {
                            world.removeBlock(checkPos, false);
                        }
                    }
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    protected void placeBaseSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {

    }

    @Override
    protected void placeEnergySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {

    }

    @Override
    protected void placeInventorySlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {

    }

    @Override
    protected void placeRedstoneSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {

    }

    @Override
    protected void removeSlaveBlocks(World world, BlockPos pos) {

    }
}

