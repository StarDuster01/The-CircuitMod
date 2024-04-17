package net.stardust.circuitmod.block.custom.oiltower;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerFuelBlockEntity;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerLubeBlockEntity;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerResidueBlockEntity;
import net.stardust.circuitmod.block.entity.oiltower.OilTowerResidueSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.GenericMachineFillerBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OilTowerResidueBlock extends BlockWithEntity implements BlockEntityProvider {



    public OilTowerResidueBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(STYLE, Style.BASE));

    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Style> STYLE = EnumProperty.of("style", Style.class);
    public enum Style implements StringIdentifiable {
        BASE("base"),
        MID("mid"),
        TOP("top");

        private final String name;

        Style(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }



    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OilTowerResidueBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getPlayer().getHorizontalFacing().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing);

        // New logic for checking placement validity
        List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, facing);
        boolean areaClear = slaveBlockPositions.stream().allMatch(blockPos ->
                world.isAir(blockPos) || world.getBlockState(blockPos).canReplace(ctx)
        );

        if (!world.isClient && !areaClear) {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                player.sendMessage(Text.literal("The area is not clear for the Oil Tower. Please clear any obstructing blocks."), false);
            }
            return null; // Cancel the block placement by returning null
        }

        if (!world.isClient) {

            BlockPos slavePos1 = pos.up();
            BlockPos slavePos2 = pos.down();
            BlockPos slavePos3 = pos.down().offset(facing.getOpposite());
            BlockPos slavePos4 = pos.down().offset(facing.rotateYClockwise());
            BlockPos slavePos5 = pos.down().offset(facing.getOpposite()).up();

            BlockPos slavePos6 = pos.offset(facing.getOpposite());
            BlockPos slavePos7 = pos.offset(facing.getOpposite()).offset(facing.rotateYClockwise());
            BlockPos slavePos8 = pos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).up();
            BlockPos slavePos9 = pos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).offset(facing);
            BlockPos slavePos10 = pos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).down();
            BlockPos slavePos11 = pos.up().offset(facing.rotateYClockwise());

            BlockPos slavePos12 = pos.up().offset(facing.rotateYClockwise()).up();
            BlockPos slavePos13 = pos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).up().up();
            BlockPos slavePos14 = pos.down().offset(facing.getOpposite()).up().up();



            BlockPos lubePos = pos.up(3);
            BlockPos fuelBlockPos = pos.up(6);


            BlockPos residueslavePos = pos.up().offset(facing.getOpposite());

            // Place Slave Blocks Here

           // placeSlaveBlocks(world, ctx, slavePos1, pos);
          //  placeSlaveBlocks(world, ctx, slavePos2, pos);
          //  placeSlaveBlocks(world, ctx, slavePos3, pos);
           // placeSlaveBlocks(world, ctx, slavePos4, pos);
           // placeSlaveBlocks(world, ctx, slavePos5, pos);

          //  placeSlaveBlocks(world, ctx, slavePos6, pos);
          //  placeSlaveBlocks(world, ctx, slavePos7, pos);
          //  placeSlaveBlocks(world, ctx, slavePos8, pos);
           // placeSlaveBlocks(world, ctx, slavePos9, pos);
           // placeSlaveBlocks(world, ctx, slavePos10, pos);
          //  placeSlaveBlocks(world, ctx, slavePos11, pos);
          //  placeSlaveBlocks(world, ctx, slavePos12, pos);
          //  placeSlaveBlocks(world, ctx, slavePos13, pos);
          //  placeSlaveBlocks(world, ctx, slavePos14, pos);

            placeResidueSlaveBlocks(world, ctx, residueslavePos, pos);
            placeLubeBlock(world, ctx, lubePos, pos);
            placeFuelBlock(world, ctx, fuelBlockPos, pos);



        }
        return state;
    }
    private List<BlockPos> calculateSlaveBlockPositions(BlockPos masterPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();

        // Directly including positions as specified in getPlacementState
        positions.add(masterPos.up()); // slavePos1
        positions.add(masterPos.down()); // slavePos2
        positions.add(masterPos.down().offset(facing.getOpposite())); // slavePos3
        positions.add(masterPos.down().offset(facing.rotateYClockwise())); // slavePos4
        positions.add(masterPos.down().offset(facing.getOpposite()).up()); // slavePos5
        positions.add(masterPos.offset(facing.getOpposite())); // slavePos6
        positions.add(masterPos.offset(facing.getOpposite()).offset(facing.rotateYClockwise())); // slavePos7
        positions.add(masterPos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).up()); // slavePos8
        positions.add(masterPos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).offset(facing)); // slavePos9
        positions.add(masterPos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).down()); // slavePos10
        positions.add(masterPos.up().offset(facing.rotateYClockwise())); // slavePos11
        positions.add(masterPos.up().offset(facing.rotateYClockwise()).up()); // slavePos12
        positions.add(masterPos.offset(facing.getOpposite()).offset(facing.rotateYClockwise()).up().up()); // slavePos13
        positions.add(masterPos.down().offset(facing.getOpposite()).up().up()); // slavePos14
        positions.add(masterPos.up().offset(facing.getOpposite())); // residueslavePos

        // Special Blocks
        positions.add(masterPos.up(3)); // Lube
        positions.add(masterPos.up(6)); // Fuel

        return positions;
    }


    protected void placeSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState baseSlaveBlockState = ModBlocks.GENERIC_MACHINE_FILLER_BLOCK.getDefaultState();
            world.setBlockState(slavePos, baseSlaveBlockState, 3);
            if (!world.isClient()) {
                BlockEntity be = world.getBlockEntity(slavePos);
                if (be instanceof GenericMachineFillerBlockEntity) {
                    ((GenericMachineFillerBlockEntity)be).setMasterPos(masterPos);
                }
            }
            world.updateNeighborsAlways(slavePos, baseSlaveBlockState.getBlock()); // This should fix lighting issue?
        }

    }
    protected void placeResidueSlaveBlocks(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState slaveBlockState = ModBlocks.OIL_TOWER_RESIDUE_SLAVE_BLOCK.getDefaultState(); // Adjust to your slave block's default state
            world.setBlockState(slavePos, slaveBlockState, 3);
            if (!world.isClient()) {
                BlockEntity be = world.getBlockEntity(slavePos);
                if (be instanceof OilTowerResidueSlaveBlockEntity) {
                    ((OilTowerResidueSlaveBlockEntity)be).setMasterPos(masterPos);
                }
            }
        }
    }
    protected void placeLubeBlock(World world, ItemPlacementContext ctx, BlockPos slavePos, BlockPos masterPos) {
        if (world.isAir(slavePos) || world.getBlockState(slavePos).canReplace(ctx)) {
            BlockState slaveBlockState = ModBlocks.OIL_TOWER_LUBE_BLOCK.getDefaultState();
            world.setBlockState(slavePos, slaveBlockState, 3);
            if (!world.isClient()) {
                BlockEntity be = world.getBlockEntity(slavePos);
                if (be instanceof OilTowerLubeBlockEntity) {
                    ((OilTowerLubeBlockEntity)be).setMasterPos(masterPos);
                }
            }
        }
    }
    protected void placeFuelBlock(World world, ItemPlacementContext ctx, BlockPos fuelBlockPos, BlockPos masterPos) {
        if (world.isAir(fuelBlockPos) || world.getBlockState(fuelBlockPos).canReplace(ctx)) {
            BlockState fuelBlockState = ModBlocks.OIL_TOWER_FUEL_BLOCK.getDefaultState();
            world.setBlockState(fuelBlockPos, fuelBlockState, 3);
            if (!world.isClient()) {
                BlockEntity be = world.getBlockEntity(fuelBlockPos);
                if (be instanceof OilTowerFuelBlockEntity) {
                    ((OilTowerFuelBlockEntity)be).setMasterPos(masterPos);
                }
            }
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, STYLE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            // Determine the facing direction of the master block
            Direction facing = state.get(Properties.HORIZONTAL_FACING);

            // List to hold the positions of all slave blocks
            List<BlockPos> slaveBlockPositions = calculateSlaveBlockPositions(pos, facing);

            // Loop over all positions and remove the blocks
            for(BlockPos slavePos : slaveBlockPositions) {
                    world.removeBlock(slavePos, false);
            }
            if (!player.isCreative()) {
                ItemStack itemStack = new ItemStack(asItem());
                Block.dropStack(world, pos, itemStack);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OIL_TOWER_RESIDUE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
