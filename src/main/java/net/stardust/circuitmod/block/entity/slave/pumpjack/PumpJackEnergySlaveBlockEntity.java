package net.stardust.circuitmod.block.entity.slave.pumpjack;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.FuelGeneratorBlockEntity;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.PumpJackBlockEntity;
import net.stardust.circuitmod.networking.ModMessages;

import java.util.List;

public class PumpJackEnergySlaveBlockEntity extends BlockEntity {
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
            System.out.println("Master block entity is not a PumpJackBlockEntity or is null");
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
            System.out.println("Energy Slave: Energy = " + this.directEnergy + ", Oil Level = " + this.oilLevel);
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
        // Ensure the energy does not exceed the capacity
        directEnergy = Math.min(directEnergy, MAX_DIRECT_ENERGY);
    }
    private void useEnergyForOil() {
        final int energyUsage = 10;
        final int oilProduction = 100; // Amount of oil produced per cycle

        if (directEnergy >= energyUsage) {
            int producibleOil = Math.min(oilProduction, max_oil - oilLevel); // Calculate the amount of oil that can be produced without exceeding the max
            if (producibleOil > 0) {
                // Increase oil level and decrease energy
                oilLevel += producibleOil;
                directEnergy -= energyUsage;
            }
        }
    }

    public int getDirectEnergy() {
        return this.directEnergy;
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }
}

