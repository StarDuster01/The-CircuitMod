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
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorRedstoneSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FuelGeneratorBlock extends AbstractGeneratorBlock{
    public FuelGeneratorBlock(Settings settings) {
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
// Special Slave Blocks
            BlockPos energyPos = pos.offset(facing).up();
            BlockPos inventoryPos = pos.offset(facing.rotateYCounterclockwise()).up().offset(facing);
            BlockPos redstonePos = pos.offset(facing.rotateYCounterclockwise()).up().offset(facing.rotateYCounterclockwise()).offset(facing);

            placeEnergySlaveBlocks(world, ctx, energyPos, pos); // Energy Slave Block
            placeInventorySlaveBlocks(world, ctx, inventoryPos, pos); // Item Slave Block
            placeRedstoneSlaveBlocks(world, ctx, redstonePos, pos); // Redstone Slave Block

// Slaves underneath the special slave blocks
            BlockPos underenergyPos = energyPos.down();
            BlockPos underinventoryPos = inventoryPos.down();
            BlockPos underredstonePos = redstonePos.down();
            placeBaseSlaveBlocks(world, ctx, underenergyPos, pos); // Underneath the Energy Slave Block
            placeBaseSlaveBlocks(world, ctx, underinventoryPos, pos); // Underneath the Item Slave Block
            placeBaseSlaveBlocks(world, ctx, underredstonePos, pos); // Underneath the Redstone Slave Block

            // Slaves making top layer behind energy
            placeBaseSlaveBlocks(world, ctx, energyPos.offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            // Slaves making top layer behind inventory
            placeBaseSlaveBlocks(world, ctx, inventoryPos.offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            // Slaves making top layer behind redstone
            placeBaseSlaveBlocks(world, ctx, redstonePos.offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);



            // slaves behind under special
            placeBaseSlaveBlocks(world, ctx, underenergyPos.offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underinventoryPos.offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underredstonePos.offset(facing.getOpposite()), pos);

            //slaves making bottom layer behind under energy
            placeBaseSlaveBlocks(world, ctx, underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            //slaves making bottom layer behind under inventory
            placeBaseSlaveBlocks(world, ctx, underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            //slaves making bottom layer behind under redstone
            placeBaseSlaveBlocks(world, ctx, underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
            placeBaseSlaveBlocks(world, ctx, underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()), pos);
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

            System.out.println("Placed a Fuel GeneratorEnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place a Fuel GeneratorEnergySlaveBlock at " + slavePos + " as the position is not replaceable");
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
            System.out.println("Placed a FuelGeneratorEnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place a FuelGeneratorEnergySlaveBlock at " + slavePos + " as the position is not replaceable");
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
            Direction facing = state.get(Properties.HORIZONTAL_FACING);
            // Handling item drop if not in creative mode
            if (!player.isCreative()) {
                Item item = asItem();
                ItemStack itemStack = new ItemStack(item);
                Block.dropStack(world, pos, itemStack);
            }
            BlockPos energyPos = pos.offset(facing).up();
            BlockPos inventoryPos = pos.offset(facing.rotateYCounterclockwise()).up().offset(facing);
            BlockPos redstonePos = pos.offset(facing.rotateYCounterclockwise()).up().offset(facing.rotateYCounterclockwise()).offset(facing);
            BlockPos underenergyPos = energyPos.down();
            BlockPos underinventoryPos = inventoryPos.down();
            BlockPos underredstonePos = redstonePos.down();
            BlockPos[] SlavePositions = {
                    energyPos,
                    inventoryPos,
                    redstonePos,
                    underenergyPos,
                    underinventoryPos,
                    underredstonePos,
                    energyPos.offset(facing.getOpposite()),
                    energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    energyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    inventoryPos.offset(facing.getOpposite()),
                    inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    inventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    redstonePos.offset(facing.getOpposite()),
                    redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    redstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underenergyPos.offset(facing.getOpposite()),
                    underinventoryPos.offset(facing.getOpposite()),
                    underredstonePos.offset(facing.getOpposite()),
                    underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underenergyPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underinventoryPos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()),
                    underredstonePos.offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite()).offset(facing.getOpposite())
            };
            // Remove each slave block
            for(BlockPos slavePos : SlavePositions) {
                world.removeBlock(slavePos, false);
            }
        }
        super.onBreak(world, pos, state, player);
    }
    @Override
    protected void removeSlaveBlocks(World world, BlockPos pos) {

    }

}
