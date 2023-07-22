package org.magmafoundation.magma.craftbukkit;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
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

        Map<String, List<String>> classesByMod = new HashMap<>();
        ModList.get().getMods().forEach(mod -> {
            final IModFileInfo owningFile = mod.getOwningFile();
            if (owningFile == null)
                return;

            final IModFile modFile = owningFile.getFile();
            if (modFile == null)
                return;

            final ModFileScanData scanData = modFile.getScanResult();
            if (scanData == null)
                return;

            scanData.getClasses().forEach(cl -> {
                final String className = cl.clazz().getClassName();
                if (!IgnoreUtil.shouldCheck(className))
                    return;
                classesByMod.computeIfAbsent(mod.getModId(), s -> new ArrayList<>()).add(className);
            });
        });

        classesByMod.forEach((modid, classes) -> {
            String prefix = getCommonPrefix(classes);
            if (prefix.isEmpty())
                return;

            int dots = 0;
            for (int i = 0; i < prefix.length(); i++) {
                if (prefix.charAt(i) == '.')
                    dots++;
            }
            if (dots <= 1) //ignore top level packages
                return;

            LOGGER.debug("Adding " + prefix + " to non-transformable list");
            NOT_TRANSFORMABLE.add(prefix);
        });

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
