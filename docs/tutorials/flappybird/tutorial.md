# Flappy Bird Tutorial
Welcome to the first tutorial for using Terminal Velocity Engine

This tutorial will guide you through creating a simple Flappy Bird clone using Terminal Velocity Engine. We will cover the basics of setting up the game, creating game objects, and implementing game logic.

## Getting Started
The first thing you'll need to do is include Terminal Velocity Engine in your project. We recommend git submodules for this. Look at our example repository for a working setup: [TVETest - GitHub](https://github.com/TerminalVelocityCabbage/TVETest) for some more detailed best practices for setting up a full project structure.

In this tutorial, we will keep thinks as simple as possible. We won't be using best practices for organization of files and will be sticking to a single Java file for our flappy bird game. You can follow along in this tutorial or check out the full source code on GitHub.

### Creating a Client Game
Flappy Bird doesn't require multiplayer, so we only need a client module. To do this, just make your main class extend `ClientBase` and you're pretty much done. Just instantiate this client in the main method and call `start();` and you're good to go!

```java
public class FlappyBirdClient extends ClientBase {

    public static final String ID = "flappybird";

    public FlappyBirdClient(String namespace, int ticksPerSecond) {
        super(namespace, ticksPerSecond);
    }

    public static void main(String[] args) {
        FlappyBirdClient client = new FlappyBirdClient("flappybird", 60);
        client.start();
    }
}
```

### Creating a window
The next logical step is to create a window for our game to render to. To do this we need to override the init method in our client class.

Here we can configure the window properties and create a window from these properties.
```java
    @Override
    public void init() {
        super.init();

        //Create window properties
        WindowProperties windowProperties = new WindowProperties(800, 600, "Flappy Bird", null);
        //Create a window
        long window = getWindowManager().createNewWindow(windowProperties);
        //Focus window
        getWindowManager().focusWindow(window);
    }
```

Here you can see we are creating a window with a width of 800 pixels, and a height of 600 pixels. The window will be titled "Flappy Bird".
You'll notice that we are not setting a scene for the window, we'll come back to that later.

After we configure the window, we create it with the `createNewWindow` method. This method returns a long, which is the native pointer to the window.

