package org.magmafoundation.magma.remapping;

import org.objectweb.asm.tree.ClassNode;

/**
 * PluginTransformer
 *
 * @author Mainly by IzzelAliz and modified Malcolm
 * @originalClassName PluginTransformer
 * @classFrom <a href="https://github.com/IzzelAliz/Arclight/blob/1.18/arclight-common/src/main/java/io/izzel/arclight/common/mod/util/remapper/PluginTransformer.java">Click here to get to github</a>
 *
 * This classes is modified by Magma to support the Magma software.
 */
public interface PluginTransformer {

    void handleClass(ClassNode node, ClassLoaderRemapper remapper);

    default int priority() {
        return 0;
    }
}
