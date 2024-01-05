package net.stardust.circuitmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlock;
import net.stardust.circuitmod.block.custom.slave.pumpjack.PumpJackEnergySlaveBlock;
import net.stardust.circuitmod.block.custom.slave.pumpjack.PumpJackExtraSlaveBlock;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PCBStationBlockEntity;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorBaseSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.efficientcoalgenerator.EfficientCoalGeneratorRedstoneSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackExtraSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PumpJackBlock extends BlockWithEntity {
    public PumpJackBlock(Settings settings) {
        super(settings.luminance((state) -> 10));
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }
    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing).with(LIT, false);

        // New logic for checking placement validity
        List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, facing);
        boolean areaClear = slaveBlockPositions.stream().allMatch(blockPos ->
                world.isAir(blockPos) || world.getBlockState(blockPos).canReplace(ctx)
        );

        if (!world.isClient && !areaClear) {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                player.sendMessage(Text.literal("The area is not clear for the Pump Jack. Please clear any obstructing blocks."), false);
            }
            return null; // Cancel the block placement by returning null
        }

        // Existing logic for placing slave blocks
        if (!world.isClient) {
            BlockPos extraSlavePos = pos.offset(facing, 2);
            BlockPos energySlavePos = pos.offset(facing.getOpposite(), 3);

            // Place Energy Slave Block
            placeEnergySlaveBlock(world, ctx, energySlavePos, pos);

            // Place Extra Slave Block
            placeExtraSlaveBlock(world, ctx, extraSlavePos, pos);
        }
        return state;
    }
    private List<BlockPos> calculateSlaveBlockPositions(BlockPos masterPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();
        // Adjust these offsets according to your slave block positions
        positions.add(masterPos.offset(facing, 2));
        positions.add(masterPos.offset(facing.getOpposite(), 3));
        return positions;
    }

    protected void placeEnergySlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        // Check if we can place the energy slave block
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState energySlaveBlockState = ModBlocks.PUMP_JACK_ENERGY_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, energySlaveBlockState, 3);
            PumpJackEnergySlaveBlockEntity energySlaveEntity = (PumpJackEnergySlaveBlockEntity) world.getBlockEntity(slavePos);
            if (energySlaveEntity != null) {
                energySlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed a pumpjack EnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place a pupmjack EnergySlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }
    protected void placeExtraSlaveBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        // Check if we can place the energy slave block
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState energySlaveBlockState = ModBlocks.PUMP_JACK_EXTRA_SLAVE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, energySlaveBlockState, 3);
            PumpJackExtraSlaveBlockEntity energySlaveEntity = (PumpJackExtraSlaveBlockEntity) world.getBlockEntity(slavePos);
            if (energySlaveEntity != null) {
                energySlaveEntity.setMasterPos(masterPos);
            }

            System.out.println("Placed a pumpjack EnergySlaveBlock at " + slavePos);
        } else {

            System.out.println("Could not place a pupmjack EnergySlaveBlock at " + slavePos + " as the position is not replaceable");
        }

    }



    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PumpJackBlockEntity(pos, state);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.PUMP_JACK_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            // Calculate the positions of the slave blocks
            List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, state.get(FACING));

            // Break each slave block
            for (BlockPos slavePos : slaveBlockPositions) {
                BlockState slaveState = world.getBlockState(slavePos);
                if (slaveState.getBlock() instanceof PumpJackEnergySlaveBlock || slaveState.getBlock() instanceof PumpJackExtraSlaveBlock) {
                    world.breakBlock(slavePos, true, player);
                }
            }

            // If the player is not in creative mode, drop the item
            if (!player.isCreative()) {
                Item item = asItem();
                ItemStack itemStack = new ItemStack(item);
                Block.dropStack(world, pos, itemStack);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((PumpJackBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

}

