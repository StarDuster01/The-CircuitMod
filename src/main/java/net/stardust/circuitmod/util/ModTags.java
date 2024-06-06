package net.stardust.circuitmod.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;

public class ModTags {
    public static class Blocks {


        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(CircuitMod.MOD_ID, name));
        }
    }

    public static class Items {


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(CircuitMod.MOD_ID, name));
        }
    }

}
