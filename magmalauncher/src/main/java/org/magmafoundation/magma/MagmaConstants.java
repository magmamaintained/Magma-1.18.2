package org.magmafoundation.magma;

import org.magmafoundation.magma.utils.DataParser;

public class MagmaConstants {
    private static final String fullVersion = "magma-"+DataParser.versionMap.get("magma");

    public static final String NAME = "Magma";
    public static final String BRAND = "MagmaFoundation";
    public static final String VERSION = fullVersion.split("-")[3];
    public static final String BUKKIT_VERSION = "v1_18_R2";
    public static final String FORGE_VERSION_FULL = fullVersion;
    public static final String FORGE_VERSION = fullVersion.split("-")[2];
    public static final String NMS_PREFIX = "net/minecraft/server/";
    public static final String INSTALLER_LIBRARIES_FOLDER = "libraries";
}
