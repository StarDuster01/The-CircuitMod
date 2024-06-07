package net.stardust.circuitmod.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.OilTowerFrameBlockEntity;
import net.stardust.circuitmod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class OilTowerFrame extends BlockWithEntity implements BlockEntityProvider {
    public OilTowerFrame(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OilTowerFrameBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && (player.getStackInHand(hand).getItem() == ModItems.WRENCH || player.getStackInHand(hand).getItem() == ModItems.GOLDEN_WRENCH)) {
            BlockPos abovePos = pos.up();
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), Direction.UP, abovePos, hit.isInsideBlock());
            ItemPlacementContext placementContext = new ItemPlacementContext(player, hand, player.getStackInHand(hand), newHit);
            BlockState residueBlockState = ModBlocks.OIL_TOWER_RESIDUE_BLOCK.getPlacementState(placementContext);

            if (residueBlockState != null && world.setBlockState(abovePos, residueBlockState, 3)) {
                world.playSound(null, abovePos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    player.getStackInHand(hand).decrement(1);
                }
                world.removeBlock(pos, false);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
