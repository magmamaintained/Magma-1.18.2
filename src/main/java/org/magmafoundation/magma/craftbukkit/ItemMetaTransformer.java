package org.magmafoundation.magma.craftbukkit;

import net.minecraft.util.SortedArraySet;
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
    private static final SortedArraySet<String> NOT_TRANSFORMABLE = SortedArraySet.create();

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

            String prefix = null;
            int dots = 0;
            for (final ModFileScanData.ClassData clazzData : scanData.getClasses()) {
                final String className = clazzData.clazz().getClassName();
                if (className == null || className.isEmpty() || numberDots(className) < 1 || !IgnoreUtil.shouldCheck(className)) continue;
                if (prefix == null) {
                    prefix = className;
                    dots = numberDots(prefix);
                    if (dots <= 1) break; //fast-fail out of the loop
                    continue;
                }
                int i = 0;
                while (i < prefix.length() && i < className.length() && prefix.charAt(i) == className.charAt(i)) {
                    i++;
                }
                int less_dots = numberDots(prefix.substring(i));
                //detect if we would over-shorten the path and exit before we do
                if (dots - less_dots <= 1) break;
                //`i` is fine to use as the index, because it is guaranteed to be smaller than the length of className and prefix
                prefix = prefix.substring(0, i);
                dots -= less_dots; //calculating the amount of dots left in the rest of the prefix.
                //this allows us to fast-fail out of the loop, since we only really care about if the common prefix has 2 or more dots
                if (dots <= 1) break;
            }
            //we fast-fail out of the inner loop. now we can actually properly continue, since we are in the outer loop
            if (dots <= 1) continue; //ignore top level packages
            if (!isClassTransformable(prefix)) {
                LOGGER.debug("Skipping " + prefix + " because it is already not transformable");
                continue;
            }
            LOGGER.debug("Adding " + prefix + " to non-transformable list");
            NOT_TRANSFORMABLE.add(prefix);
        }

        LOGGER.debug("Loaded {} prefixes", NOT_TRANSFORMABLE.size());
    }

    private static int numberDots(String s) {
        int i = 0;
        for (final char c : s.toCharArray()) {
            if (c == '.') i++;
        }
        return i;
    }
    public static boolean isTransformable(ItemStack item) {
        String pkg = item.getItem().getClass().getPackage().getName();

        if (pkg.startsWith("net.minecraft.")) //ignore vanilla
            return true;

        return isClassTransformable(pkg);
    }
    public static boolean isClassTransformable(final String name){
        for (String s : NOT_TRANSFORMABLE) {
            if (name.startsWith(s))
                return false;
        }
        return true;
    }
}
