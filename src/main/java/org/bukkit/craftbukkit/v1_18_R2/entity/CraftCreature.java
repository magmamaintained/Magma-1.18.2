package org.bukkit.craftbukkit.v1_18_R2.entity;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.Creature;

public class CraftCreature extends CraftMob implements Creature {
    public CraftCreature(CraftServer server, net.minecraft.world.entity.PathfinderMob entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.PathfinderMob getHandle() {
        return (net.minecraft.world.entity.PathfinderMob) entity;
    }

    @Override
    public String toString() {
        return "CraftCreature";
    }
}
