package org.magmafoundation.magma.helpers;

import net.minecraftforge.fml.ModList;
import org.bukkit.event.Event;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class AsyncHelper {

    public static boolean canRunAsync(Event event) {
        if (isModLoaded("the_vault") && (event instanceof EntitiesLoadEvent || event instanceof EntitiesUnloadEvent))
            return true;
        return false;
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
