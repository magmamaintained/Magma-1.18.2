package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.TamableAnimal;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftTameableAnimal;

public class CraftCustomTamable extends CraftTameableAnimal {

    public CraftCustomTamable(CraftServer server, TamableAnimal entity) {
        super(server, entity);
    }
}
