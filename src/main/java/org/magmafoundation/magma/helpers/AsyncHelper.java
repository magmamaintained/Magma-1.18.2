package org.magmafoundation.magma.helpers;

import net.minecraftforge.fml.ModList;
import org.bukkit.event.Event;

public class AsyncHelper {

    private static String[] modIds = {
            "blue_skies",
            "the_vault"
    };

    public static boolean canRunAsync(Event event) {
        for (String modId : modIds) {
            if (isModLoaded(modId))
                return true;
        }

        return false;
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
