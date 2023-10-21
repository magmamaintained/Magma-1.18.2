package org.magmafoundation.magma.craftbukkit;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import org.magmafoundation.magma.util.IgnoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ItemMetaTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemMetaTransformer.class);
    private static final List<String> NOT_TRANSFORMABLE = new ArrayList<>();

    //Performance heavy, please only call when needed
    public static void loadPrefixes() {
        LOGGER.debug("Loading prefixes");
        NOT_TRANSFORMABLE.clear();

        //get the mod information synchronously, because IDK if there are any issues with async
        for (IModInfo mod:ModList.get().getMods()){
            final IModFileInfo owningFile = mod.getOwningFile();
            if (owningFile == null) continue;

            final IModFile modFile = owningFile.getFile();
            if (modFile == null) continue;

            final ModFileScanData scanData = modFile.getScanResult();
            if (scanData == null) continue;

            List<String> classes = scanData.getClasses().parallelStream()
                            .map(cl -> cl.clazz().getClassName())
                            .filter(IgnoreUtil::shouldCheck)
                            .toList();
            String prefix = getCommonPrefix(classes);
            if (prefix.isEmpty()) continue;
            long dots = prefix.chars().parallel()
                    .filter(c -> c == '.')
                    .count();
            if (dots <= 1) continue; //ignore top level packages
            LOGGER.debug("Adding " + prefix + " to non-transformable list");
            NOT_TRANSFORMABLE.add(prefix);
        }

        LOGGER.debug("Loaded {} prefixes", NOT_TRANSFORMABLE.size());
    }

    private static String getCommonPrefix(List<String> classes) {
        String prefix = "";
        for (String s : classes) {
            if (prefix.isEmpty()) {
                prefix = s;
                continue;
            }
            int i = 0;
            while (i < prefix.length() && i < s.length() && prefix.charAt(i) == s.charAt(i)) {
                i++;
            }
            prefix = prefix.substring(0, i);
        }
        return prefix;
    }

    public static boolean isTransformable(ItemStack item) {
        String pkg = item.getItem().getClass().getPackage().getName();

        if (pkg.startsWith("net.minecraft.")) //ignore vanilla
            return true;

        for (String s : NOT_TRANSFORMABLE) {
            if (pkg.startsWith(s))
                return false;
        }
        return true;
    }
}
