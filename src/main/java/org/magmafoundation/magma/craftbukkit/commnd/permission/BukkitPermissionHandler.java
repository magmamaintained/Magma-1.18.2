package org.magmafoundation.magma.craftbukkit.commnd.permission;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class BukkitPermissionHandler implements IPermissionHandler {

    private final IPermissionHandler delegate;

    public BukkitPermissionHandler(IPermissionHandler delegate) {
        Objects.requireNonNull(delegate, "permission handler");
        this.delegate = delegate;

        delegate.getRegisteredNodes().parallelStream().parallel().forEach(node -> {
            if (node.getType() == PermissionTypes.BOOLEAN) {
                DefaultPermissions.registerPermission(new Permission(node.getNodeName(), node.getDescription().getString(), PermissionDefault.FALSE), false);
            }
        });
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation("magma_permission_handler", "permission");
    }

    @Override
    public Set<PermissionNode<?>> getRegisteredNodes() {
        return delegate.getRegisteredNodes();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        if (node.getType() == PermissionTypes.BOOLEAN) {
            return (T) (Object) player.getBukkitEntity().hasPermission(node.getNodeName());
        } else {
            return delegate.getPermission(player, node, context);
        }
    }

    @Override
    public <T> T getOfflinePermission(UUID uuid, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
        var player = Bukkit.getPlayer(uuid);
        if (player != null && node.getType() == PermissionTypes.BOOLEAN) {
            return (T) (Object) player.hasPermission(node.getNodeName());
        } else {
            return delegate.getOfflinePermission(uuid, node, context);
        }
    }
}