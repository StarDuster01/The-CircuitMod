package net.stardust.circuitmod.block.entity.slave;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PCBStationBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PCBStationBaseSlaveBlockEntity extends AbstractTechSlaveBlockEntity implements ExtendedScreenHandlerFactory {
    public PCBStationBaseSlaveBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.PCBSTATION_BASE_SLAVE_BE, pos, state);
        }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        BlockPos masterPos = getMasterPos();
        if (masterPos != null) {
            buf.writeBlockPos(masterPos);
        }
    }

    @Override
    public Text getDisplayName() {
        // Assuming you have a similar method in your master block entity
        BlockPos masterPos = getMasterPos();
        if (masterPos != null) {
            World world = getWorld();
            if (world != null) {
                BlockEntity masterEntity = world.getBlockEntity(masterPos);
                if (masterEntity instanceof PCBStationBlockEntity) {
                    return ((PCBStationBlockEntity) masterEntity).getDisplayName();
                }
            }
        }
        return Text.literal(""); // Fallback display name or retrieve from master
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // Delegate to the master block entity
        World world = player.getEntityWorld();
        BlockPos masterPos = getMasterPos();
        if (masterPos != null) {
            BlockEntity masterEntity = world.getBlockEntity(masterPos);
            if (masterEntity instanceof PCBStationBlockEntity) {
                return ((PCBStationBlockEntity) masterEntity).createMenu(syncId, playerInventory, player);
            }
        }
        return null;
    }


}
