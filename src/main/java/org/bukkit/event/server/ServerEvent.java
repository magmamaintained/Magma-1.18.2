package org.bukkit.event.server;

import org.bukkit.event.Event;

/**
 * Miscellaneous server events
 */
public abstract class ServerEvent extends Event {

    public ServerEvent() {
        super();
    }

    public ServerEvent(boolean isAsync) {
        super(isAsync);
    }

    public ServerEvent(boolean isAsync, boolean runFromAllThreads) {
        super(isAsync, runFromAllThreads);
    }
}
