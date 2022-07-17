package org.magmafoundation.magma.remapping;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;

/**
 * LenientJarRemapper
 *
 * @author Mainly by IzzelAliz and modified Malcolm
 * @originalClassName LenientJarRemapper
 * @classFrom <a href="https://github.com/IzzelAliz/Arclight/blob/1.18/arclight-common/src/main/java/io/izzel/arclight/common/mod/util/remapper/LenientJarRemapper.java">Click here to get to github</a>
 *
 * This classes is modified by Magma to support the Magma software.
 */
public class LenientJarRemapper extends JarRemapper {

    public LenientJarRemapper(JarMapping jarMapping) {
        super(jarMapping);
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        try {
            return super.mapSignature(signature, typeSignature);
        } catch (Exception e) {
            return signature;
        }
    }
}
