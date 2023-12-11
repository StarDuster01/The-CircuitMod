package net.stardust.circuitmod.block.custom.slave;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTechSlaveBlock extends BlockWithEntity {
    protected AbstractTechSlaveBlock(Settings settings) {
        super(settings);
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    protected abstract BlockPos findMaster(BlockPos slavePos, World world);
    protected abstract Class<? extends BlockEntity> getMasterBlockEntityClass();

    // Common functionality for interacting with the master block upon right-click
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos masterPos = findMaster(pos, world);
            BlockEntity masterEntity = world.getBlockEntity(masterPos);

            if (getMasterBlockEntityClass().isInstance(masterEntity)) {
                player.openHandledScreen((NamedScreenHandlerFactory) masterEntity);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    // Common functionality for removing the master block when the slave block is broken
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            // Debug logging
            System.out.println("Slave block at " + pos + " is being broken");

            BlockPos masterPos = findMaster(pos, world);

            // Debug logging
            System.out.println("Master block expected at " + masterPos);

            if (masterPos != null) {
                BlockState masterState = world.getBlockState(masterPos);
                Block masterBlock = masterState.getBlock();

                // Further debug logging
                System.out.println("Block at master position is: " + masterBlock);

                if (getMasterBlockEntityClass().isInstance(world.getBlockEntity(masterPos))) {
                    // Debug logging
                    System.out.println("Master block confirmed. Proceeding with removal.");

                    world.breakBlock(masterPos, true); // Directly break the block and drop items
                } else {
                    // Debug logging
                    System.out.println("The entity at master position is not an instance of the master's block entity class.");
                }
            } else {
                // Debug logging
                System.out.println("Master position is null. Master not found.");
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
