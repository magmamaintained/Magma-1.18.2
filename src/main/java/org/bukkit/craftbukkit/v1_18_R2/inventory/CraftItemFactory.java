package org.bukkit.craftbukkit.v1_18_R2.inventory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftLegacy;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);
    private static final CraftItemFactory instance;

    static {
        instance = new CraftItemFactory();
        ConfigurationSerialization.registerClass(CraftMetaItem.SerializableMeta.class);
    }

    private CraftItemFactory() {
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
        if(itemstack == null) {
            return false;
        }
        return isApplicable(meta, itemstack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material type) {
        type = CraftLegacy.fromLegacy(type); // This may be called from legacy item stacks, try to get the right material
        if(type == null || meta == null) {
            return false;
        }
        if(!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + CraftItemFactory.class.getName());
        }

        return ((CraftMetaItem) meta).applicableTo(type);
    }

    @Override
    public ItemMeta getItemMeta(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, CraftMetaItem meta) {
        material = CraftLegacy.fromLegacy(material); // This may be called from legacy item stacks, try to get the right material

        try {

        return switch (material) {
            case AIR -> null;
            case WRITTEN_BOOK -> meta instanceof CraftMetaBookSigned ? meta : new CraftMetaBookSigned(meta);
            case WRITABLE_BOOK ->
                    meta != null && meta.getClass().equals(CraftMetaBook.class) ? meta : new CraftMetaBook(meta);
            case CREEPER_HEAD, CREEPER_WALL_HEAD, DRAGON_HEAD, DRAGON_WALL_HEAD, PLAYER_HEAD, PLAYER_WALL_HEAD, SKELETON_SKULL, SKELETON_WALL_SKULL, WITHER_SKELETON_SKULL, WITHER_SKELETON_WALL_SKULL, ZOMBIE_HEAD, ZOMBIE_WALL_HEAD ->
                    meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
            case LEATHER_HELMET, LEATHER_HORSE_ARMOR, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS ->
                    meta instanceof CraftMetaLeatherArmor ? meta : new CraftMetaLeatherArmor(meta);
            case POTION, SPLASH_POTION, LINGERING_POTION, TIPPED_ARROW ->
                    meta instanceof CraftMetaPotion ? meta : new CraftMetaPotion(meta);
            case FILLED_MAP -> meta instanceof CraftMetaMap ? meta : new CraftMetaMap(meta);
            case FIREWORK_ROCKET -> meta instanceof CraftMetaFirework ? meta : new CraftMetaFirework(meta);
            case FIREWORK_STAR -> meta instanceof CraftMetaCharge ? meta : new CraftMetaCharge(meta);
            case ENCHANTED_BOOK -> meta instanceof CraftMetaEnchantedBook ? meta : new CraftMetaEnchantedBook(meta);
            case BLACK_BANNER, BLACK_WALL_BANNER, BLUE_BANNER, BLUE_WALL_BANNER, BROWN_BANNER, BROWN_WALL_BANNER, CYAN_BANNER, CYAN_WALL_BANNER, GRAY_BANNER, GRAY_WALL_BANNER, GREEN_BANNER, GREEN_WALL_BANNER, LIGHT_BLUE_BANNER, LIGHT_BLUE_WALL_BANNER, LIGHT_GRAY_BANNER, LIGHT_GRAY_WALL_BANNER, LIME_BANNER, LIME_WALL_BANNER, MAGENTA_BANNER, MAGENTA_WALL_BANNER, ORANGE_BANNER, ORANGE_WALL_BANNER, PINK_BANNER, PINK_WALL_BANNER, PURPLE_BANNER, PURPLE_WALL_BANNER, RED_BANNER, RED_WALL_BANNER, WHITE_BANNER, WHITE_WALL_BANNER, YELLOW_BANNER, YELLOW_WALL_BANNER ->
                    meta instanceof CraftMetaBanner ? meta : new CraftMetaBanner(meta);
            case AXOLOTL_SPAWN_EGG, BAT_SPAWN_EGG, BEE_SPAWN_EGG, BLAZE_SPAWN_EGG, CAT_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, CHICKEN_SPAWN_EGG, COD_SPAWN_EGG, COW_SPAWN_EGG, CREEPER_SPAWN_EGG, DOLPHIN_SPAWN_EGG, DONKEY_SPAWN_EGG, DROWNED_SPAWN_EGG, ELDER_GUARDIAN_SPAWN_EGG, ENDERMAN_SPAWN_EGG, ENDERMITE_SPAWN_EGG, EVOKER_SPAWN_EGG, FOX_SPAWN_EGG, GHAST_SPAWN_EGG, GLOW_SQUID_SPAWN_EGG, GOAT_SPAWN_EGG, GUARDIAN_SPAWN_EGG, HOGLIN_SPAWN_EGG, HORSE_SPAWN_EGG, HUSK_SPAWN_EGG, LLAMA_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, MULE_SPAWN_EGG, OCELOT_SPAWN_EGG, PANDA_SPAWN_EGG, PARROT_SPAWN_EGG, PHANTOM_SPAWN_EGG, PIGLIN_BRUTE_SPAWN_EGG, PIGLIN_SPAWN_EGG, PIG_SPAWN_EGG, PILLAGER_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, PUFFERFISH_SPAWN_EGG, RABBIT_SPAWN_EGG, RAVAGER_SPAWN_EGG, SALMON_SPAWN_EGG, SHEEP_SPAWN_EGG, SHULKER_SPAWN_EGG, SILVERFISH_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG, SKELETON_SPAWN_EGG, SLIME_SPAWN_EGG, SPIDER_SPAWN_EGG, SQUID_SPAWN_EGG, STRAY_SPAWN_EGG, STRIDER_SPAWN_EGG, TRADER_LLAMA_SPAWN_EGG, TROPICAL_FISH_SPAWN_EGG, TURTLE_SPAWN_EGG, VEX_SPAWN_EGG, VILLAGER_SPAWN_EGG, VINDICATOR_SPAWN_EGG, WANDERING_TRADER_SPAWN_EGG, WITCH_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, WOLF_SPAWN_EGG, ZOGLIN_SPAWN_EGG, ZOMBIE_HORSE_SPAWN_EGG, ZOMBIE_SPAWN_EGG, ZOMBIE_VILLAGER_SPAWN_EGG, ZOMBIFIED_PIGLIN_SPAWN_EGG ->
                    meta instanceof CraftMetaSpawnEgg ? meta : new CraftMetaSpawnEgg(meta);
            case ARMOR_STAND -> meta instanceof CraftMetaArmorStand ? meta : new CraftMetaArmorStand(meta);
            case KNOWLEDGE_BOOK -> meta instanceof CraftMetaKnowledgeBook ? meta : new CraftMetaKnowledgeBook(meta);
            case FURNACE, CHEST, TRAPPED_CHEST, JUKEBOX, DISPENSER, DROPPER, ACACIA_SIGN, ACACIA_WALL_SIGN, BIRCH_SIGN, BIRCH_WALL_SIGN, CRIMSON_SIGN, CRIMSON_WALL_SIGN, DARK_OAK_SIGN, DARK_OAK_WALL_SIGN, JUNGLE_SIGN, JUNGLE_WALL_SIGN, OAK_SIGN, OAK_WALL_SIGN, SPRUCE_SIGN, SPRUCE_WALL_SIGN, WARPED_SIGN, WARPED_WALL_SIGN, SPAWNER, BREWING_STAND, ENCHANTING_TABLE, COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, BEACON, DAYLIGHT_DETECTOR, HOPPER, COMPARATOR, SHIELD, STRUCTURE_BLOCK, SHULKER_BOX, WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX, PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX, BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX, BLACK_SHULKER_BOX, ENDER_CHEST, BARREL, BELL, BLAST_FURNACE, CAMPFIRE, SOUL_CAMPFIRE, JIGSAW, LECTERN, SMOKER, BEEHIVE, BEE_NEST, SCULK_SENSOR ->
                    new CraftMetaBlockState(meta, material);
            case TROPICAL_FISH_BUCKET ->
                    meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
            case AXOLOTL_BUCKET -> meta instanceof CraftMetaAxolotlBucket ? meta : new CraftMetaAxolotlBucket(meta);
            case CROSSBOW -> meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
            case SUSPICIOUS_STEW -> meta instanceof CraftMetaSuspiciousStew ? meta : new CraftMetaSuspiciousStew(meta);
            case COD_BUCKET, PUFFERFISH_BUCKET, SALMON_BUCKET, ITEM_FRAME, GLOW_ITEM_FRAME, PAINTING ->
                    meta instanceof CraftMetaEntityTag ? meta : new CraftMetaEntityTag(meta);
            case COMPASS -> meta instanceof CraftMetaCompass ? meta : new CraftMetaCompass(meta);
            case BUNDLE -> meta instanceof CraftMetaBundle ? meta : new CraftMetaBundle(meta);
            default -> new CraftMetaItem(meta);
        };

        } catch (ArrayIndexOutOfBoundsException ex){
            System.err.println(ex.getMessage() + " (CraftItemFactory.java#getItemMeta(Material,CraftMetaItem))");
            return null;
        }
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if(meta1 == meta2) {
            return true;
        }
        if(meta1 != null && !(meta1 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if(meta2 != null && !(meta2 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if(meta1 == null) {
            return ((CraftMetaItem) meta2).isEmpty();
        }
        if(meta2 == null) {
            return ((CraftMetaItem) meta1).isEmpty();
        }

        return equals((CraftMetaItem) meta1, (CraftMetaItem) meta2);
    }

    boolean equals(CraftMetaItem meta1, CraftMetaItem meta2) {
        /*
         * This couldn't be done inside of the objects themselves, else force recursion.
         * This is a fairly clean way of implementing it, by dividing the methods into purposes and letting each method perform its own function.
         *
         * The common and uncommon were split, as both could have variables not applicable to the other, like a skull and book.
         * Each object needs its chance to say "hey wait a minute, we're not equal," but without the redundancy of using the 1.equals(2) && 2.equals(1) checking the 'commons' twice.
         *
         * Doing it this way fills all conditions of the .equals() method.
         */
        return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
    }

    public static CraftItemFactory instance() {
        return instance;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
        Validate.notNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) {
        Validate.notNull(material, "Material cannot be null");
        if(!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + CraftItemFactory.class.getName());
        }
        return getItemMeta(material, (CraftMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return DEFAULT_LEATHER_COLOR;
    }

    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        try {
            ItemParser arg = new ItemParser(new StringReader(input), false).parse(); // false = no tags

            Item item = arg.getItem();
            net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item);

            CompoundTag nbt = arg.getNbt();
            if(nbt != null) {
                nmsItemStack.setTag(nbt);
            }

            return CraftItemStack.asCraftMirror(nmsItemStack);
        } catch (CommandSyntaxException ex) {
            throw new IllegalArgumentException("Could not parse ItemStack: " + input, ex);
        }
    }

    @Override
    public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
        return ((CraftMetaItem) meta).updateMaterial(material);
    }
}
