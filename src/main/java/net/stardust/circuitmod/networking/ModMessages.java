package net.stardust.circuitmod.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.entity.EfficientCoalGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.QuarryBlockEntity;
import net.stardust.circuitmod.screen.QuarryScreen;
import org.joml.Vector2i;

public class ModMessages {

    public static final Identifier QUARRY_UPDATE_ID = new Identifier(CircuitMod.MOD_ID, "quarry_update");
    public static final Identifier QUARRY_AREA_UPDATE_ID = new Identifier(CircuitMod.MOD_ID, "quarry_area_update");
    public static final Identifier TOGGLE_MINING_ID = new Identifier(CircuitMod.MOD_ID, "toggle_mining");
    public static final Identifier CHANGE_QUARRY_MINING_AREA_ID = new Identifier(CircuitMod.MOD_ID, "change_quarry_mining_area");
    private static final Identifier COAL_GENERATOR_FUEL_LEVEL_UPDATE_ID = new Identifier(CircuitMod.MOD_ID, "coal_generator_fuel_update");


    public static void sendQuarryUpdate(ServerPlayerEntity player, BlockPos pos, long energy, boolean isMiningActive) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeLong(energy);
        buf.writeBoolean(isMiningActive);
        ServerPlayNetworking.send(player, QUARRY_UPDATE_ID, buf);

    }
    public static void sendQuarryAreaUpdate(ServerPlayerEntity player, BlockPos pos, Vector2i dimensions) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeInt(dimensions.x);
        buf.writeInt(dimensions.y);
        ServerPlayNetworking.send(player, QUARRY_AREA_UPDATE_ID, buf);
    }

    public static void registerC2SPackets() {
        ClientPlayNetworking.registerGlobalReceiver(QUARRY_UPDATE_ID, (client, player, buf, sender) -> {
            BlockPos blockPos = buf.readBlockPos();
            long energy = buf.readLong();
            boolean miningActive = buf.readBoolean();

            client.execute(() -> {
                World clientWorld = MinecraftClient.getInstance().world;
                if (clientWorld != null) {
                    BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);
                    if (blockEntity instanceof QuarryBlockEntity) {
                        ((QuarryBlockEntity) blockEntity).energyStorage.setAmountDirectly(energy);
                        ((QuarryBlockEntity) blockEntity).setMiningActive(miningActive);
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.QUARRY_AREA_UPDATE_ID, (client, handler, buf, responseSender) -> {
            BlockPos blockPos = buf.readBlockPos();
            Vector2i dimensions = new Vector2i(buf.readInt(), buf.readInt());

            client.execute(() -> {
                QuarryScreen quarryScreen = null;
                if (MinecraftClient.getInstance().currentScreen instanceof QuarryScreen) {
                    quarryScreen = (QuarryScreen) MinecraftClient.getInstance().currentScreen;

                } else {

                }

                if (quarryScreen != null) {
                    quarryScreen.updateMiningAreaDimensions(dimensions);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_MINING_ID, (server, player, handler, buf, sender) -> {
            BlockPos blockPos = buf.readBlockPos();
            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof QuarryBlockEntity) {
                    ((QuarryBlockEntity) blockEntity).setMiningActive(!((QuarryBlockEntity) blockEntity).isMiningActive());
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(ModMessages.CHANGE_QUARRY_MINING_AREA_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos blockPos = buf.readBlockPos();
            Vector2i dimensions = new Vector2i(buf.readInt(), buf.readInt());
            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof QuarryBlockEntity) {
                    ((QuarryBlockEntity) blockEntity).setMiningAreaDimensions(dimensions);
                }
            });
        });
    }
    public static void registerS2CPackets() {
    }


}

