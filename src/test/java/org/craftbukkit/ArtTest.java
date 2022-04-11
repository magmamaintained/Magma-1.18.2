package org.craftbukkit;

import java.util.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;
import org.bukkit.Art;
import org.bukkit.craftbukkit.v1_18_R2.CraftArt;
import org.craftbukkit.support.AbstractTestingBase;
import org.junit.Test;

public class ArtTest extends AbstractTestingBase {

    private static final int UNIT_MULTIPLIER = 16;

    @Test
    public void verifyMapping() {
        List<Art> arts = Lists.newArrayList(Art.values());

        for (ResourceLocation key : net.minecraft.core.Registry.MOTIVE.keySet()) {
            Motive enumArt = Registry.MOTIVE.get(key);
            String name = key.getPath();
            int width = enumArt.getWidth() / UNIT_MULTIPLIER;
            int height = enumArt.getHeight() / UNIT_MULTIPLIER;

            Art subject = CraftArt.NotchToBukkit(enumArt);

            String message = String.format("org.bukkit.Art is missing '%s'", name);
            assertNotNull(message, subject);

            assertThat(Art.getByName(name), is(subject));
            assertThat("Art." + subject + "'s width", subject.getBlockWidth(), is(width));
            assertThat("Art." + subject + "'s height", subject.getBlockHeight(), is(height));

            arts.remove(subject);
        }

        assertThat("org.bukkit.Art has too many arts", arts, is(Collections.EMPTY_LIST));
    }

    @Test
    public void testCraftArtToNotch() {
        Map<Motive, Art> cache = new HashMap<>();
        for (Art art : Art.values()) {
            Motive enumArt = CraftArt.BukkitToNotch(art);
            assertNotNull(art.name(), enumArt);
            assertThat(art.name(), cache.put(enumArt, art), is(nullValue()));
        }
    }

    @Test
    public void testCraftArtToBukkit() {
        Map<Art, Motive> cache = new EnumMap(Art.class);
        for (Motive enumArt : Registry.MOTIVE) {
            Art art = CraftArt.NotchToBukkit(enumArt);
            assertNotNull("Could not CraftArt.NotchToBukkit " + enumArt, art);
            assertThat("Duplicate artwork " + enumArt, cache.put(art, enumArt), is(nullValue()));
        }
    }
}
