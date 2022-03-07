package org.bukkit.craftbukkit.v1_18_R2.entity;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.entity.ChestedHorse;

public abstract class CraftChestedHorse extends CraftAbstractHorse implements ChestedHorse {

    public CraftChestedHorse(CraftServer server, net.minecraft.world.entity.animal.horse.AbstractChestedHorse entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.horse.AbstractChestedHorse getHandle() {
        return (net.minecraft.world.entity.animal.horse.AbstractChestedHorse) super.getHandle();
    }

    @Override
    public boolean isCarryingChest() {
        return getHandle().hasChest();
    }

    @Override
    public void setCarryingChest(boolean chest) {
        if (chest == isCarryingChest()) return;
        getHandle().setChest(chest);
        getHandle().createInventory();
    }
}
