package com.terminalvelocitycabbage.tutorial;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public class FlappyBirdClient extends ClientBase {

    public static final String ID = "flappybird";

    public FlappyBirdClient(String namespace, int ticksPerSecond) {
        super(namespace, ticksPerSecond);
    }

    public static void main(String[] args) {
        FlappyBirdClient client = new FlappyBirdClient("flappybird", 60);
        client.start();
    }

    @Override
    public void init() {
        super.init();

        //Create window properties
        WindowProperties windowProperties = new WindowProperties(800, 600, "Flappy Bird", null);
        //Create window
        long window = getWindowManager().createNewWindow(windowProperties);
        //Focus window
        getWindowManager().focusWindow(window);
    }
}