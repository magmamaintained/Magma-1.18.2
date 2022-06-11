package org.spigotmc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;

public class TrackingRange
{

    /**
     * Gets the range an entity should be 'tracked' by players and visible in
     * the client.
     *
     * @param entity
     * @param defaultRange Default range defined by Mojang
     * @return
     */
    public static int getEntityTrackingRange(Entity entity, int defaultRange)
    {
        if ( defaultRange == 0 )
        {
            return defaultRange;
        }
        SpigotWorldConfig config = entity.level.spigotConfig;
        if ( entity instanceof Player)
        {
            return config.playerTrackingRange;
        } else if ( entity.activationType == ActivationRange.ActivationType.MONSTER || entity.activationType == ActivationRange.ActivationType.RAIDER )
        {
            return config.monsterTrackingRange;
        } else if ( entity instanceof Ghast)
        {
            if ( config.monsterTrackingRange > config.monsterActivationRange )
            {
                return config.monsterTrackingRange;
            } else
            {
                return config.monsterActivationRange;
            }
        } else if ( entity.activationType == ActivationRange.ActivationType.ANIMAL )
        {
            return config.animalTrackingRange;
        } else if ( entity instanceof ItemFrame || entity instanceof Painting || entity instanceof ItemEntity || entity instanceof ExperienceOrb)
        {
            return config.miscTrackingRange;
        } else
        {
            return config.otherTrackingRange;
        }
    }
}