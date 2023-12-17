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
    public static FlowableFluid STILL_CRUDE_OIL;
    public static FlowableFluid FLOWING_CRUDE_OIL;
    public static Block CRUDE_OIL_BLOCK;
    public static Item CRUDE_OIL_BUCKET;
    public static FlowableFluid STILL_LIQUID_FUEL;
    public static FlowableFluid FLOWING_LIQUID_FUEL;
    public static Block LIQUID_FUEL_BLOCK;
    public static Item LIQUID_FUEL_BUCKET;


    public static void register() {
        STILL_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/crudeoil_still"), new CrudeOilFluid.Still());
        FLOWING_CRUDE_OIL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/crubeoil_flow"), new CrudeOilFluid.Flowing());

        CRUDE_OIL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "crude_oil_block"),
                new FluidBlock(ModFluids.STILL_CRUDE_OIL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        CRUDE_OIL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "crude_oil_bucket"),
                new BucketItem(ModFluids.STILL_CRUDE_OIL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));






        STILL_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/petrol_still"), new LiquidFuelFluid.Still());
        FLOWING_LIQUID_FUEL = Registry.register(Registries.FLUID,
                new Identifier(CircuitMod.MOD_ID, "fluids/petrol_flow"), new LiquidFuelFluid.Flowing());

        LIQUID_FUEL_BLOCK = Registry.register(Registries.BLOCK, new Identifier(CircuitMod.MOD_ID, "liquid_fuel_block"),
                new FluidBlock(ModFluids.STILL_LIQUID_FUEL, FabricBlockSettings.copyOf(Blocks.WATER)){ });
        LIQUID_FUEL_BUCKET = Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, "liquid_fuel_bucket"),
                new BucketItem(ModFluids.STILL_LIQUID_FUEL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

    }
}
