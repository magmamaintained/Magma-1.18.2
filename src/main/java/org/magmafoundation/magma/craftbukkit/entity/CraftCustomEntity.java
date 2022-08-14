package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CraftCustomEntity extends CraftEntity {

    private String entityName;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
        if (entityName == null) {
            entityName = entity.getName().getString();
        }
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    public LivingEntity asLivingEntity() {
        try {
            return (LivingEntity) entity;
        } catch (ClassCastException e) {
            System.err.println("Attempted to call asLivingEntity() on a non-LivingEntity entity");
            System.err.println("Entity name: " + entityName);
            System.err.println("Entity type: " + entity.getType());
            System.err.println("Entity class: " + entity.getClass());
            return null;
        }
    }

    @Override
    public @NotNull EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        return type != null ? type : EntityType.UNKNOWN;
    }

    @Override
    public String toString() {
        return entityName;
    }

    @Override
    public String getCustomName() {
        String name = this.getHandle().getCustomName().getString();
        return name == null || name.length() == 0 ? this.entity.getName().getString() : name;
    }
}
