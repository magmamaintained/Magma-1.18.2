package org.magmafoundation.magma.commands;

public final class DispatcherRedirector {

    // Sorted by most common to least common (or at least what I think is most common)
    private static final String[] DO_NOT_CHECK = {
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

    private static final String[] BYPASSED_CLASSES = {
            "org.popcraft.chunky.ChunkyForge"
    };

    public static boolean shouldBypass() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (!shouldCheck(element.getClassName())) // Internal classes should never be bypassed
                continue;

            for (String bypassedClass : BYPASSED_CLASSES) {
                if (element.getClassName().equals(bypassedClass))
                    return true;
            }
        }
        return false;
    }

    private static boolean shouldCheck(String classpath) {
        for (String doNotCheck : DO_NOT_CHECK) {
            if (classpath.startsWith(doNotCheck))
                return false;
        }
        return true;
    }
}
