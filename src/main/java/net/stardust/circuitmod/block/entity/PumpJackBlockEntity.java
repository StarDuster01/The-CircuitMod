package net.stardust.circuitmod.block.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.stardust.circuitmod.block.entity.slave.pumpjack.PumpJackEnergySlaveBlockEntity;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.PumpJackScreenHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class PumpJackBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {
    private boolean isPowered; // THIS REFERS TO THE REDSTONE CONTROL SIGNAL AND NOTHING ELSE
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    /// NEW TEST ENERGY CODE
    private boolean checkedForOilChunk = false;
    private boolean isInOilChunk = false;
    public boolean getPoweredState() {
        return this.isPowered;
    }

    private int max_oil = 64800;

    public PumpJackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUMP_JACK_BE, pos, state);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
    }

    private static final int MAX_DIRECT_ENERGY = 100000;

    private static final int OIL_CAPACITY = 64800; // Example capacity, adjust as needed
    private int oilLevel = 0; // Oil level in the tank

    private static final int INPUT_SLOT = 0;

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Pump Jack");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PumpJackScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    private boolean isRunning = false;

    private boolean isOilChunk(World world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        long seed = ((StructureWorldAccess)world).getSeed();
        return ChunkRandom.getSlimeRandom(chunkPos.x, chunkPos.z, seed, 987234911L).nextInt(2) == 0;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;

    }



    private static final int POWERED_INDEX = 1;
    /////////////////////// PROPERTY DELEGATE ////////////////////////
    private static final int RUNNING_INDEX = 2; // New index for isRunning
    private static final int IS_OIL_CHUNK_INDEX = 4; // New index for isoilchunk

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case POWERED_INDEX: return isPowered ? 1 : 0;
                case RUNNING_INDEX: return isRunning ? 1 : 0;
                case FLUID_LEVEL_INDEX: return oilLevel;
                case IS_OIL_CHUNK_INDEX: return isInOilChunk ? 1 : 0;
                default: return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case FLUID_LEVEL_INDEX: oilLevel = value; break;
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };



    public void setOilLevel(int oil) {
        this.oilLevel = Math.min(oil, OIL_CAPACITY);
        markDirty();
    }

    private int directEnergy = 0;
    ////////////////ALL ADDITIONAL ENERGY FUNCTION HERE //////////////////


    private static final int FLUID_LEVEL_INDEX = 3; // Assign an appropriate index

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        if (!world.isClient && !checkedForOilChunk) {
            isInOilChunk = isOilChunk(world, pos);
            checkedForOilChunk = true;
        }

        // Retrieve the Energy Slave Block Entity
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        BlockPos extraSlavePos = pos.offset(facing, 2);
        BlockPos energySlavePos = pos.offset(facing.getOpposite(), 3);
        BlockEntity slaveBlockEntity = world.getBlockEntity(energySlavePos);
        System.out.println("OIl CHunk status = "+ isInOilChunk);

        if(!isInOilChunk) return;

        if (slaveBlockEntity instanceof PumpJackEnergySlaveBlockEntity) {
            PumpJackEnergySlaveBlockEntity slave = (PumpJackEnergySlaveBlockEntity) slaveBlockEntity;

            // Synchronize energy and use it to produce oil
            directEnergy = slave.getDirectEnergy();
            useEnergyToProduceOil(slave); // Pass slave to the method
          //  System.out.println("PumpJack: Energy = " + directEnergy + ", Oil Level = " + this.oilLevel);
            markDirty();
        }

        ItemStack inputItem = this.inventory.get(INPUT_SLOT);
        if (inputItem.getItem() == Items.BUCKET && this.oilLevel == this.max_oil) {
            // Replace the bucket with a Crude Oil Bucket
            ItemStack crudeOilBucket = new ItemStack(ModFluids.CRUDE_OIL_BUCKET);
            this.inventory.set(INPUT_SLOT, crudeOilBucket);

            // Reset oil level
            this.oilLevel = 0;
            markDirty();
        }
        passFluidToPipe();
    }
    private void useEnergyToProduceOil(PumpJackEnergySlaveBlockEntity slave) {
        final int energyUsage = 10;
        final int oilProduction = 40;

        if (slave.getDirectEnergy() >= energyUsage && this.oilLevel + oilProduction <= max_oil) {
            // Increase oil level and decrease slave's energy
            this.oilLevel += oilProduction;
            slave.reduceEnergy(energyUsage); // A new method in slave entity to reduce energy
            shouldPumpAnimate = true;
            sendAnimationUpdate();
           // System.out.println(shouldPumpAnimate + "shouldPumpAnimate value from method");
        }
        else {
            shouldPumpAnimate = false;
            sendAnimationUpdate();
        }
    }

    private void passFluidToPipe() {
        if (world == null) {
         //   System.out.println("Debug: World is null");
            return;
        }
        if (world.isClient) {
          //  System.out.println("Debug: World is client-side");
            return;
        }

       // System.out.println("Debug: Starting to check adjacent blocks");

        // Calculate the extra slave position
        Direction facing = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
        BlockPos extraSlavePos = pos.offset(facing, 2);

        // Iterate over all horizontal directions from extraSlavePos
        for (Direction direction : Direction.values()) {
            if (!direction.getAxis().isHorizontal()) {
                continue;
            }

            BlockPos adjacentPos = extraSlavePos.offset(direction); // Position next to the extra slave position
           // System.out.println("Debug: Checking block at " + adjacentPos + " in direction " + direction);

            BlockEntity adjacentBlockEntity = world.getBlockEntity(adjacentPos);

            if (adjacentBlockEntity instanceof FluidPipeBlockEntity) {
                //System.out.println("Debug: Found Fluid Pipe Block Entity at " + adjacentPos);

                FluidPipeBlockEntity pipe = (FluidPipeBlockEntity) adjacentBlockEntity;
                if (pipe.canReceiveFluid()) {
                    //System.out.println("Debug: Pipe can receive fluid");

                    String fluidType = pipe.getCurrentFluidType();
                    if (fluidType != null && fluidType.equals("OIL") || pipe.isPipeEmpty()) {
                        //System.out.println("Debug: Pipe is suitable for oil");
                        int fluidAmount = 20;
                        if (this.getFluidLevel() >= fluidAmount) {
                           // System.out.println("Debug: Pump Jack has enough fluid to transfer");
                            this.decreaseFluidLevel(fluidAmount);
                            pipe.increaseFluidLevel(fluidAmount, "OIL"); // Pass the fluid type here
                        } else {
                           // System.out.println("Debug: Not enough fluid in Pump Jack to transfer");
                        }
                    } else {
                      //  System.out.println("Debug: Pipe is not empty or does not contain oil");
                    }
                } else {
                   // System.out.println("Debug: Pipe cannot receive fluid");
                }
            } else {
               // System.out.println("Debug: No Fluid Pipe Block Entity found at " + adjacentPos);
            }
        }
    }





    private void decreaseFluidLevel(int fluidAmount) {
    }

    private int getFluidLevel() {
        return this.oilLevel;
    }


    ///////// ANIMATION DETAILS ////////////




    //////////////// NBT DATA /////////////
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OilLevel", oilLevel);
        nbt.putInt("Energy", this.directEnergy);
        Inventories.writeNbt(nbt, getItems());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, getItems());
        oilLevel = nbt.getInt("OilLevel");
        directEnergy = nbt.getInt("Energy");
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

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    boolean shouldPumpAnimate = false;
    public void setShouldPumpAnimate(boolean shouldAnimate) {
        this.shouldPumpAnimate = shouldAnimate;
        if (this.world != null && this.world.isClient) {
        }
    }

    private void sendAnimationUpdate() {
        if (this.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.world;
            serverWorld.getPlayers().stream()
                    .filter(player -> player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) < 64 * 64) // within 64 blocks
                    .forEach(player -> {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBlockPos(this.pos);
                        buf.writeBoolean(this.shouldPumpAnimate);
                        ServerPlayNetworking.send(player, ModMessages.PUMP_JACK_ANIMATION_UPDATE_ID, buf);
                    });
        }
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));

    }


    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

        if (shouldPumpAnimate) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.running", Animation.LoopType.LOOP));
        }
        else {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));

        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public int getOilLevel() {
        return this.oilLevel;
    }


    public void updateEnergyAndOilLevel(int energy, int oilLevel) {
        this.directEnergy = energy;
        this.oilLevel = oilLevel;
        markDirty();
    }
}
