package org.bukkit.craftbukkit.legacy;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

//Magma - Redirect to version specific CraftLegacy
@Deprecated
public class CraftLegacy {

    private CraftLegacy() {
        //
    }

    public static Material toLegacy(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toLegacy(material);
    }

    public static MaterialData toLegacyData(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toLegacyData(material);
    }

    public static BlockState fromLegacyData(Material material, byte data) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.fromLegacyData(material, data);
    }

    public static Item fromLegacyData(Material material, short data) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.fromLegacyData(material, data);
    }

    public static byte toLegacyData(BlockState blockData) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toLegacyData(blockData);
    }

    public static Material toLegacyMaterial(BlockState blockData) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toLegacyMaterial(blockData);
    }

    public static MaterialData toLegacy(BlockState blockData) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toLegacy(blockData);
    }

    public static Material fromLegacy(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.fromLegacy(material);
    }

    public static Material fromLegacy(MaterialData materialData) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.fromLegacy(materialData);
    }

    public static Material fromLegacy(MaterialData materialData, boolean itemPriority) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.fromLegacy(materialData, itemPriority);
    }

    public static Material[] values() {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.values();
    }

    public static Material valueOf(String name) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.valueOf(name);
    }

    public static Material getMaterial(String name) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.getMaterial(name);
    }

    public static Material matchMaterial(String name) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.matchMaterial(name);
    }

    public static int ordinal(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.ordinal(material);
    }

    public static String name(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.name(material);
    }

    public static String toString(Material material) {
        return org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.toString(material);
    }

    public static void init() {
        org.bukkit.craftbukkit.v1_18_R2.legacy.CraftLegacy.init();
    }
}
