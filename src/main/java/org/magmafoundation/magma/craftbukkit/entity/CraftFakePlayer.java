package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraftforge.common.util.FakePlayer;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class CraftFakePlayer extends CraftPlayer {

    private final CraftInventoryPlayer inventory;

    public CraftFakePlayer(CraftServer server, FakePlayer entity) {
        super(server, entity);
        this.inventory = new CraftInventoryPlayer(entity.getInventory());
    }

    public @NotNull PlayerInventory getInventory() {
        return inventory;
    }
}
