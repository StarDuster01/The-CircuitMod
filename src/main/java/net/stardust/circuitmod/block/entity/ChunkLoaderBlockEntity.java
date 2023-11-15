package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class ChunkLoaderBlockEntity extends BlockEntity {

    public ChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER_BE, pos, state);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld) {
            loadChunks((ServerWorld) world, pos);
            System.out.println("Ticking after LoadChunks method");
        }
    }

    private void loadChunks(ServerWorld world, BlockPos pos) {
        System.out.println("loadChunks MEthod Called");
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkTicketType<ChunkPos> ticketType = ChunkTicketType.create("chunk_loader", Comparator.comparingLong(ChunkPos::toLong));
        world.getChunkManager().addTicket(ticketType, chunkPos, 2, chunkPos);
        System.out.println("Loading chunk at: " + chunkPos);

        // Load surrounding chunks
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // Skip the current chunk
                ChunkPos surroundingPos = new ChunkPos(pos.getX() >> 4 + dx, pos.getZ() >> 4 + dz);
                world.getChunkManager().addTicket(ticketType, surroundingPos, 2, surroundingPos);
                System.out.println("Loading surrounding chunk at: " + surroundingPos);
            }
        }
    }


    @Override
    public void markRemoved() {
        super.markRemoved();
        if (this.world instanceof ServerWorld) {
            unloadChunks((ServerWorld) this.world, this.pos);
        }
    }
    private void unloadChunks(ServerWorld world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkTicketType<ChunkPos> ticketType = ChunkTicketType.create("chunk_loader", Comparator.comparingLong(ChunkPos::toLong));
        world.getChunkManager().removeTicket(ticketType, chunkPos, 2, chunkPos);
        System.out.println("Unloading chunk at: " + chunkPos);

        // Unload surrounding chunks
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // Skip the current chunk
                ChunkPos surroundingPos = new ChunkPos(pos.getX() >> 4 + dx, pos.getZ() >> 4 + dz);
                world.getChunkManager().removeTicket(ticketType, surroundingPos, 2, surroundingPos);
                System.out.println("Unloading surrounding chunk at: " + surroundingPos);
            }
        }
    }


}
