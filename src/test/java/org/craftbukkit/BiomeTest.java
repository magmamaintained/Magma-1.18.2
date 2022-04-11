package org.craftbukkit;

import net.minecraft.data.BuiltinRegistries;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.craftbukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class BiomeTest extends AbstractTestingBase {

    @Test
    public void testBukkitToMinecraft() {
        for (Biome biome : Biome.values()) {
            if (biome == Biome.CUSTOM) {
                continue;
            }

            Assert.assertNotNull("No NMS mapping for " + biome, CraftBlock.biomeToBiomeBase(BuiltinRegistries.BIOME, biome));
        }
    }

    @Test
    public void testMinecraftToBukkit() {
        for (net.minecraft.world.level.biome.Biome biomeBase : BuiltinRegistries.BIOME) {
            Biome biome = CraftBlock.biomeBaseToBiome(BuiltinRegistries.BIOME, biomeBase);
            Assert.assertTrue("No Bukkit mapping for " + biomeBase, biome != null && biome != Biome.CUSTOM);
        }
    }
}
