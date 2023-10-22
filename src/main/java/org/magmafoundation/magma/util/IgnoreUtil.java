package org.magmafoundation.magma.util;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;

public final class IgnoreUtil {

    private static final ObjectImmutableList<String> DO_NOT_CHECK = ObjectImmutableList.of(
            "java.",
            "net.minecraft.",
            "net.minecraftforge.",
            "org.bukkit.",
            "org.magmafoundation.",
            "org.spigotmc.",
            "com.mojang.",
            "io.papermc.",
            "co.aikar.",
            "com.destroystokyo.",
            "jdk.internal.",
            "cpw.mods."
    );

    //Don't parallelize this, because it's already used in a parallel stream
    public static boolean shouldCheck(final String classpath) {
        for (final String clazz : DO_NOT_CHECK){
            if (classpath.startsWith(clazz))
                return false;
        }
        return true;
    }
}
