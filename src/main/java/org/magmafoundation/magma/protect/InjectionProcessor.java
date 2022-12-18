package org.magmafoundation.magma.protect;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;
import org.magmafoundation.magma.common.utils.ShortenedStackTrace;

import java.util.Optional;

public class InjectionProcessor {

    public static @Nullable String getErroringMixin(Throwable error) {
        Throwable cause = ShortenedStackTrace.findCause(error);
        String message = cause.getMessage();

        //example: Critical injection failure: LVT in ... has incompatible changes at opcode ... in callback MODHERE.mixins.json:MIXINCLASSHERE->@...
        int index = message.indexOf("in callback") + "in callback".length() + 1;
        try {
            return message.substring(index, message.indexOf("(", index));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static @Nullable String getModID(Throwable error) {
        String file = getErroringMixin(error);
        if (file == null)
            return null;

        int index = file.indexOf(".mixins.json");
        if (index == -1)
            return null;

        return file.substring(0, index);
    }

    public static @Nullable IModInfo getModInfo(Throwable error) {
        ModList modList = ModList.get();
        if (modList == null)
            return null;

        String modid = getModID(error);
        if (modid == null)
            return null;

        Optional<? extends ModContainer> modContainerById = modList.getModContainerById(modid);
        return modContainerById.map(ModContainer::getModInfo).orElse(null);

    }
}
