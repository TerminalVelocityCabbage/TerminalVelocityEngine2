package com.terminalvelocitycabbage.engine.util;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.server.ServerBase;

public class EntrypointUtils {

    public static boolean isCurrentEntrypointClient() {
        return ClientBase.getInstance() != null;
    }

    public static boolean isCurrentEntrypointServer() {
        return ServerBase.getInstance() != null;
    }

    /**
     * @return The entrypoint in this current context
     */
    public static Entrypoint getEntrypoint() {
        if (isCurrentEntrypointClient()) return ClientBase.getInstance();
        if (isCurrentEntrypointServer()) return ServerBase.getInstance();
        return null;
    }
}
