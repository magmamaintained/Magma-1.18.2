package org.magmafoundation.magma.helpers;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class BorderHelper {

    private static final Map<ClientboundInitializeBorderPacket, ServerPlayer> BORDER_PACKETS = new HashMap<>();

    public static @Nullable Level getLevelFromPlayer(ClientboundInitializeBorderPacket packet) {
        final ServerPlayer serverPlayer = BORDER_PACKETS.get(packet);
        return serverPlayer == null ? null : serverPlayer.level;
    }

    public static void captureBorderPacket(Packet<?> packet, ServerPlayer player) {
        if (!(packet instanceof ClientboundInitializeBorderPacket borderPacket))
            return;

        BORDER_PACKETS.put(borderPacket, player);
    }

    public static void clearCapture(Packet<?> packet) {
        if (!(packet instanceof ClientboundInitializeBorderPacket borderPacket))
            return;

        BORDER_PACKETS.remove(borderPacket);
    }
}
