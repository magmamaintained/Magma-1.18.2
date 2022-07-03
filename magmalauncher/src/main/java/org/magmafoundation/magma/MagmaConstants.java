package org.magmafoundation.magma;

import java.util.Objects;

public class MagmaConstants {
    private static final String fullVersion = (MagmaStart.class.getPackage().getImplementationVersion() != null) ? MagmaStart.class.getPackage().getImplementationVersion() : "dev-env";

    public static final String NAME = "Magma";
    public static final String BRAND = "MagmaFoundation";
    public static final String VERSION = !Objects.equals(fullVersion, "dev-env") ? fullVersion.split("-")[2] : "dev-env";
    public static final String BUKKIT_VERSION = "v1_18_R2";
    public static final String FORGE_VERSION_FULL = fullVersion;
    public static final String FORGE_VERSION = fullVersion.split("-")[1];
    public static final String NMS_PREFIX = "net/minecraft/server/";
    public static final String INSTALLER_LIBRARIES_FOLDER = "libraries";

}
