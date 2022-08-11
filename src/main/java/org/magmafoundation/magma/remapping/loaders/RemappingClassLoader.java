package org.magmafoundation.magma.remapping.loaders;

import cpw.mods.modlauncher.TransformingClassLoader;
import org.magmafoundation.magma.remapping.ClassLoaderRemapper;

/**
 * RemappingClassLoader
 *
 * @author Mainly by IzzelAliz and modified Malcolm
 * @originalClassName RemappingClassLoader
 * @classFrom <a href="https://github.com/IzzelAliz/Arclight/blob/1.18/arclight-common/src/main/java/io/izzel/arclight/common/mod/util/remapper/RemappingClassLoader.java">Click here to get to github</a>
 *
 * This classes is modified by Magma to support the Magma software.
 */
public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    static ClassLoader asTransforming(ClassLoader classLoader) {
        boolean found = false;
        while (classLoader != null) {
            if (classLoader instanceof TransformingClassLoader || classLoader instanceof RemappingClassLoader) {
                found = true;
                break;
            } else {
                classLoader = classLoader.getParent();
            }
        }
        return found ? classLoader : RemappingClassLoader.class.getClassLoader();
    }
}
