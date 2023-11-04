package net.stardust.circuitmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.slave.EfficientCoalGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.EfficientCoalGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.EfficientCoalGeneratorInventorySlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EfficientCoalGeneratorBlock extends BlockWithEntity{
    public EfficientCoalGeneratorBlock(Settings settings) {
        super(settings);
    }
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing);

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
            BlockPos inventorySlavePos = energySlavePos.down();
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
        }
        return state;
    }

    private void placeBaseSlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState baseSlaveBlockState = ModBlocks.EFFICIENT_COAL_GENERATOR_BASE_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, baseSlaveBlockState, 3);
            EfficientCoalGeneratorBaseSlaveBlockEntity baseSlaveEntity = (EfficientCoalGeneratorBaseSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (baseSlaveEntity != null) {
                baseSlaveEntity.setMasterPos(masterPos);
            }
            System.out.println("Placed an EfficientCoalGeneratorBaseSlaveBlock at " + slavePos);
        } else {
            System.out.println("Could not place an EfficientCoalGeneratorBaseSlaveBlock at " + slavePos + " as the position is not replaceable");
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EfficientCoalGeneratorBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.EFFICIENT_COAL_GENERATOR_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {

            if (!player.isCreative()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                Item item = asItem(); // This gets the item form of the block

                // Drop the item with the default item-drop behavior (spawns the item in the world)
                ItemStack itemStack = new ItemStack(item);
                // Optionally, you can add NBT data to the ItemStack if needed
                // itemStack.setTag(blockEntity.createNbt());

                Block.dropStack(world, pos, itemStack);
            }
            Direction facing = state.get(FACING);

            BlockPos[] relativeSlavePositions = new BlockPos[]{
                    pos.offset(facing.getOpposite()),
                    pos.offset(facing),
                    pos.offset(facing.rotateYCounterclockwise()),
                    pos.offset(facing.rotateYClockwise()),
                    pos.offset(facing.getOpposite()).up(),
                    pos.offset(facing.rotateYCounterclockwise()).up(),
                    pos.offset(facing.rotateYClockwise()).up(),

                    // Diagonals at the base level
                    pos.offset(facing.rotateYCounterclockwise()).offset(facing),
                    pos.offset(facing.rotateYClockwise()).offset(facing),
                    pos.offset(facing.rotateYCounterclockwise()).offset(facing.getOpposite()),
                    pos.offset(facing.rotateYClockwise()).offset(facing.getOpposite()),

                    // Diagonals one level up
                    pos.offset(facing.rotateYCounterclockwise()).offset(facing).up(),
                    pos.offset(facing.rotateYClockwise()).offset(facing).up(),
                    pos.offset(facing.rotateYCounterclockwise()).offset(facing.getOpposite()).up(),
                    pos.offset(facing.rotateYClockwise()).offset(facing.getOpposite()).up(),

                    // Energy slave block position (two levels up from the master block in the facing direction)
                    pos.offset(facing).up(2),

                    // Inventory slave block position (directly below the energy slave block)
                    pos.offset(facing).up()
            };

            // Remove the block at each slave position
            for (BlockPos slavePos : relativeSlavePositions) {
                if (world.getBlockState(slavePos).getBlock() instanceof EfficientCoalGeneratorBaseSlaveBlock ||
                        world.getBlockState(slavePos).getBlock() instanceof EfficientCoalGeneratorEnergySlaveBlock ||
                        world.getBlockState(slavePos).getBlock() instanceof EfficientCoalGeneratorInventorySlaveBlock) {
                    world.removeBlock(slavePos, false);
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }
}

