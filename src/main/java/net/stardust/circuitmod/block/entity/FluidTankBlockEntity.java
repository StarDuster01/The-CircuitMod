package net.stardust.circuitmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity implements IFluidConsumer {

    private static final int MAX_FLUID_AMOUNT = 4000; // 4 buckets (1000 mB each)
    private int currentFluidAmount = 0;
    private String currentFluidType = null;

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK_BE, pos, state);
    }

    public void tick(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }

        // Debug statement to print the current fluid status
        System.out.println("FluidTankBlockEntity at " + pos + " contains " + currentFluidAmount + " mB of " + (currentFluidType == null ? "no fluid" : currentFluidType));
    }

    @Override
    public void addFluid(int fluidAmount, String fluidType) {
        if (currentFluidType == null) {
            currentFluidType = fluidType;
        }

        if (!currentFluidType.equals(fluidType)) {
            System.out.println("Cannot add " + fluidType + " to tank containing " + currentFluidType);
            return; // Cannot mix different types of fluids
        }

        currentFluidAmount += fluidAmount;
        if (currentFluidAmount > MAX_FLUID_AMOUNT) {
            currentFluidAmount = MAX_FLUID_AMOUNT; // Cap the fluid amount to the max capacity
        }

        System.out.println("Added " + fluidAmount + " mB of " + fluidType + " to tank. New amount: " + currentFluidAmount + " mB");

        markDirty(); // Mark the block entity as dirty to ensure it is saved correctly
        sync();
    }

    @Override
    public boolean canReceiveFluid(String fluidType) {
        boolean canReceive = (currentFluidAmount < MAX_FLUID_AMOUNT) && (currentFluidType == null || currentFluidType.equals(fluidType));
        System.out.println("Can receive " + fluidType + ": " + canReceive);
        return canReceive;
    }

    public int getCurrentFluidAmount() {
        return currentFluidAmount;
    }

    public String getCurrentFluidType() {
        return currentFluidType;
    }

    public void setCurrentFluidAmount(int amount) {
        currentFluidAmount = amount;
        if (currentFluidAmount == 0) {
            currentFluidType = null; // Reset the fluid type if the tank is empty
        }
        markDirty();
        sync();
    }

    public void setCurrentFluidType(String type) {
        currentFluidType = type;
        markDirty();
        sync();
    }

    public float getMaxFluidAmount() {
        return MAX_FLUID_AMOUNT;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("CurrentFluidAmount", currentFluidAmount);
        if (currentFluidType != null) {
            nbt.putString("CurrentFluidType", currentFluidType);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        currentFluidAmount = nbt.getInt("CurrentFluidAmount");
        currentFluidType = nbt.getString("CurrentFluidType");
        if (currentFluidType.isEmpty()) {
            currentFluidType = null;
        }
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

    private void sync() {
        if (this.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.world;
            serverWorld.getPlayers().forEach(player -> {
                ModMessages.sendFluidTankUpdate((ServerPlayerEntity) player, this.getPos(), this.currentFluidAmount, this.currentFluidType);
            });
        }
    }
}
