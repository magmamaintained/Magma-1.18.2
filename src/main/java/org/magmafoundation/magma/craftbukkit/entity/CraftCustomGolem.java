package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.animal.AbstractGolem;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftGolem;

public class CraftCustomGolem extends CraftGolem {

    public CraftCustomGolem(CraftServer server, AbstractGolem entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftCustomGolem";
    }
}
