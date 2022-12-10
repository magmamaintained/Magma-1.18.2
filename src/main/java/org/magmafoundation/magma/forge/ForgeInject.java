package org.magmafoundation.magma.forge;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_18_R2.CraftStatistic;
import org.bukkit.craftbukkit.v1_18_R2.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.craftbukkit.entity.CraftCustomEntity;
import org.magmafoundation.magma.helpers.EnumJ17Helper;
import org.magmafoundation.magma.util.ResourceLocationUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeInject {

    public static BiMap<ResourceKey<LevelStem>, World.Environment> environments = HashBiMap
            .create(ImmutableMap.<ResourceKey<LevelStem>, World.Environment>builder()
                    .put(LevelStem.OVERWORLD, World.Environment.NORMAL)
                    .put(LevelStem.NETHER, World.Environment.NETHER)
                    .put(LevelStem.END, World.Environment.THE_END)
                    .build());

    public static final Map<Villager.Profession, ResourceLocation> PROFESSION_MAP = new ConcurrentHashMap<>();
    public static final Map<net.minecraft.world.entity.EntityType<?>, String> ENTITY_TYPES = new ConcurrentHashMap<>();

    public static void init() {
        debug("Injecting Forge Materials into Bukkit");
        addForgeMaterials();
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
        debug("Injecting Forge statistics into bukkit");
        addForgeStatistics();

        debug("Injecting Forge into Bukkit: DONE");

        try {
            for (var field : org.bukkit.Registry.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.get(null) instanceof org.bukkit.Registry.SimpleRegistry<?> registry) {
                    registry.reloader.run();
                }
            }
        } catch (Throwable ignored) {}
    }

    private static void debug(String message) {
        if (MagmaConfig.instance.debugPrintInjections.getValues())
            Magma.LOGGER.warn(message);
        else
            Magma.LOGGER.debug(message);
    }

    private static void error(String message) {
        Magma.LOGGER.error(message);
    }

    private static void addForgeMaterials() {
        int ordinal = Material.values().length;
        List<Material> values = new ArrayList<>();
        int origin = ordinal;
        int blocks = 0;
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            var location = entry.getKey().location();
            if (location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                continue;
            }
            // inject block materials into Bukkit for FML
            var enumName = ResourceLocationUtil.standardize(location);
            var block = entry.getValue();
            var item = ForgeRegistries.ITEMS.getValue(location);
            try {
                var material = Material.addMaterial(enumName, ordinal,
                        CraftNamespacedKey.fromMinecraft(location), true, item != null && item != Items.AIR);
                if (material == null) {
                    Magma.LOGGER.warn("Could not inject block into Bukkit: " + enumName);
                    continue;
                }
                ordinal++;
                values.add(material);
                blocks++;
                CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
                CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                debug("Injecting Forge Blocks into Bukkit: " + material.name());
            } catch (Throwable e) {
                error("Could not inject block into Bukkit: " + enumName + ". " + e.getMessage());
            }
        }
        debug("Injecting Forge Blocks into Bukkit: DONE");

        int items = 0;
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            var location = entry.getKey().location();
            if (location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                continue;
            }
            // inject item materials into Bukkit for FML
            var enumName = ResourceLocationUtil.standardize(location);
            var item = entry.getValue();
            var material = Material.getMaterial(enumName);
            // Material may already be registered by a block
            if (material == null) {
                try {
                    material = Material.addMaterial(enumName, ordinal, CraftNamespacedKey.fromMinecraft(location),
                            false, true);
                    if (material == null) {
                        Magma.LOGGER.warn("Could not inject item into Bukkit: " + enumName);
                        continue;
                    }
                    values.add(material);
                    ordinal++;
                    items++;
                    debug("Injecting Forge Material into Bukkit: " + material.name());
                } catch (Throwable e) {
                    error("Could not inject item into Bukkit: " + enumName + ". " + e.getMessage());
                }
            }
            CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
            CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
        }
        debug("Injecting Forge Material into Bukkit: DONE");
        EnumJ17Helper.addEnums(Material.class, values);
        Magma.LOGGER.info("Injected {} modded materials ({} blocks, {} items)", ordinal - origin, blocks, items);
    }

    private static void addForgeEnchantments() {
        ForgeRegistries.ENCHANTMENTS.getEntries().forEach(entry -> {
            CraftEnchantment enchantment = new CraftEnchantment(entry.getValue());
            if (!org.bukkit.enchantments.Enchantment.byKey.containsKey(enchantment.getKey())
                    || !org.bukkit.enchantments.Enchantment.byName.containsKey(enchantment.getName())) {
                org.bukkit.enchantments.Enchantment.byKey.put(enchantment.getKey(), enchantment);
                org.bukkit.enchantments.Enchantment.byName.put(enchantment.getName(), enchantment);
                debug("Injecting Forge Enchantments into Bukkit: " + enchantment.getName());
            }
        });
        debug("Injecting Forge Enchantments into Bukkit: DONE");
    }

    private static void addForgePotions() {
        PotionEffectType.startAcceptingRegistrations();
        ForgeRegistries.MOB_EFFECTS.getEntries().forEach(entry -> {
            var pet = new CraftPotionEffectType(entry.getValue());

            if (pet == null)
                return;

            try {
                PotionEffectType.registerPotionEffectType(pet);
                debug("Registering Forge Potion into Bukkit: " + pet.getName());
            } catch (IllegalStateException e) {
                error("Could not register potion effect into Bukkit: " + pet.getName() + ". " + e.getMessage());
            }
        });
        PotionEffectType.stopAcceptingRegistrations();
        // Stage 1 complete - now to add the types to bukkit

        int ordinal = EntityType.values().length;
        List<PotionType> values = new ArrayList<>();
        BiMap<PotionType, String> newRegular = HashBiMap.create(CraftPotionUtil.regular);
        for (var entry : ForgeRegistries.POTIONS.getEntries()) {
            var location = entry.getKey().location();
            if (location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                continue;
            }
            var enumName = ResourceLocationUtil.standardize(location);
            var potion = entry.getValue();
            var effect = potion.getEffects().isEmpty() ? null : potion.getEffects().get(0);
            var type = effect == null
                    ? null
                    : PotionEffectType.getById(MobEffect.getId(effect.getEffect()));
            try {
                var potionType = EnumJ17Helper.makeEnum(PotionType.class, enumName, ordinal,
                        List.of(PotionEffectType.class, boolean.class, boolean.class),
                        List.of(type, false, false));
                ordinal++;
                values.add(potionType);
                newRegular.put(potionType, potion.getRegistryName().toString());
                debug("Injecting Forge Potion into Bukkit: " + potionType.name());
            } catch (Throwable e) {
                error("Could not inject potion into Bukkit: " + enumName + ". " + e.getMessage());
            }
        }
        CraftPotionUtil.regular = newRegular;
        EnumJ17Helper.addEnums(PotionType.class, values);
        debug("Injecting Forge Potion into Bukkit: DONE");
    }

    private static void addForgeBiomes() {
        int ordinal = EntityType.values().length;
        List<Biome> values = new ArrayList<>();
        for (var entry : ForgeRegistries.BIOMES.getEntries()) {
            var location = entry.getKey().location();
            if (location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                continue;
            }
            var enumName = ResourceLocationUtil.standardize(location);
            try {
                var biome = EnumJ17Helper.makeEnum(Biome.class, enumName, ordinal, List.of(), List.of());
                ordinal++;
                values.add(biome);
                debug("Injecting Forge Biome into Bukkit: " + biome.name());
            } catch (Throwable e) {
                error("Could not inject biome into Bukkit: " + enumName + ". " + e.getMessage());
            }
        }
        EnumJ17Helper.addEnums(Biome.class, values);
        debug("Injecting Forge Biome into Bukkit: DONE");
    }

    private static void addForgeEntities() {
        int ordinal = EntityType.values().length;
        List<EntityType> values = new ArrayList<>();
        for (var entry : ForgeRegistries.ENTITIES.getEntries()) {
            var location = entry.getValue().getRegistryName();
            if (location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                continue;
            }
            var enumName = ResourceLocationUtil.standardize(location);
            int typeId = enumName.hashCode();
            try {
                var bukkitType = EnumJ17Helper.makeEnum(EntityType.class, enumName, ordinal,
                        List.of(String.class, Class.class, Integer.TYPE, Boolean.TYPE),
                        List.of(enumName.toLowerCase(), CraftCustomEntity.class, typeId, false));
                EntityType.NAME_MAP.put(enumName.toLowerCase(), bukkitType);
                EntityType.ID_MAP.put((short) typeId, bukkitType);
                ordinal++;
                values.add(bukkitType);
                ENTITY_TYPES.put(entry.getValue(), enumName);
                debug("Injecting Forge Entity into Bukkit: " + enumName);
            } catch (Throwable e) {
                error("Could not inject entity into Bukkit: " + enumName + ". " + e.getMessage());
            }
        }
        EnumJ17Helper.addEnums(EntityType.class, values);
        debug("Injecting Forge Entity into Bukkit: DONE");
    }

    private static void addForgeVillagerProfessions() {
        int ordinal = Villager.Profession.values().length;
        List<Villager.Profession> values = new ArrayList<>();
        for (var entry : ForgeRegistries.PROFESSIONS.getEntries()) {
            var location = entry.getKey().location();
            if (!location.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                var enumName = ResourceLocationUtil.standardize(location);
                try {
                    var profession = EnumJ17Helper.makeEnum(Villager.Profession.class, enumName, ordinal, List.of(),
                            List.of());
                    values.add(profession);
                    ordinal++;
                    PROFESSION_MAP.put(profession, location);
                    debug("Injecting Forge VillagerProfession into Bukkit: " + profession.name());
                } catch (Throwable e) {
                    error("Could not inject villager profession into Bukkit: " + enumName + ". " + e.getMessage());
                }
            }
        }
        EnumJ17Helper.addEnums(Villager.Profession.class, values);
        debug("Injecting Forge VillagerProfession into Bukkit: DONE");
    }

    public static void addForgeEnvironment(net.minecraft.core.Registry<LevelStem> registry) {
        int ordinal = World.Environment.values().length;
        List<World.Environment> values = new ArrayList<>();
        for (var entry : registry.entrySet()) {
            var key = entry.getKey();
            var environment = environments.get(key);
            if (environment == null) {
                var enumName = ResourceLocationUtil.standardize(key.location());
                environment = EnumJ17Helper.makeEnum(World.Environment.class, enumName, ordinal, List.of(Integer.TYPE),
                        List.of(ordinal - 1));
                values.add(environment);
                environments.put(key, environment);
                debug(String.format("Injected new Forge DimensionType %s.", environment));
                ordinal++;
            }
        }
        EnumJ17Helper.addEnums(World.Environment.class, values);
    }

    public static void addForgeStatistics() {
        List<Statistic> values = new ArrayList<>();
        var statistics = HashBiMap.create(CraftStatistic.statistics);
        int ordinal = Statistic.values().length;
        for (var entry : Registry.CUSTOM_STAT.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey().location();
            assert resourceLocation != null;
            if (!resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                var name = ResourceLocationUtil.standardize(resourceLocation);
                try {
                    var statistic = EnumJ17Helper.makeEnum(Statistic.class, name, ordinal, List.of(), List.of());
                    values.add(statistic);
                    statistics.put(resourceLocation, statistic);
                    debug("Injected Forge Statistic into Bukkit: " + statistic.name());
                } catch (Throwable e) {
                    error("Could not inject statistic into Bukkit: " + name + ". " + e.getMessage());
                }
            }
        }
        EnumJ17Helper.addEnums(Statistic.class, values);
        CraftStatistic.statistics = statistics;
        debug("Injecting Forge Statistic into Bukkit: DONE");
    }
}
