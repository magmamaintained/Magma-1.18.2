package org.magmafoundation.magma.util;

import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class LogUtils {

    private static final List<BlockPos> holders = new ArrayList<>();

    public static void infoOnce(Class<?> logClass, String message, BlockPos holder) {
        logOnce(logClass, message, holder, false, false);
    }

    public static void warnOnce(Class<?> logClass, String message, BlockPos holder) {
        logOnce(logClass, message, holder, true, false);
    }

    public static void debugOnce(Class<?> logClass, String message, BlockPos holder) {
        logOnce(logClass, message, holder, false, true);
    }

    private static void logOnce(Class<?> logClass, String message, BlockPos holder, boolean warn, boolean debug) {
        if (holders.contains(holder)) return;
        final Logger logger = LogManager.getLogger(logClass);

        if (debug)
            logger.debug(message);
        else if (warn)
            logger.warn(message);
        else
            logger.info(message);

        holders.add(holder);
    }

    public static void removeHolder(BlockPos holder) {
        holders.remove(holder);
    }
}
