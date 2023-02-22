package com.terminalvelocitycabbage.engine.client;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.util.TickManager;

public abstract class ClientBase extends Entrypoint {

    private static ClientBase instance; //A singleton to represent the client for this program
    private Window window;
    private RendererBase renderer;
    private TickManager tickManager;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
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
        window.run();
    }

    @Override
    public void init() {
        window = new Window();
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
    public void update() {
        tickManager.update();
        while (tickManager.hasTick()) {
            tick();
        }
    }

    /**
     * The code to be executed every tick
     * This is mainly used for networking tasks, most things for clients should happen every frame
     */
    public abstract void tick();

    public Window getWindow() {
        return window;
    }

    protected void setRenderer(RendererBase renderer) {
        this.renderer = renderer;
    }

    public RendererBase getRenderer() {
        return renderer;
    }
}
