package org.magmafoundation.magma.util;

import java.util.Locale;

import org.bukkit.NamespacedKey;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtil {

    public static String standardize(ResourceLocation location) {
        Preconditions.checkNotNull(location, "location");
        return (location.getNamespace().equals(NamespacedKey.MINECRAFT) ? location.getPath() : location.toString())
            .replace(':', '_')
            .replaceAll("\\s+", "_")
            .replaceAll("\\W", "")
            .toUpperCase(Locale.ENGLISH);
    }

    public static String standardizeLower(ResourceLocation location) {
        return (location.getNamespace().equals(NamespacedKey.MINECRAFT) ? location.getPath() : location.toString())
            .replace(':', '_')
            .replaceAll("\\s+", "_")
            .replaceAll("\\W", "")
            .toLowerCase(Locale.ENGLISH);
    }
}