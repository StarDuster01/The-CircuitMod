package net.stardust.circuitmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.item.custom.PunchRod;
import net.stardust.circuitmod.item.custom.UraniumRod;

public class ModItems {

    public static final Item GRAPHITE = registerItem("graphite", new Item(new FabricItemSettings()));
    public static final Item GRAPHITE_POWDER = registerItem("graphite_powder", new Item(new FabricItemSettings()));
    public static final Item CALCIUM = registerItem("calcium", new Item(new FabricItemSettings()));
    public static final Item ZIRCONIUM_POWDER = registerItem("zirconium_powder", new Item(new FabricItemSettings()));
    public static final Item ZIRCON = registerItem("zircon", new Item(new FabricItemSettings()));
    public static final Item ZIRCONIUM_INGOT = registerItem("zirconium_ingot", new Item(new FabricItemSettings()));
    public static final Item LEAD_POWDER = registerItem("lead_powder", new Item(new FabricItemSettings()));
    public static final Item LEAD_RAW = registerItem("lead_raw", new Item(new FabricItemSettings()));
    public static final Item PUNCH_ROD = registerItem("punch_rod", new PunchRod(new FabricItemSettings()));
    public static final Item LEAD_INGOT = registerItem("lead_ingot", new Item(new FabricItemSettings()));
    public static final Item SODIUM = registerItem("sodium", new Item(new FabricItemSettings()));
    public static final Item LITHIUM = registerItem("lithium", new Item(new FabricItemSettings()));
    public static final Item PLASTIC = registerItem("plastic", new Item(new FabricItemSettings()));
    public static final Item PLASTIC_PELLET = registerItem("plastic_pellet", new Item(new FabricItemSettings()));
    public static final Item SYNTHETIC_RUBBER = registerItem("synthetic_rubber", new Item(new FabricItemSettings()));
    public static final Item NATURAL_RUBBER = registerItem("natural_rubber", new Item(new FabricItemSettings()));
    //public static final Item URANIUM_ROD = registerItem("uranium_rod", new UraniumRod(new FabricItemSettings()));

    public static final Item RAW_BAUXITE = registerItem("raw_bauxite", new Item(new FabricItemSettings()));
    public static final Item CRUSHED_BAUXITE = registerItem("crushed_bauxite", new Item(new FabricItemSettings()));
    public static final Item ALUMINUM_HYDROXIDE = registerItem("aluminum_hydroxide", new Item(new FabricItemSettings()));
    public static final Item GIBBSITE = registerItem("gibbsite", new Item(new FabricItemSettings()));
    public static final Item ALUMINA = registerItem("alumina", new Item(new FabricItemSettings()));
    public static final Item ALUMINUM_INGOT = registerItem("aluminum_ingot", new Item(new FabricItemSettings()));

    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new FabricItemSettings()));

    public static final Item RAW_URANIUM = registerItem("raw_uranium", new Item(new FabricItemSettings()));
    public static final Item CRUSHED_URANIUM = registerItem("crushed_uranium", new Item(new FabricItemSettings()));
    public static final Item URANIUM_DIOXIDE_238 = registerItem("uranium_dioxide_238", new Item(new FabricItemSettings()));
    public static final Item URANIUM_DIOXIDE_235 = registerItem("uranium_dioxide_235", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_GENERIC = registerItem("nuclear_pellet_generic", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_U235 = registerItem("nuclear_pellet_u235", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_U238 = registerItem("nuclear_pellet_u238", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_U239 = registerItem("nuclear_pellet_u239", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_NP237 = registerItem("nuclear_pellet_np237", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_NP238 = registerItem("nuclear_pellet_np238", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_NP239 = registerItem("nuclear_pellet_np239", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_PU238 = registerItem("nuclear_pellet_pu238", new Item(new FabricItemSettings()));
    public static final Item NUCLEAR_PELLET_PU239 = registerItem("nuclear_pellet_pu239", new Item(new FabricItemSettings()));

    public static final Item CAPACITOR = registerItem("capacitor", new Item(new FabricItemSettings()));
    public static final Item DIODE = registerItem("diode", new Item(new FabricItemSettings()));
    public static final Item MOSFET = registerItem("mosfet", new Item(new FabricItemSettings()));
    public static final Item RELAY = registerItem("relay", new Item(new FabricItemSettings()));
    public static final Item TRANSISTOR = registerItem("transistor", new Item(new FabricItemSettings()));
    public static final Item SMALLCHIP = registerItem("smallchip", new Item(new FabricItemSettings()));
    public static final Item MEDCHIP = registerItem("medchip", new Item(new FabricItemSettings()));
    public static final Item LARGECHIP = registerItem("largechip", new Item(new FabricItemSettings()));
    public static final Item RESISTORCOPPER = registerItem("resistorcopper", new Item(new FabricItemSettings()));
    public static final Item RESISTORIRON = registerItem("resistoriron", new Item(new FabricItemSettings()));
    public static final Item RESISTORGOLD = registerItem("resistorgold", new Item(new FabricItemSettings()));
    public static final Item RESISTOREMERALD = registerItem("resistoremerald", new Item(new FabricItemSettings()));
    public static final Item RESISTORDIAMOND = registerItem("resistordiamond", new Item(new FabricItemSettings()));
    public static final Item RESISTORNETHERITE = registerItem("resistornetherite", new Item(new FabricItemSettings()));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {

    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CircuitMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        CircuitMod.LOGGER.info("Registering Mod Items for " + CircuitMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
