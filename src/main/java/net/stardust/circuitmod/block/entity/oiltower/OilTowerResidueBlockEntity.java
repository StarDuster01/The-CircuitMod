package net.stardust.circuitmod.block.entity.oiltower;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.stardust.circuitmod.api.IEnergyConsumer;
import net.stardust.circuitmod.api.IFluidConsumer;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.FluidPipeBlockEntity;
import net.stardust.circuitmod.block.entity.ImplementedInventory;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.networking.ModMessages;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public class OilTowerResidueBlockEntity extends BlockEntity implements IFluidConsumer {

    private int oilAmount;
    private int maxOilCapacity = 100000; // Example maximum oil capacity, adjust as needed

    private int residueAmount;
    private int maxResidueCapacity = 50000; // Example maximum residue capacity, adjust as needed
    private int tickCounter = 0;


    public OilTowerResidueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_TOWER_RESIDUE_BE, pos, state);
        this.oilAmount = 0;
        this.residueAmount = 0;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!world.isClient) {
            tickCounter++;
            if(tickCounter >= 20) {
                tickCounter = 0;
                if(this.oilAmount > 0 && this.residueAmount < this.maxResidueCapacity) {
                    decreaseOil(1); // Decrease oil by 1 unit
                    increaseResidue(1); // Increase residue by 1 unit
                }
                passResidueToPipe(); // Attempt to pass residue to an adjacent pipe
            }
        }
    }

    private void passResidueToPipe() {
        if(world == null || world.isClient) return;

        for(Direction direction : Direction.values()) {
            if(!direction.getAxis().isHorizontal()) continue;

            BlockPos adjacentPos = pos.offset(direction);
            BlockEntity adjacentEntity = world.getBlockEntity(adjacentPos);

            if(adjacentEntity instanceof FluidPipeBlockEntity) {
                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentEntity;

                if(pipe.canReceiveFluid() && this.residueAmount > 0) {
                    int transferredAmount = Math.min(this.residueAmount, 20);
                    decreaseResidue(transferredAmount);
                    pipe.increaseFluidLevel(transferredAmount, "RESIDUE");
                    if(this.residueAmount == 0) break;
                }
            }
        }
    }

    public boolean hasSpecificBlocksAbove(World world) {
        Block[] requiredBlocksAbove = {
                Blocks.DIAMOND_BLOCK, // First block above
                Blocks.EMERALD_BLOCK, // Second block above
                Blocks.GOLD_BLOCK,    // Third block above
                Blocks.IRON_BLOCK     // Fourth block above
        };
        for (int i = 0; i < requiredBlocksAbove.length; i++) {
            BlockPos checkPos = pos.up(i + 1);
            BlockState stateAtPos = world.getBlockState(checkPos);

            if (stateAtPos.getBlock() != requiredBlocksAbove[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addFluid(int fluidAmount, String fluidType) {
        if("oil".equalsIgnoreCase(fluidType)) {
            increaseOil(fluidAmount);
        }
    }
    public void increaseOil(int amount) {
        this.oilAmount += amount;
        if (this.oilAmount > this.maxOilCapacity) {
            this.oilAmount = this.maxOilCapacity;
        }
        markDirty();
    }
    public void decreaseOil(int amount) {
        this.oilAmount -= amount;
        if (this.oilAmount < 0) {
            this.oilAmount = 0;
        }
        markDirty();
    }
    public void increaseResidue(int amount) {
        this.residueAmount += amount;
        if (this.residueAmount > this.maxResidueCapacity) {
            this.residueAmount = this.maxResidueCapacity;
        }
        markDirty();
    }
    public void decreaseResidue(int amount) {
        this.residueAmount -= amount;
        if (this.residueAmount < 0) {
            this.residueAmount = 0;
        }
        markDirty();
    }

    public int getOilAmount() {
        return this.oilAmount;
    }
    public int getResidueAmount() {
        return this.residueAmount;
    }

    @Override
    public boolean canReceiveFluid() {
        boolean hasRequiredBlocksAbove = hasSpecificBlocksAbove(getWorld());
        boolean hasCapacityForMoreOil = this.oilAmount < this.maxOilCapacity;
        return hasRequiredBlocksAbove && hasCapacityForMoreOil;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OilAmount", this.oilAmount);
        nbt.putInt("ResidueAmount", this.residueAmount);
    }

}
