package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.registries.ForgeRegistries;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecart;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.forge.ForgeInject;

public class CraftCustomMinecart extends CraftMinecart {

    private final EntityType entityType;

    public CraftCustomMinecart(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
        this.entityType = EntityType.valueOf(ForgeInject.ENTITY_TYPES.get(ForgeRegistries.ENTITIES.getKey(entity.getType())));
    }

    @Override
    public @NotNull EntityType getType() {
        return entityType;
    }

    @Override
    public String toString() {
        return "CraftCustomMinecart{" +
                "entityType=" + entityType +
                '}';
    }
}
