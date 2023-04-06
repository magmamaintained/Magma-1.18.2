package org.bukkit.entity;

import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an Ender Dragon
 */
public interface EnderDragon extends ComplexLivingEntity, Boss, Mob {

    /**
     * Represents a phase or action that an Ender Dragon can perform.
     */
    enum Phase {
        /**
         * The dragon will circle outside the ring of pillars if ender
         * crystals remain or inside the ring if not.
         */
        CIRCLING,
        /**
         * The dragon will fly towards a targeted player and shoot a
         * fireball when within 64 blocks.
         */
        STRAFING,
        /**
         * The dragon will fly towards the empty portal (approaching
         * from the other side, if applicable).
         */
        FLY_TO_PORTAL,
        /**
         * The dragon will land on on the portal. If the dragon is not near
         * the portal, it will fly to it before mounting.
         */
        LAND_ON_PORTAL,
        /**
         * The dragon will leave the portal.
         */
        LEAVE_PORTAL,
        /**
         * The dragon will attack with dragon breath at its current location.
         */
        BREATH_ATTACK,
        /**
         * The dragon will search for a player to attack with dragon breath.
         * If no player is close enough to the dragon for 5 seconds, the
         * dragon will charge at a player within 150 blocks or will take off
         * and begin circling if no player is found.
         */
        SEARCH_FOR_BREATH_ATTACK_TARGET,
        /**
         * The dragon will roar before performing a breath attack.
         */
        ROAR_BEFORE_ATTACK,
        /**
         * The dragon will charge a player.
         */
        CHARGE_PLAYER,
        /**
         * The dragon will fly to the vicinity of the portal and die.
         */
        DYING,
        /**
         * The dragon will hover at its current location, not performing any actions.
         */
        HOVER,
        /**
         * The bukkit phase is unknown. Likely caused by a modded entity.
         */
        UNKNOWN;

        //Magma start - return unknown when phase is not found
        public static @Nullable Phase getOrUnknown(int id) {
            final Phase[] values = values();
            return id >= 0 && id < values.length - 1 /* -1 to prevent UNKNOWN getting detected as valid phase */ ? values[id] : UNKNOWN;
        }
        //Magma end
    }

    /**
     * Gets the current phase that the dragon is performing.
     *
     * @return the current phase
     */
    @NotNull
    Phase getPhase();

    /**
     * Sets the next phase for the dragon to perform.
     *
     * @param phase the next phase
     */
    void setPhase(@NotNull Phase phase);

    /**
     * Get the {@link DragonBattle} associated with this EnderDragon.
     *
     * This will return null if the EnderDragon is not in the End dimension.
     *
     * @return the dragon battle
     *
     * @see World#getEnderDragonBattle()
     */
    @Nullable
    DragonBattle getDragonBattle();

    /**
     * Get the current time in ticks relative to the start of this dragon's
     * death animation.
     *
     * If this dragon is alive, 0 will be returned. This value will never exceed
     * 200 (the length of the animation).
     *
     * @return this dragon's death animation ticks
     */
    int getDeathAnimationTicks();
}
