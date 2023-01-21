package org.bukkit.craftbukkit.v1_18_R2.entity;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.DragonFireball;

public class CraftDragonFireball extends CraftFireball implements DragonFireball {
    public CraftDragonFireball(CraftServer server, net.minecraft.world.entity.projectile.DragonFireball entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftDragonFireball";
    }
}
