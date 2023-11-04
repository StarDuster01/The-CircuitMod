package net.stardust.circuitmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.slave.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.ModBlocks;
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
            Direction facing = state.get(FACING);
            BlockPos slavePos = pos.offset(facing).up(2);
            if (world.getBlockState(slavePos).getBlock() instanceof EfficientCoalGeneratorEnergySlaveBlock) {
                world.removeBlock(slavePos, false);
            }
        }
        super.onBreak(world, pos, state, player);
    }

}

