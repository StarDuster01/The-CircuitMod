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


public class ModItemGroups {

    // Creative Tab
    public static final ItemGroup Space_Group = Registry.register(Registries.ITEM_GROUP,
            new Identifier(CircuitMod.MOD_ID, "circuit"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.circuit"))
                    .icon(() -> new ItemStack(Items.DIAMOND)).entries((displayContext, entries) -> {


                        entries.add(ModBlocks.CONDUCTOR_BLOCK);
                        entries.add(ModBlocks.EFFICIENT_COAL_GENERATOR_BLOCK);
                        entries.add(ModBlocks.QUARRY_BLOCK);




                    }).build());


    public static void registerItemGroups() {
        CircuitMod.LOGGER.info("Registering Item Groups for " + CircuitMod.MOD_ID);
    }
}