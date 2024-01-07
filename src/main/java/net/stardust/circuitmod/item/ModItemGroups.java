package net.stardust.circuitmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.fluid.ModFluids;


public class ModItemGroups {

    // Creative Tab
    public static final ItemGroup Circuit_Group = Registry.register(Registries.ITEM_GROUP,
            new Identifier(CircuitMod.MOD_ID, "circuit"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.circuit"))
                    .icon(() -> new ItemStack(ModFluids.CRUDE_OIL_BUCKET)).entries((displayContext, entries) -> {


                        entries.add(ModBlocks.CONDUCTOR_BLOCK);
                        entries.add(ModBlocks.FLUID_PIPE_BLOCK);
                        entries.add(ModBlocks.EFFICIENT_COAL_GENERATOR_BLOCK);
                        entries.add(ModBlocks.QUARRY_BLOCK);
                        entries.add(ModBlocks.PCBSTATION_BLOCK);
                        entries.add(ModBlocks.MOVING_WALKWAY_BLOCK);
                        entries.add(ModBlocks.RUBBER_TAP_BLOCK);
                        entries.add(ModBlocks.NUKE_BLOCK);
                        entries.add(ModBlocks.LARGE_NUKE_BLOCK);
                        entries.add(ModBlocks.QUANTUM_TELEPORTER_BLOCK);


                        entries.add(ModBlocks.RUBBER_LOG);
                        entries.add(ModBlocks.RUBBER_LEAVES);
                        entries.add(ModBlocks.STRIPPED_RUBBER_LOG);
                        entries.add(ModBlocks.RUBBER_SAPLING);
                        entries.add(ModBlocks.CHUNK_LOADER_BLOCK);
                        entries.add(ModBlocks.FUEL_GENERATOR_BLOCK);
                        entries.add(ModBlocks.PIPE_BLOCK);
                        entries.add(ModBlocks.PUMP_JACK_BLOCK);
                    //    entries.add(ModBlocks.INPUT_PIPE_BLOCK);


                        entries.add(ModFluids.CRUDE_OIL_BUCKET);
                        entries.add(ModFluids.LIQUID_FUEL_BUCKET);


                    }).build());

    //Items Creative Tab
    public static final ItemGroup Circuititems_Group = Registry.register(Registries.ITEM_GROUP,
            new Identifier(CircuitMod.MOD_ID, "circuititems"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.circuititems"))
                    .icon(() -> new ItemStack(ModItems.LARGECHIP)).entries((displayContext, entries) -> {

                        entries.add(ModItems.URANIUM_ROD);


                        entries.add(ModItems.GRAPHITE);
                        entries.add(ModItems.GRAPHITE_POWDER);
                        entries.add(ModItems.CALCIUM);
                        entries.add(ModItems.ZIRCON);
                        entries.add(ModItems.ZIRCONIUM_POWDER);
                        entries.add(ModItems.ZIRCONIUM_INGOT);
                        entries.add(ModItems.LITHIUM);
                        entries.add(ModItems.LEAD_POWDER);
                        entries.add(ModItems.LEAD_RAW);
                        entries.add(ModItems.LEAD_INGOT);
                        entries.add(ModItems.URANIUM);
                        entries.add(ModItems.PLUTONIUM);
                        entries.add(ModItems.SODIUM);
                        entries.add(ModItems.PLASTIC);
                        entries.add(ModItems.SYNTHETIC_RUBBER);
                        entries.add(ModItems.NATURAL_RUBBER);
                        entries.add(ModItems.PUNCH_ROD);


                        entries.add(ModItems.CAPACITOR);
                        entries.add(ModItems.DIODE);
                        entries.add(ModItems.MOSFET);
                        entries.add(ModItems.RELAY);
                        entries.add(ModItems.TRANSISTOR);
                        entries.add(ModItems.SMALLCHIP);
                        entries.add(ModItems.MEDCHIP);
                        entries.add(ModItems.LARGECHIP);
                        entries.add(ModItems.RESISTORCOPPER);
                        entries.add(ModItems.RESISTORIRON);
                        entries.add(ModItems.RESISTORGOLD);
                        entries.add(ModItems.RESISTOREMERALD);
                        entries.add(ModItems.RESISTORDIAMOND);
                        entries.add(ModItems.RESISTORNETHERITE);


                    }).build());


    public static void registerItemGroups() {
        CircuitMod.LOGGER.info("Registering Item Groups for " + CircuitMod.MOD_ID);
    }
}