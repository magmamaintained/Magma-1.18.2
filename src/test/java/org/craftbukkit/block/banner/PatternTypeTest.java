package org.craftbukkit.block.banner;

import junit.framework.Assert;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.bukkit.block.banner.PatternType;
import org.craftbukkit.support.AbstractTestingBase;
import org.junit.Test;

public class PatternTypeTest extends AbstractTestingBase {

    @Test
    public void testToBukkit() {
        for (BannerPattern nms : BannerPattern.values()) {
            PatternType bukkit = PatternType.getByIdentifier(nms.getHashname());

            Assert.assertNotNull("No Bukkit banner for " + nms + " " + nms.getHashname(), bukkit);
        }
    }

    @Test
    public void testToNMS() {
        for (PatternType bukkit : PatternType.values()) {
            BannerPattern found = null;
            for (BannerPattern nms : BannerPattern.values()) {
                if (bukkit.getIdentifier().equals(nms.getHashname())) {
                    found = nms;
                    break;
                }
            }

            Assert.assertNotNull("No NMS banner for " + bukkit + " " + bukkit.getIdentifier(), found);
        }
    }
}
