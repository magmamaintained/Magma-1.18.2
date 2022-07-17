package org.magmafoundation.magma.craftbukkit.commnd.permission;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.*;

/**
 * Project: Magma
 * <p>
 * Author: Malcolm (M1lc0lm)
 * Date: 17.07.2022 - 14:00
 */
public class BukkitPermissionHandler implements IPermissionHandler {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation("bukkit", "bukkit_handler");
    ;
    private final Set<PermissionNode<?>> registeredNodes = new HashSet<>();
    private Set<PermissionNode<?>> immutableRegisteredNodes = Collections.unmodifiableSet(this.registeredNodes);

    //TODO: Add finish BukkitPermissionHandler

    public BukkitPermissionHandler(Collection<PermissionNode<?>> permissions) {

    }

    @Override
    public ResourceLocation getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<PermissionNode<?>> getRegisteredNodes() {
        return immutableRegisteredNodes;
    }

    @Override
    public <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        return node.getDefaultResolver().resolve(player, player.getUUID(), context);
    }

    @Override
    public <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        return node.getDefaultResolver().resolve(null, player, context);
    }

}
