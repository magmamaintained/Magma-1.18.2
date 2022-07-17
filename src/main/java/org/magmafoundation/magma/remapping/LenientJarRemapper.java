package org.magmafoundation.magma.remapping;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;

//Class Made by Arclight (Izzel)
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
