package net.stardust.circuitmod.fluid;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;

public class ModFluids {
    // CRUDE OIL
    public static FlowableFluid STILL_CRUDE_OIL;
    public static FlowableFluid FLOWING_CRUDE_OIL;
    public static Block CRUDE_OIL_BLOCK;
    public static Item CRUDE_OIL_BUCKET;

    // HOT CRUDE
    public static FlowableFluid STILL_HOT_CRUDE_OIL;
    public static FlowableFluid FLOWING_HOT_CRUDE_OIL;
    public static Block HOT_CRUDE_OIL_BLOCK;
    public static Item HOT_CRUDE_OIL_BUCKET;

    // LIQUID FUEL
    public static FlowableFluid STILL_LIQUID_FUEL;
    public static FlowableFluid FLOWING_LIQUID_FUEL;
    public static Block LIQUID_FUEL_BLOCK;
    public static Item LIQUID_FUEL_BUCKET;

    // RESIDUE
    public static FlowableFluid STILL_RESIDUE;
    public static FlowableFluid FLOWING_RESIDUE;
    public static Block RESIDUE_FLUID_BLOCK;
    public static Item RESIDUE_BUCKET;

    // LUBE
    public static FlowableFluid STILL_LUBEOIL;
    public static FlowableFluid FLOWING_LUBEOIL;
    public static Block LUBEOIL_BLOCK;
    public static Item LUBEOIL_BUCKET;

    // MOTOR OIL
    public static FlowableFluid STILL_MOTOROIL;
    public static FlowableFluid FLOWING_MOTOROIL;
    public static Block MOTOROIL_BLOCK;
    public static Item MOTOROIL_BUCKET;

    // NAPHTHA
    public static FlowableFluid STILL_NAPHTHA;
    public static FlowableFluid FLOWING_NAPHTHA;
    public static Block NAPHTHA_BLOCK;
    public static Item NAPHTHA_BUCKET;

    // HOT NAPHTHA
    public static FlowableFluid STILL_HOT_NAPHTHA;
    public static FlowableFluid FLOWING_HOT_NAPHTHA;
    public static Block HOT_NAPHTHA_BLOCK;
    public static Item HOT_NAPHTHA_BUCKET;

    // ETHYLENE
    public static FlowableFluid STILL_ETHYLENE;
    public static FlowableFluid FLOWING_ETHYLENE;
    public static Block ETHYLENE_BLOCK;
    public static Item ETHYLENE_BUCKET;

    // BUTADIENE
    public static FlowableFluid STILL_BUTADIENE;
    public static FlowableFluid FLOWING_BUTADIENE;
    public static Block BUTADIENE_BLOCK;
    public static Item BUTADIENE_BUCKET;

    // BENZENE
    public static FlowableFluid STILL_BENZENE;
    public static FlowableFluid FLOWING_BENZENE;
    public static Block BENZENE_BLOCK;
    public static Item BENZENE_BUCKET;

    // STYRENE
    public static FlowableFluid STILL_STYRENE;
    public static FlowableFluid FLOWING_STYRENE;
    public static Block STYRENE_BLOCK;
    public static Item STYRENE_BUCKET;

    // ETHYLBENZENE
    public static FlowableFluid STILL_ETHYLBENZENE;
    public static FlowableFluid FLOWING_ETHYLBENZENE;
    public static Block ETHYLBENZENE_BLOCK;
    public static Item ETHYLBENZENE_BUCKET;

    // POLYETHYLENE
    public static FlowableFluid STILL_POLYETHYLENE;
    public static FlowableFluid FLOWING_POLYETHYLENE;
    public static Block POLYETHYLENE_BLOCK;
    public static Item POLYETHYLENE_BUCKET;


    public static void register() {
        // CRUDE OIL
        STILL_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/crudeoil_still"), new CrudeOilFluid.Still());
        FLOWING_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/crudeoil_flow"), new CrudeOilFluid.Flowing());

        CRUDE_OIL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "crude_oil_block"),
                new FluidBlock(ModFluids.STILL_CRUDE_OIL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        CRUDE_OIL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "crude_oil_bucket"),
                new BucketItem(ModFluids.STILL_CRUDE_OIL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // HOT CRUDE
        STILL_HOT_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/hot_crudeoil_still"), new HotCrudeOilFluid.Still());
        FLOWING_HOT_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/hot_crudeoil_flow"), new HotCrudeOilFluid.Flowing());

        HOT_CRUDE_OIL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "hot_crude_oil_block"),
                new FluidBlock(ModFluids.STILL_HOT_CRUDE_OIL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        HOT_CRUDE_OIL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "hot_crude_oil_bucket"),
                new BucketItem(ModFluids.STILL_HOT_CRUDE_OIL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // LIQUID FUEL
        STILL_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/petrol_still"), new LiquidFuelFluid.Still());
        FLOWING_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/petrol_flow"), new LiquidFuelFluid.Flowing());

        LIQUID_FUEL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "liquid_fuel_block"),
                new FluidBlock(ModFluids.STILL_LIQUID_FUEL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        LIQUID_FUEL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "liquid_fuel_bucket"),
                new BucketItem(ModFluids.STILL_LIQUID_FUEL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // RESIDUE
        STILL_RESIDUE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/residue_still"), new ResidueFluid.Still());
        FLOWING_RESIDUE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/residue_flow"), new ResidueFluid.Flowing());

        RESIDUE_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "residue_fluid_block"),
                new FluidBlock(ModFluids.STILL_RESIDUE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        RESIDUE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "residue_bucket"),
                new BucketItem(ModFluids.STILL_RESIDUE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // LUBE OIL
        STILL_LUBEOIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/lubeoil_still"), new LubeOilFluid.Still());
        FLOWING_LUBEOIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/lubeoil_flow"), new LubeOilFluid.Flowing());

        LUBEOIL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "lubeoil_block"),
                new FluidBlock(ModFluids.STILL_LUBEOIL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        LUBEOIL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "lubeoil_bucket"),
                new BucketItem(ModFluids.STILL_LUBEOIL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // MOTOR OIL
        STILL_MOTOROIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/motoroil_still"), new MotorOilFluid.Still());
        FLOWING_MOTOROIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/motoroil_flow"), new MotorOilFluid.Flowing());

        MOTOROIL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "motoroil_block"),
                new FluidBlock(ModFluids.STILL_MOTOROIL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        MOTOROIL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "motoroil_bucket"),
                new BucketItem(ModFluids.STILL_MOTOROIL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // NAPHTHA
        STILL_NAPHTHA = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/naphtha_still"), new NaphthaFluid.Still());
        FLOWING_NAPHTHA = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/naphtha_flow"), new NaphthaFluid.Flowing());

        NAPHTHA_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "naphtha_block"),
                new FluidBlock(ModFluids.STILL_NAPHTHA, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        NAPHTHA_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "naphtha_bucket"),
                new BucketItem(ModFluids.STILL_NAPHTHA, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // HOT NAPHTHA
        STILL_HOT_NAPHTHA = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/hot_naphtha_still"), new HotNaphthaFluid.Still());
        FLOWING_HOT_NAPHTHA = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/hot_naphtha_flow"), new HotNaphthaFluid.Flowing());

        HOT_NAPHTHA_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "hot_naphtha_block"),
                new FluidBlock(ModFluids.STILL_HOT_NAPHTHA, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        HOT_NAPHTHA_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "hot_naphtha_bucket"),
                new BucketItem(ModFluids.STILL_HOT_NAPHTHA, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // ETHYLENE
        STILL_ETHYLENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/ethylene_still"), new EthyleneFluid.Still());
        FLOWING_ETHYLENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/ethylene_flow"), new EthyleneFluid.Flowing());

        ETHYLENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "ethylene_block"),
                new FluidBlock(ModFluids.STILL_ETHYLENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        ETHYLENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "ethylene_bucket"),
                new BucketItem(ModFluids.STILL_ETHYLENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // BUTADIENE
        STILL_BUTADIENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/butadiene_still"), new ButadieneFluid.Still());
        FLOWING_BUTADIENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/butadiene_flow"), new ButadieneFluid.Flowing());

        BUTADIENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "butadiene_block"),
                new FluidBlock(ModFluids.STILL_BUTADIENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        BUTADIENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "butadiene_bucket"),
                new BucketItem(ModFluids.STILL_BUTADIENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // BENZENE
        STILL_BENZENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/benzene_still"), new BenzeneFluid.Still());
        FLOWING_BENZENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/benzene_flow"), new BenzeneFluid.Flowing());

        BENZENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "benzene_block"),
                new FluidBlock(ModFluids.STILL_BENZENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        BENZENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "benzene_bucket"),
                new BucketItem(ModFluids.STILL_BENZENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // STYRENE
        STILL_STYRENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/styrene_still"), new StyreneFluid.Still());
        FLOWING_STYRENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/styrene_flow"), new StyreneFluid.Flowing());

        STYRENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "styrene_block"),
                new FluidBlock(ModFluids.STILL_STYRENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        STYRENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "styrene_bucket"),
                new BucketItem(ModFluids.STILL_STYRENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // ETHYLBENZENE
        STILL_ETHYLBENZENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/ethylbenzene_still"), new EthylbenzeneFluid.Still());
        FLOWING_ETHYLBENZENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/ethylbenzene_flow"), new EthylbenzeneFluid.Flowing());

        ETHYLBENZENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "ethylbenzene_block"),
                new FluidBlock(ModFluids.STILL_ETHYLBENZENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        ETHYLBENZENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "ethylbenzene_bucket"),
                new BucketItem(ModFluids.STILL_ETHYLBENZENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        // POLYETHYLENE
        STILL_POLYETHYLENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/polyethylene_still"), new PolyethyleneFluid.Still());
        FLOWING_POLYETHYLENE = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/polyethylene_flow"), new PolyethyleneFluid.Flowing());

        POLYETHYLENE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "polyethylene_block"),
                new FluidBlock(ModFluids.STILL_POLYETHYLENE, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        POLYETHYLENE_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "polyethylene_bucket"),
                new BucketItem(ModFluids.STILL_POLYETHYLENE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

    }
}
