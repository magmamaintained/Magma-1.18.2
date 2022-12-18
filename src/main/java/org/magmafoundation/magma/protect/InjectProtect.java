package org.magmafoundation.magma.protect;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.common.betterui.BetterUI;
import org.magmafoundation.magma.common.utils.ShortenedStackTrace;
import org.magmafoundation.magma.util.InjectSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.throwables.MixinError;

import java.util.ArrayList;
import java.util.List;

public class InjectProtect {

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectProtect.class);
    private static final List<InjectSet> errors = new ArrayList<>();

    public static void init() {
        LOGGER.info("Booting up InjectProtect");
        Mixins.registerErrorHandlerClass(MixinErrorHandler.class.getCanonicalName());
    }

    public static void onBootErrorCaught(MixinError error) {
        LOGGER.warn("Caught exception during server boot phase, shutting down server");
        BetterUI.printError("Mixin related error", InjectionProcessor.getErroringMixin(error), new ShortenedStackTrace(error, 5));
        System.exit(1);
    }

    public static void mixinInjectCaught(IMixinInfo info, Throwable t) {
        LOGGER.warn("Caught mixin injection error!");
        errors.add(InjectSet.of(info, t));
    }

    public static void shutdownCalled() {
        LOGGER.debug("Processing shutdown request");
        if (errors.isEmpty()) {
            LOGGER.debug("No errors found, shutting down");
            return;
        }

        if (errors.size() == 1) {
            LOGGER.debug("Found 1 error, showing user friendly error");
            Throwable t = errors.get(0).getThrowable();
            BetterUI.printError("Mixin injection error", InjectionProcessor.getErroringMixin(t) + getMod(t), new ShortenedStackTrace(t, 5));
            return;
        }

        LOGGER.debug("Found {} errors, showing user friendly error", errors.size());
        ShortenedStackTrace[] traces = new ShortenedStackTrace[errors.size()];
        String modIDS = "";
        for (int i = 0; i < errors.size(); i++) {
            Throwable t = errors.get(i).getThrowable();
            traces[i] = new ShortenedStackTrace(t, 5);
            modIDS += InjectionProcessor.getErroringMixin(t) + ", ";
        }

        modIDS = modIDS.substring(0, modIDS.length() - 2);

        BetterUI.printError("Mixin injection errors", "Multiple errors: " + modIDS, traces);
    }

    private static @NotNull String getMod(Throwable t) {
        IModInfo modInfo = InjectionProcessor.getModInfo(t);
        return modInfo == null ? "" : " (" + modInfo.getDisplayName() + ")";
    }
}
