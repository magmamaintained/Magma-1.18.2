package org.craftbukkit.support;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.junit.Assert;

/**
 *  If you are getting: java.lang.ExceptionInInitializerError
 *    at net.minecraft.server.StatisticList.&lt;clinit&gt;(SourceFile:58)
 *    at net.minecraft.server.Item.&lt;clinit&gt;(SourceFile:252)
 *    at net.minecraft.server.Block.&lt;clinit&gt;(Block.java:577)
 *
 *  extend this class to solve it.
 */
public abstract class AbstractTestingBase {
    // Materials that only exist in block form (or are legacy)
    public static final List<Material> INVALIDATED_MATERIALS;

    public static final ReloadableServerResources DATA_PACK;

    static {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        // Set up resource manager
        MultiPackResourceManager resourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, Collections.singletonList(new VanillaPackResources(ServerPacksSource.BUILT_IN_METADATA, "minecraft")));
        // add tags and loot tables for unit tests
        RegistryAccess.Frozen registry = RegistryAccess.builtinCopy().freeze();
        // Register vanilla pack
        DATA_PACK = ReloadableServerResources.loadResources(resourceManager, registry, Commands.CommandSelection.DEDICATED, 0, MoreExecutors.directExecutor(), MoreExecutors.directExecutor()).join();
        // Bind tags
        DATA_PACK.updateRegistryTags(registry);

        DummyServer.setup();
        DummyEnchantments.setup();

        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for (Material m : Material.values()) {
            if (m.isLegacy() || CraftMagicNumbers.getItem(m) == null) {
                builder.add(m);
            }
        }
        INVALIDATED_MATERIALS = builder.build();
        Assert.assertEquals("Expected 590 invalidated materials (got " + INVALIDATED_MATERIALS.size() + ")", 590, INVALIDATED_MATERIALS.size());
    }
}
