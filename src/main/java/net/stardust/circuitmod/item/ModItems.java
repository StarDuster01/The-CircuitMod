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
import net.stardust.circuitmod.item.custom.UraniumRod;

public class ModItems {

    public static final Item NATURAL_RUBBER = registerItem("natural_rubber", new Item(new FabricItemSettings()));
    public static final Item GRAPHITE = registerItem("graphite", new Item(new FabricItemSettings()));
    public static final Item CALCIUM = registerItem("calcium", new Item(new FabricItemSettings()));
    public static final Item ZIRCONIUM = registerItem("zirconium", new Item(new FabricItemSettings()));
    public static final Item LEAD = registerItem("lead", new Item(new FabricItemSettings()));
    public static final Item URANIUM = registerItem("uranium", new Item(new FabricItemSettings()));
    public static final Item PLUTONIUM = registerItem("plutonium", new Item(new FabricItemSettings()));
    public static final Item SODIUM = registerItem("sodium", new Item(new FabricItemSettings()));
    public static final Item LITHIUM = registerItem("lithium", new Item(new FabricItemSettings()));
    public static final Item PLASTIC = registerItem("plastic", new Item(new FabricItemSettings()));
    public static final Item SYNTHETIC_RUBBER = registerItem("synthetic_rubber", new Item(new FabricItemSettings()));

    public static final Item URANIUM_ROD = registerItem("uranium_rod", new UraniumRod(new FabricItemSettings()));

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
