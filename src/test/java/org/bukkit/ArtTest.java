package org.bukkit;

import java.util.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;
import org.bukkit.craftbukkit.v1_18_R2.CraftArt;
import org.junit.Test;

public class ArtTest {

    @Test(expected = NullPointerException.class)
    public void getByNullName() {
        Art.getByName(null);
    }

    @Test
    public void getById() {
        for (Art art : Art.values()) {
            assertThat(Art.getById(art.getId()), is(art));
        }
    }

    @Test
    public void getByName() {
        for (Art art : Art.values()) {
            assertThat(Art.getByName(art.toString()), is(art));
        }
    }

    @Test
    public void dimensionSanityCheck() {
        for (Art art : Art.values()) {
            assertThat(art.getBlockHeight(), is(greaterThan(0)));
            assertThat(art.getBlockWidth(), is(greaterThan(0)));
        }
    }

    @Test
    public void getByNameWithMixedCase() {
        Art subject = Art.values()[0];
        String name = subject.toString().replace('E', 'e');

        assertThat(Art.getByName(name), is(subject));
    }

}
