package org.bukkit.potion;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class PotionEffectTypeWrapper extends PotionEffectType {
    protected PotionEffectTypeWrapper(int id, @NotNull String name) {
        super(id, NamespacedKey.minecraft(name));
    }

    public double getDurationModifier() {
        return this.getType().getDurationModifier();
    }

    @NotNull
    public String getName() {
        return this.getType().getName();
    }

    @NotNull
    public PotionEffectType getType() {
        return PotionEffectType.getById(this.getId());
    }

    public boolean isInstant() {
        return this.getType().isInstant();
    }

    @NotNull
    public Color getColor() {
        return this.getType().getColor();
    }
}
