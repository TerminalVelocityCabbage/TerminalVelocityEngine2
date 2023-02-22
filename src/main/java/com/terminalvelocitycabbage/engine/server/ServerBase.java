package com.terminalvelocitycabbage.engine.server;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.util.TickManager;

public abstract class ServerBase extends Entrypoint {

    private static ServerBase instance; //A singleton to represent the server for this program

    private boolean shouldStop;
    private TickManager tickManager;

    public ServerBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
    }

    /**
     * Starts this server program
     */
    public void start() {
        getInstance().init();
        getInstance().run();
        getInstance().destroy();
    }

    /**
     * initializes the game loop
     */
    private void run() {

        while (!shouldStop) {
            tickManager.update();
            while (tickManager.hasTick()) {
                tick();
            }
        }
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
