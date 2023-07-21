package org.magmafoundation.magma.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.Map;

public class ForgeInventoryHelper {

    private static final Map<Inventory, Inventory> captures = new HashMap<>();

    public static void capturePlayerInventory(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            Inventory inventory = player.getInventory();

            Inventory capture = new Inventory(player);
            player.setInventory(capture);

            captures.put(capture, inventory);
        }
    }

    public static Inventory restorePlayerInventory(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            Inventory capture = player.getInventory();

            Inventory inventory = captures.get(capture);
            captures.remove(capture);
            if (inventory != null) {
                player.setInventory(inventory);
                return capture;
            }
        }
        return null;
    }
}
