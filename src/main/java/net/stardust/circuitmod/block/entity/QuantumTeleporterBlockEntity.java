package net.stardust.circuitmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.QuantumTeleporterScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

public class QuantumTeleporterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IEnergyConsumer {

    protected final PropertyDelegate propertyDelegate;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private long energyStored = 0; // Replace energyStorage with this
    private static final long MAX_ENERGY = 1000000;


    public static int EnergyPerBlock = 10;

    public static int getEnergyPerBlock() {
        return EnergyPerBlock;
    }
    public void setEnergyStored(long energy) {
        this.energyStored = energy;
        markDirty(); // Mark the block entity as dirty to ensure the change is saved
    }
    public long getEnergyStored() {
        return this.energyStored;
    }






    public QuantumTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_TELEPORTER_BLOCK_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 0)
                    return (int) energyStored;
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    private int tickCounter = 0;




    public void tick(World world, BlockPos pos, BlockState state) {
        tickCounter++;
        if(!world.isClient) {
            for (PlayerEntity playerEntity : world.getPlayers()) {
                if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20*20) {
                    ModMessages.sendQuantumTeleporterUpdate((ServerPlayerEntity) playerEntity, pos, energyStored);
                }
            }
        }
        if (this.energyStored < MAX_ENERGY) {
            markDirty(world, pos, state);
        }
        System.out.println("QT has energy" + energyStored);
    }



    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    @Override
    public Text getDisplayName() {
        return Text.literal("Quantum Teleporter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new QuantumTeleporterScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("quantum_teleporter.energy", this.energyStored);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        if(nbt.contains("quantum_teleporter.energy")) {
            this.energyStored = nbt.getLong("quantum_teleporter.energy");
        }
    }

    @Override
    public void addEnergy(int energy) {
        this.energyStored += energy;
        if (this.energyStored > MAX_ENERGY) {
            this.energyStored = MAX_ENERGY;
        }
    }
}
