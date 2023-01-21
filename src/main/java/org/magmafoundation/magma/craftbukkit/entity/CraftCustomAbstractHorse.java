package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftAbstractHorse;
import org.bukkit.entity.Horse;

public class CraftCustomAbstractHorse extends CraftAbstractHorse {

    public CraftCustomAbstractHorse(CraftServer server, AbstractHorse entity) {
        super(server, entity);
    }

    @Override
    public Horse.Variant getVariant() {
        return Horse.Variant.MODDED;
    }
}
