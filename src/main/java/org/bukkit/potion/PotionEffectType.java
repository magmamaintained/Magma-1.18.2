package org.bukkit.potion;

import java.util.*;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a type of potion and its effect on an entity.
 */
public abstract class PotionEffectType implements Keyed {
    /**
     * Increases movement speed.
     */
    public static final PotionEffectType SPEED = new PotionEffectTypeWrapper(1, "speed");

    /**
     * Decreases movement speed.
     */
    public static final PotionEffectType SLOW = new PotionEffectTypeWrapper(2, "slowness");

    /**
     * Increases dig speed.
     */
    public static final PotionEffectType FAST_DIGGING = new PotionEffectTypeWrapper(3, "haste");

    /**
     * Decreases dig speed.
     */
    public static final PotionEffectType SLOW_DIGGING = new PotionEffectTypeWrapper(4, "mining_fatigue");

    /**
     * Increases damage dealt.
     */
    public static final PotionEffectType INCREASE_DAMAGE = new PotionEffectTypeWrapper(5, "strength");

    /**
     * Heals an entity.
     */
    public static final PotionEffectType HEAL = new PotionEffectTypeWrapper(6, "instant_health");

    /**
     * Hurts an entity.
     */
    public static final PotionEffectType HARM = new PotionEffectTypeWrapper(7, "instant_damage");

    /**
     * Increases jump height.
     */
    public static final PotionEffectType JUMP = new PotionEffectTypeWrapper(8, "jump_boost");

    /**
     * Warps vision on the client.
     */
    public static final PotionEffectType CONFUSION = new PotionEffectTypeWrapper(9, "nausea");

    /**
     * Regenerates health.
     */
    public static final PotionEffectType REGENERATION = new PotionEffectTypeWrapper(10, "regeneration");

    /**
     * Decreases damage dealt to an entity.
     */
    public static final PotionEffectType DAMAGE_RESISTANCE = new PotionEffectTypeWrapper(11, "resistance");

    /**
     * Stops fire damage.
     */
    public static final PotionEffectType FIRE_RESISTANCE = new PotionEffectTypeWrapper(12, "fire_resistance");

    /**
     * Allows breathing underwater.
     */
    public static final PotionEffectType WATER_BREATHING = new PotionEffectTypeWrapper(13, "water_breathing");

    /**
     * Grants invisibility.
     */
    public static final PotionEffectType INVISIBILITY = new PotionEffectTypeWrapper(14, "invisibility");

    /**
     * Blinds an entity.
     */
    public static final PotionEffectType BLINDNESS = new PotionEffectTypeWrapper(15, "blindness");

    /**
     * Allows an entity to see in the dark.
     */
    public static final PotionEffectType NIGHT_VISION = new PotionEffectTypeWrapper(16, "night_vision");

    /**
     * Increases hunger.
     */
    public static final PotionEffectType HUNGER = new PotionEffectTypeWrapper(17, "hunger");

    /**
     * Decreases damage dealt by an entity.
     */
    public static final PotionEffectType WEAKNESS = new PotionEffectTypeWrapper(18, "weakness");

    /**
     * Deals damage to an entity over time.
     */
    public static final PotionEffectType POISON = new PotionEffectTypeWrapper(19, "poison");

    /**
     * Deals damage to an entity over time and gives the health to the
     * shooter.
     */
    public static final PotionEffectType WITHER = new PotionEffectTypeWrapper(20, "wither");

    /**
     * Increases the maximum health of an entity.
     */
    public static final PotionEffectType HEALTH_BOOST = new PotionEffectTypeWrapper(21, "health_boost");

    /**
     * Increases the maximum health of an entity with health that cannot be
     * regenerated, but is refilled every 30 seconds.
     */
    public static final PotionEffectType ABSORPTION = new PotionEffectTypeWrapper(22, "absorption");

    /**
     * Increases the food level of an entity each tick.
     */
    public static final PotionEffectType SATURATION = new PotionEffectTypeWrapper(23, "saturation");

    /**
     * Outlines the entity so that it can be seen from afar.
     */
    public static final PotionEffectType GLOWING = new PotionEffectTypeWrapper(24, "glowing");

    /**
     * Causes the entity to float into the air.
     */
    public static final PotionEffectType LEVITATION = new PotionEffectTypeWrapper(25, "levitation");

    /**
     * Loot table luck.
     */
    public static final PotionEffectType LUCK = new PotionEffectTypeWrapper(26, "luck");

    /**
     * Loot table unluck.
     */
    public static final PotionEffectType UNLUCK = new PotionEffectTypeWrapper(27, "unluck");

    /**
     * Slows entity fall rate.
     */
    public static final PotionEffectType SLOW_FALLING = new PotionEffectTypeWrapper(28, "slow_falling");

    /**
     * Effects granted by a nearby conduit. Includes enhanced underwater abilities.
     */
    public static final PotionEffectType CONDUIT_POWER = new PotionEffectTypeWrapper(29, "conduit_power");

    /**
     * Increses underwater movement speed.<br>
     * Squee'ek uh'k kk'kkkk squeek eee'eek.
     */
    public static final PotionEffectType DOLPHINS_GRACE = new PotionEffectTypeWrapper(30, "dolphins_grace");

    /**
     * Triggers a raid when the player enters a village.<br>
     * oof.
     */
    public static final PotionEffectType BAD_OMEN = new PotionEffectTypeWrapper(31, "bad_omen");

    /**
     * Reduces the cost of villager trades.<br>
     * \o/.
     */
    public static final PotionEffectType HERO_OF_THE_VILLAGE = new PotionEffectTypeWrapper(32, "hero_of_the_village");

    private final int id;
    private final NamespacedKey key;
    public static final PotionEffectType[] byId = new PotionEffectType[1250];
    public static final Map<String, PotionEffectType> byName = new HashMap<>();
    public static final Map<NamespacedKey, PotionEffectType> byKey = new HashMap<>();
    private static boolean acceptingNew = true;

    protected PotionEffectType(int id, @NotNull NamespacedKey key) {
        this.id = id;
        this.key = key;
    }

    @NotNull
    public PotionEffect createEffect(int duration, int amplifier) {
        return new PotionEffect(this, this.isInstant() ? 1 : (int)((double)duration * this.getDurationModifier()), amplifier);
    }

    /** @deprecated */
    @Deprecated
    public abstract double getDurationModifier();

    /** @deprecated */
    @Deprecated
    public int getId() {
        return this.id;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    public abstract String getName();

    public abstract boolean isInstant();

    @NotNull
    public abstract Color getColor();

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof PotionEffectType)) {
            return false;
        } else {
            PotionEffectType other = (PotionEffectType)obj;
            return this.id == other.id;
        }
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "PotionEffectType[" + this.id + ", " + this.getName() + "]";
    }

    @Contract("null -> null")
    @Nullable
    public static PotionEffectType getByKey(@Nullable NamespacedKey key) {
        return byKey.get(key);
    }

    /** @deprecated */
    @Deprecated
    @Nullable
    public static PotionEffectType getById(int id) {
        return id < byId.length && id >= 0 ? byId[id] : null;
    }

    @Nullable
    public static PotionEffectType getByName(@NotNull String name) {
        Validate.notNull(name, "name cannot be null");
        return byName.get(name.toLowerCase(Locale.ENGLISH));
    }

    public static void registerPotionEffectType(@NotNull PotionEffectType type) {
        if (byId[type.id] == null && !byName.containsKey(type.getName().toLowerCase(Locale.ENGLISH)) && !byKey.containsKey(type.key)) {
            if (!acceptingNew) {
                throw new IllegalStateException("No longer accepting new potion effect types");
            } else {
                byId[type.id] = type;
                byName.put(type.getName().toLowerCase(Locale.ENGLISH), type);
                byKey.put(type.key, type);
            }
        } else {
            throw new IllegalArgumentException("Cannot set already-set type");
        }
    }

    public static void startAcceptingRegistrations() {
        acceptingNew = true;
    }

    public static void stopAcceptingRegistrations() {
        acceptingNew = false;
    }

    @NotNull
    public static PotionEffectType[] values() {
        return Arrays.stream(Arrays.copyOfRange(byId, 1, byId.length)).filter(Objects::nonNull).toArray(PotionEffectType[]::new);
    }
}
