package org.magmafoundation.magma.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_18_R2.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffectType;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.helpers.EnumJ17Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForgeInject {

    public static void init() {
        Magma.LOGGER.warn("Injecting Forge Material into Bukkit");
        addForgeItems();
        Magma.LOGGER.warn("Injecting Forge Blocks into Bukkit");
        addForgeBlocks();
        Magma.LOGGER.warn("Injecting Forge Enchantments into Bukkit");
        addForgeEnchantments();
        Magma.LOGGER.warn("Injecting Forge Potions into Bukkit");
        addForgePotions();
        Magma.LOGGER.warn("Injecting Forge Biomes into Bukkit");
        addForgeBiomes();
        Magma.LOGGER.warn("Injecting Forge Entities into Bukkit");
        addForgeEntities();
        Magma.LOGGER.warn("Injecting Forge VillagerProfessions into Bukkit");
        addForgeVillagerProfessions();

        Magma.LOGGER.warn("Injecting Forge into Bukkit: DONE");
    }


    private static void addForgeItems() {
        ForgeRegistries.ITEMS.getEntries().forEach(entry -> {
            ResourceLocation resourceLocation = entry.getValue().getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                // inject item materials into Bukkit for FML
                String materialName = normalizeName(entry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_ITEM__", "");
                Item item = entry.getValue();
                int id = Item.getId(item);
                try {
                    Material material = Material.addMaterial(materialName, id, false, resourceLocation.getNamespace(), CraftNamespacedKey.fromMinecraft(resourceLocation));
                    if(material == null){
                        Magma.LOGGER.warn("Could not inject item into Bukkit: " + materialName);
                        return;
                    }
                    CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
                    CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
                    Magma.LOGGER.warn("Injecting Forge Material into Bukkit: " +  material.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        Magma.LOGGER.warn("Injecting Forge Material into Bukkit: DONE");

    }


    private static void addForgeBlocks() {
        ForgeRegistries.BLOCKS.getEntries().forEach(entry -> {
            ResourceLocation resourceLocation = entry.getValue().getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                // inject block materials into Bukkit for FML
                String materialName = normalizeName(entry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_BLOCK__", "");
                Block block = entry.getValue();
                int id = Item.getId(block.asItem());
                try {
                    Material material = Material.addMaterial(materialName, id, true, resourceLocation.getNamespace(), CraftNamespacedKey.fromMinecraft(resourceLocation));
                    if (material == null) {
                        Magma.LOGGER.warn("Could not inject block into Bukkit: " + materialName);
                        return;
                    }
                    CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
                    CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                    Magma.LOGGER.warn("Injecting Forge Blocks into Bukkit: " +  material.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        Magma.LOGGER.warn("Injecting Forge Blocks into Bukkit: DONE");
    }


    private static void addForgeEnchantments() {
        ForgeRegistries.ENCHANTMENTS.getEntries().forEach(entry -> {
            CraftEnchantment enchantment = new CraftEnchantment(entry.getValue());
            if (!org.bukkit.enchantments.Enchantment.byKey.containsKey(enchantment.getKey()) || !org.bukkit.enchantments.Enchantment.byName.containsKey(enchantment.getName())) {
                org.bukkit.enchantments.Enchantment.byKey.put(enchantment.getKey(), enchantment);
                org.bukkit.enchantments.Enchantment.byName.put(enchantment.getName(), enchantment);
                Magma.LOGGER.warn("Injecting Forge Enchantments into Bukkit: " +  enchantment.getName());
            }
        });
        Magma.LOGGER.warn("Injecting Forge Enchantments into Bukkit: DONE");
    }

    private static void addForgePotions() {
        ForgeRegistries.MOB_EFFECTS.getEntries().forEach(entry -> {
            PotionEffectType pet = new CraftPotionEffectType(entry.getValue());

            if (PotionEffectType.byId[pet.getId()] == null || !PotionEffectType.byName.containsKey(pet.getName().toLowerCase(java.util.Locale.ENGLISH)) || !PotionEffectType.byKey.containsKey(pet.getKey())) {
                PotionEffectType.byId[pet.getId()] = pet;
                PotionEffectType.byName.put(pet.getName().toLowerCase(java.util.Locale.ENGLISH), pet);
                PotionEffectType.byKey.put(pet.getKey(), pet);
                Magma.LOGGER.warn("Injecting Forge Potion into Bukkit: " +  pet.getName());
            }
        });
        Magma.LOGGER.warn("Injecting Forge Potion into Bukkit: DONE");
    }

    private static void addForgeBiomes() {
        List<String> map = new ArrayList<>();
        ForgeRegistries.BLOCKS.getEntries().forEach(entry -> {
            String biomeName = Objects.requireNonNull(entry.getValue().getRegistryName()).getNamespace();
            if (!biomeName.equals(NamespacedKey.MINECRAFT) && !map.contains(biomeName)) {
                map.add(biomeName);
                try {
                    Biome biome = EnumJ17Helper.addEnum0(Biome.class, biomeName, new Class[0]);
                    Magma.LOGGER.warn("Injecting Forge Biome into Bukkit: " +  biome.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        map.clear();
        Magma.LOGGER.warn("Injecting Forge Biome into Bukkit: DONE");
    }


    private static void addForgeEntities() {
        ForgeRegistries.ENTITIES.getEntries().forEach(entity -> {
            ResourceLocation resourceLocation = entity.getValue().getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                String entityType = normalizeName(resourceLocation.toString());
                int typeId = entityType.hashCode();
                try {
                    EntityType bukkitType = EnumJ17Helper.addEnum0(EntityType.class, entityType, new Class[]{String.class, Class.class, Integer.TYPE, Boolean.TYPE}, entityType.toLowerCase(), CraftEntity.class, typeId, false);
                    EntityType.NAME_MAP.put(entityType.toLowerCase(), bukkitType);
                    EntityType.ID_MAP.put((short) typeId, bukkitType);
                    Magma.LOGGER.warn("Injecting Forge Entity into Bukkit: " +  entityType);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        Magma.LOGGER.warn("Injecting Forge Entity into Bukkit: DONE");
    }

    private static void addForgeVillagerProfessions() {
        ForgeRegistries.PROFESSIONS.forEach(villagerProfession -> {
            ResourceLocation resourceLocation = villagerProfession.getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                String name = normalizeName(resourceLocation.toString());
                try {
                    Villager.Profession profession = EnumJ17Helper.addEnum0(Villager.Profession.class, name, new Class[0]);
                    Magma.LOGGER.warn("Injecting Forge VillagerProfession into Bukkit: " +  profession.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        Magma.LOGGER.warn("Injecting Forge VillagerProfession into Bukkit: DONE");
    }

    private static String normalizeName(String name) {
        return name.toUpperCase(java.util.Locale.ENGLISH).replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
    }
}
