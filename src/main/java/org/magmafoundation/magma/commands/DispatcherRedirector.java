package org.magmafoundation.magma.commands;

import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.util.IgnoreUtil;

import java.util.Arrays;

public final class DispatcherRedirector {

    private static final String[] BYPASSED_CLASSES = {
            "org.popcraft.chunky.ChunkyForge",
            "team.creative.playerrevive.PlayerRevive",
    };

    public static boolean shouldBypass() {
        if(MagmaConfig.instance.debugOverrideDispatRedirector.getValues())
            return false;
        return Arrays.stream(Thread.currentThread().getStackTrace()).parallel()
                .map(StackTraceElement::getClassName)
                .filter(IgnoreUtil::shouldCheck)
                .anyMatch(s -> Arrays.stream(BYPASSED_CLASSES).parallel().anyMatch(s::equals));
    }
}
