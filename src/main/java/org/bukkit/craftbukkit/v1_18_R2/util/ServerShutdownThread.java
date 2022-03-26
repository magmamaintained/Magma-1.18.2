package org.bukkit.craftbukkit.v1_18_R2.util;

import net.minecraft.server.MinecraftServer;

public class ServerShutdownThread extends Thread {
    private final MinecraftServer server;

    public ServerShutdownThread(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            //MAGMA START - moved Forge shutdown hook to here
            if (!(server instanceof net.minecraft.gametest.framework.GameTestServer))
                server.halt(true);
            //TODO-PATCHING: Figure out what needs to be done here after the logging update.
            //LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
            org.apache.logging.log4j.LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
            //MAGMA END
            server.close();
        } finally {
            try {
                server.reader.getTerminal().restore();
            } catch (Exception e) {
            }
        }
    }
}
