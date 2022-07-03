package org.magmafoundation.magma.helpers;

import com.mojang.serialization.DynamicOps;
import joptsimple.OptionSet;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.DataPackConfig;

public class ServerInitHelper {

    private static OptionSet options;
    private static DataPackConfig dataPackConfig;
    private static DynamicOps<Tag> registryreadops;

    public static void init(OptionSet options, DataPackConfig p_129768_, DynamicOps<Tag> registryreadops) {
        ServerInitHelper.options = options;
        ServerInitHelper.dataPackConfig = p_129768_;
        ServerInitHelper.registryreadops = registryreadops;
    }

    public static OptionSet getOptions() {
        return options;
    }

    public static DataPackConfig getDataPackConfig() {
        return dataPackConfig;
    }

    public static DynamicOps<Tag> getRegistryReadOps() {
        return registryreadops;
    }
}
