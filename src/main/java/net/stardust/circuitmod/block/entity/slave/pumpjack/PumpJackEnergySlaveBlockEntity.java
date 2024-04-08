package net.stardust.circuitmod.block.entity.slave.pumpjack;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PumpJackEnergySlaveBlockEntity extends BlockEntity implements IEnergyConsumer {
    public PumpJackEnergySlaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUMP_JACK_ENERGY_SLAVE_BE,pos, state);
    }

    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
        markDirty();
    }
    private BlockPos masterPos;

    private int oilLevel = 0; // Oil level in the tank

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        BlockEntity masterBlockEntity = world.getBlockEntity(masterPos);
        if (!(masterBlockEntity instanceof PumpJackBlockEntity)) {
       //     System.out.println("Master block entity is not a PumpJackBlockEntity or is null");
            return;
        }
        PumpJackBlockEntity master = (PumpJackBlockEntity) masterBlockEntity;

        for (PlayerEntity playerEntity : world.getPlayers()) {
            if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20 * 20) {
                ModMessages.sendPumpJackUpdate((ServerPlayerEntity) playerEntity, pos, directEnergy, oilLevel);
            }
        }
        // Synchronize oil level
        if (masterBlockEntity instanceof PumpJackBlockEntity) {
            // Synchronize oil level
            this.oilLevel = master.getOilLevel();
     //       System.out.println("Energy Slave: Energy = " + this.directEnergy + ", Oil Level = " + this.oilLevel);
            markDirty();
        }
            markDirty();
        }
    private int max_oil = 64800;
    private static final int MAX_DIRECT_ENERGY = 100000;

    public void setEnergy(int energy) {
        this.directEnergy = Math.min(energy, MAX_DIRECT_ENERGY);
        markDirty();
    }

    private int directEnergy = 0;
    public void addEnergy(int amount) {
        directEnergy += amount;
        directEnergy = Math.min(directEnergy, MAX_DIRECT_ENERGY);
    }
    public void reduceEnergy(int amount) {
        directEnergy = Math.max(0, directEnergy - amount);
        markDirty();
    }
    public int getDirectEnergy() {
        return this.directEnergy;
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("pump_jack_energy_slave.energy", directEnergy);
        if (masterPos != null) {
            nbt.putInt("MasterPosX", masterPos.getX());
            nbt.putInt("MasterPosY", masterPos.getY());
            nbt.putInt("MasterPosZ", masterPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("fuel_generator_energy_slave.energy")) {
            directEnergy = nbt.getInt("fuel_generator_energy_slave.energy");
        }
        if (nbt.contains("MasterPosX") && nbt.contains("MasterPosY") && nbt.contains("MasterPosZ")) {
            int x = nbt.getInt("MasterPosX");
            int y = nbt.getInt("MasterPosY");
            int z = nbt.getInt("MasterPosZ");
            masterPos = new BlockPos(x, y, z);
        }
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }


}

