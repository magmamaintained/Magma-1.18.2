package org.magmafoundation.magma.util;

import java.util.Arrays;

public final class IgnoreUtil {

    public static final String[] DO_NOT_CHECK = {
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
    };

    public static boolean shouldCheck(String classpath) {
        return Arrays.stream(DO_NOT_CHECK).parallel().noneMatch(classpath::startsWith);
    }
}
