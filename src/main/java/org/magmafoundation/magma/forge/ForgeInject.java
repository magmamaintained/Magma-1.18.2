package org.magmafoundation.magma.forge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.registries.ForgeRegistries;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_18_R2.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffectType;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.craftbukkit.entity.CraftCustomEntity;
import org.magmafoundation.magma.helpers.EnumJ17Helper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeInject {

    public static BiMap<ResourceKey<LevelStem>, World.Environment> environment = HashBiMap.create(ImmutableMap.<ResourceKey<LevelStem>, World.Environment>builder()
            .put(LevelStem.OVERWORLD, World.Environment.NORMAL)
            .put(LevelStem.NETHER, World.Environment.NETHER)
            .put(LevelStem.END, World.Environment.THE_END)
            .build());

    public static final Map<Villager.Profession, ResourceLocation> PROFESSION_MAP = new ConcurrentHashMap<>();
    public static final Map<net.minecraft.world.entity.EntityType<?>, String> ENTITY_TYPES = new ConcurrentHashMap<>();

    public static void init() {
        debug("Injecting Forge Material into Bukkit");
        addForgeItems();
        debug("Injecting Forge Blocks into Bukkit");
        addForgeBlocks();
        debug("Injecting Forge Enchantments into Bukkit");
        addForgeEnchantments();
        debug("Injecting Forge Potions into Bukkit");
        addForgePotions();
        debug("Injecting Forge Biomes into Bukkit");
        addForgeBiomes();
        debug("Injecting Forge Entities into Bukkit");
        addForgeEntities();
        debug("Injecting Forge VillagerProfessions into Bukkit");
        addForgeVillagerProfessions();

        debug("Injecting Forge into Bukkit: DONE");
    }

    private static void debug(String message) {
        if (MagmaConfig.instance.debugPrintInjections.getValues())
            Magma.LOGGER.warn(message);
        else
            Magma.LOGGER.debug(message);
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
                    debug("Injecting Forge Material into Bukkit: " +  material.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        debug("Injecting Forge Material into Bukkit: DONE");

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
                    debug("Injecting Forge Blocks into Bukkit: " +  material.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        debug("Injecting Forge Blocks into Bukkit: DONE");
    }


    private static void addForgeEnchantments() {
        ForgeRegistries.ENCHANTMENTS.getEntries().forEach(entry -> {
            CraftEnchantment enchantment = new CraftEnchantment(entry.getValue());
            if (!org.bukkit.enchantments.Enchantment.byKey.containsKey(enchantment.getKey()) || !org.bukkit.enchantments.Enchantment.byName.containsKey(enchantment.getName())) {
                org.bukkit.enchantments.Enchantment.byKey.put(enchantment.getKey(), enchantment);
                org.bukkit.enchantments.Enchantment.byName.put(enchantment.getName(), enchantment);
                debug("Injecting Forge Enchantments into Bukkit: " +  enchantment.getName());
            }
        });
        debug("Injecting Forge Enchantments into Bukkit: DONE");
    }

    private static void addForgePotions() {
        ForgeRegistries.MOB_EFFECTS.getEntries().forEach(entry -> {
            PotionEffectType pet = new CraftPotionEffectType(entry.getValue());

            if (PotionEffectType.byId[pet.getId()] == null || !PotionEffectType.byName.containsKey(pet.getName().toLowerCase(java.util.Locale.ENGLISH)) || !PotionEffectType.byKey.containsKey(pet.getKey())) {
                PotionEffectType.byId[pet.getId()] = pet;
                PotionEffectType.byName.put(pet.getName().toLowerCase(java.util.Locale.ENGLISH), pet);
                PotionEffectType.byKey.put(pet.getKey(), pet);
                debug("Injecting Forge Potion into Bukkit: " +  pet.getName());
            }
        });
        debug("Injecting Forge Potion into Bukkit: DONE");
    }

    private static void addForgeBiomes() {
        List<String> map = new ArrayList<>();
        ForgeRegistries.BIOMES.getEntries().forEach(entry -> {
            String biomeName = Objects.requireNonNull(entry.getValue().getRegistryName()).getNamespace();
            if (!biomeName.equals(NamespacedKey.MINECRAFT) && !map.contains(biomeName)) {
                map.add(biomeName);
                try {
                    Biome biome = EnumJ17Helper.addEnum0(Biome.class, biomeName, new Class[0]);
                    debug("Injecting Forge Biome into Bukkit: " +  biome.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        map.clear();
        debug("Injecting Forge Biome into Bukkit: DONE");
    }


    private static void addForgeEntities() {
        ForgeRegistries.ENTITIES.getEntries().forEach(entity -> {
            ResourceLocation resourceLocation = entity.getValue().getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                String entityType = normalizeName(resourceLocation.toString());
                int typeId = entityType.hashCode();
                try {
                    EntityType bukkitType = EnumJ17Helper.addEnum0(EntityType.class, entityType, new Class[]{String.class, Class.class, Integer.TYPE, Boolean.TYPE}, entityType.toLowerCase(), CraftCustomEntity.class, typeId, false);
                    EntityType.NAME_MAP.put(entityType.toLowerCase(), bukkitType);
                    EntityType.ID_MAP.put((short) typeId, bukkitType);
                    ENTITY_TYPES.put(entity.getValue(), entityType);
                    debug("Injecting Forge Entity into Bukkit: " +  entityType);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        debug("Injecting Forge Entity into Bukkit: DONE");
    }

    private static void addForgeVillagerProfessions() {
        ForgeRegistries.PROFESSIONS.forEach(villagerProfession -> {
            ResourceLocation resourceLocation = villagerProfession.getRegistryName();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                String name = normalizeName(resourceLocation.toString());
                try {
                    Villager.Profession profession = EnumJ17Helper.addEnum0(Villager.Profession.class, name, new Class[0]);
                    PROFESSION_MAP.put(profession, resourceLocation);
                    debug("Injecting Forge VillagerProfession into Bukkit: " +  profession.name());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        debug("Injecting Forge VillagerProfession into Bukkit: DONE");
    }

    public static void addForgeEnvironment(net.minecraft.core.Registry<LevelStem> registry) {
        int i = World.Environment.values().length;
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : registry.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            World.Environment environment1 = environment.get(key);
            if (environment1 == null) {
                String name = normalizeName(key.location().toString());
                int id = i - 1;
                environment1 = EnumJ17Helper.addEnum(World.Environment.class, name, new Class[] {Integer.TYPE}, new Object[] {id});
                environment.put(key, environment1);
                debug(String.format("Injected new Forge DimensionType %s.", environment1));
                i++;
            }
        }
    }

    private static String normalizeName(String name) {
        return name.toUpperCase(java.util.Locale.ENGLISH).replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
    }
}
