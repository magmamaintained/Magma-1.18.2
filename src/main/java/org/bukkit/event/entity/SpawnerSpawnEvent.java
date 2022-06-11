package org.bukkit.event.entity;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Magma
 * <p>
 * Author: Malcolm (M1lc0lm)
 * Date: 11.06.2022
 */
public class SpawnerSpawnEvent extends EntitySpawnEvent {

    private final CreatureSpawner spawner;

    public SpawnerSpawnEvent(@NotNull final Entity spawnee, @NotNull final CreatureSpawner spawner) {
        super(spawnee);
        this.spawner = spawner;
    }

    @NotNull
    public CreatureSpawner getSpawner() {
        return spawner;
    }
}
