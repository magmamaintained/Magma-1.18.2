package org.magmafoundation.magma.commands;

import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.util.IgnoreUtil;

public final class DispatcherRedirector {

    private static final String[] BYPASSED_CLASSES = {
            "org.popcraft.chunky.ChunkyForge",
            "team.creative.playerrevive.PlayerRevive",
    };

    public static boolean shouldBypass() {
        if(MagmaConfig.instance.debugOverrideDispatRedirector.getValues())
            return false;
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (!IgnoreUtil.shouldCheck(element.getClassName())) // Internal classes should never be bypassed
                continue;

            for (String bypassedClass : BYPASSED_CLASSES) {
                if (element.getClassName().equals(bypassedClass))
                    return true;
            }
        }
        return false;
    }
}
