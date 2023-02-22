package com.terminalvelocitycabbage.engine.client;

import com.terminalvelocitycabbage.engine.Entrypoint;

public abstract class ClientBase extends Entrypoint {

    private static ClientBase instance; //A singleton to represent the client for this program

    public ClientBase(String namespace) {
        super(namespace);
        instance = this;
    }

    /**
     * Starts this client program
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
        //TODO update and tick loops
        update();
        tick();
    }

    /**
     * Gets the singleton instance of this client
     * @return this client instance
     */
    public static ClientBase getInstance() {
        return instance;
    }

    /**
     * The code to be executed every frame
     */
    public abstract void update();

    /**
     * The code to be executed every tick
     * This is mainly used for networking tasks, most things for clients should happen every frame
     */
    public abstract void tick();
}
