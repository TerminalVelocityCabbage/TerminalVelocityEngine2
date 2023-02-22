package com.terminalvelocitycabbage.engine.server;

import com.terminalvelocitycabbage.engine.Entrypoint;

public abstract class ServerBase extends Entrypoint {

    private static ServerBase instance; //A singleton to represent the server for this program

    public ServerBase(String namespace) {
        super(namespace);
        instance = this;
    }

    /**
     * Gets the singleton instance of this server
     * @return This server instance
     */
    public static ServerBase getInstance() {
        return instance;
    }

    /**
     * The logic to be executed when this Server updates
     */
    public abstract void tick();
}
