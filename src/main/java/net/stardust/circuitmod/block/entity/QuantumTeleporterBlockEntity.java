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
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.QuantumTeleporterScreenHandler;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

public class QuantumTeleporterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, EnergyStorage {

    protected final PropertyDelegate propertyDelegate;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final SimpleSidedEnergyContainer energyContainer;

    public static int EnergyPerBlock = 10;

    public static int getEnergyPerBlock() {
        return EnergyPerBlock;
    }
    public class QuantumTeleporterEnergyStorage extends SimpleEnergyStorage {
        public QuantumTeleporterEnergyStorage(long capacity, long maxInsert, long maxExtract) {
            super(capacity, maxInsert, maxExtract);
        }

        public void setAmountDirectly(long newAmount) {
            this.amount = Math.min(newAmount, this.capacity);
        }
    }

    public final QuantumTeleporterEnergyStorage energyStorage = new QuantumTeleporterEnergyStorage(3600000, 100000, 2000) {
        @Override
        protected void onFinalCommit() {
            markDirty();
            if(world != null)
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    };



    public QuantumTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_TELEPORTER_BLOCK_BE, pos, state);
        this.energyContainer = new SimpleSidedEnergyContainer() {
            @Override
            public long getCapacity() {
                return 0;
            }

            @Override
            public long getMaxInsert(@Nullable Direction side) {
                return 0;
            }

            @Override
            public long getMaxExtract(@Nullable Direction side) {
                return 0;
            }
        };

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 0)
                    return (int) energyStorage.amount;
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


    private void extractEnergy(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(amount, transaction);
            markDirty();
            transaction.commit();
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        tickCounter++;
        if(!world.isClient) {
            for (PlayerEntity playerEntity : world.getPlayers()) {
                if (playerEntity instanceof ServerPlayerEntity && playerEntity.squaredDistanceTo(Vec3d.of(pos)) < 20*20) {
                    ModMessages.sendQuantumTeleporterUpdate((ServerPlayerEntity) playerEntity, pos, energyStorage.amount);
                }
            }
        }
        if (this.energyStorage.getAmount() < this.energyStorage.getCapacity()) {
            markDirty(world, pos, state);
        }
        System.out.println("QT has energy" + energyStorage.amount);
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
    public long insert(long maxAmount, TransactionContext transaction) {
        long inserted = energyStorage.insert(maxAmount, transaction);
        System.out.println("Energy inserted: " + inserted);
        if (inserted > 0) {

            markDirty();
        }
        return inserted;
    }
    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long extracted = energyStorage.extract(maxAmount, transaction);
        if (extracted > 0) {

            markDirty();
        }
        return extracted;
    }
    @Override
    public long getAmount() {
        return energyStorage.amount;
    }
    @Override
    public long getCapacity() {
        return energyStorage.getCapacity();
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("quantum_teleporter.energy", energyStorage.amount);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        if(nbt.contains("quantum_teleporter.energy")) {
            energyStorage.amount = nbt.getLong("quantum_teleporter.energy");
        }
    }

}
