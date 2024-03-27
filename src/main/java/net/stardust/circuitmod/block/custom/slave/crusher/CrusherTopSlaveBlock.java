package net.stardust.circuitmod.block.custom.slave.crusher;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.custom.CrusherBlock;
import net.stardust.circuitmod.block.custom.slave.AbstractTechSlaveBlock;
import net.stardust.circuitmod.block.entity.CrusherBlockEntity;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherEnergySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherInventorySlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.crusher.CrusherTopSlaveBlockEntity;
import net.stardust.circuitmod.block.entity.slave.fuelgenerator.FuelGeneratorBaseSlaveBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrusherTopSlaveBlock extends BlockWithEntity {
    public CrusherTopSlaveBlock(Settings settings) {
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherTopSlaveBlockEntity(pos,state);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }



    protected BlockPos findMaster(BlockPos slavePos, World world) {
        BlockEntity be = world.getBlockEntity(slavePos);
        if (be instanceof CrusherTopSlaveBlockEntity) {
            return ((CrusherTopSlaveBlockEntity) be).getMasterPos();
        }
        return null;
    }



    protected Class<? extends BlockEntity> getMasterBlockEntityClass() {
        return CrusherBlockEntity.class;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        System.out.println("onUse called"); // Debug 1
        if (!world.isClient) {
            System.out.println("Server side"); // Debug 2
            BlockPos masterPos = findMaster(pos, world);
            System.out.println("Master position: " + masterPos); // Debug 3
            if (masterPos != null) {
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                System.out.println("Master entity class: " + masterEntity.getClass().getName()); // Debug 4
                if (masterEntity instanceof CrusherBlockEntity) {
                    System.out.println("Instance of CrusherBlockEntity"); // Debug 5
                    player.openHandledScreen((NamedScreenHandlerFactory)masterEntity);
                    return ActionResult.SUCCESS;
                } else {
                    System.out.println("Master entity is not an instance of CrusherBlockEntity"); // Debug 6
                }
            } else {
                System.out.println("Master position is null"); // Debug 7
            }
        } else {
            System.out.println("Client side"); // Debug 8
        }
        return ActionResult.CONSUME;
    }




    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CRUSHER_TOP_SLAVE_BE, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrusherTopSlaveBlockEntity) {
                BlockPos masterPos = ((CrusherTopSlaveBlockEntity) be).getMasterPos();

                if (masterPos != null) {
                    BlockState masterState = world.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof CrusherBlock) {
                        masterState.getBlock().onBreak(world, masterPos, masterState, player);
                        world.setBlockState(masterPos, Blocks.AIR.getDefaultState(), 3); // 3 for Block.UPDATE_ALL flag
                    }
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }
}
