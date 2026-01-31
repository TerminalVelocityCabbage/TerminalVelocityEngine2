# Flappy Bird Tutorial
Welcome to the first tutorial for using Terminal Velocity Engine

This tutorial will guide you through creating a simple Flappy Bird clone using Terminal Velocity Engine. We will cover the basics of setting up the game, creating game objects, and implementing game logic.

## Core Concepts
This tutorial does not cover the core concepts of Terminal Velocity Engine, so if you are new to the engine, please read the rest of the documentation for things on how to use:
- ECS (make this a link when it's done)
- Identifiers
- Resources
- [UI](../../ui.md)

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

## Creating a window
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

## Registering Assets
Next we need to register our assets. The way we do this is by adding resources to our project and registering them to the filesystem. TVE provides some events to listen to so that you always register your resources in order. The engine also has some predefiend expected locations for resources, and provides some utilities for registering the resource locations that the engine expects. Advanced users are free to redefine these locations, but for 99% of users, just use the default locations.

The default locations for the resource types that we will be using in this game are:
- `assets/textures` for textures
- `assets/shaders` for shaders
- `assets/fonts` for fonts
- //TODO sounds

First let's upload an image for the bird:
![Bird Image](src/main/resources/assets/textures/bird.png)

And create our vertex and fragment shaders:

`default.vert`
```glsl
#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;

out vec2 outTextureCoord;

void main()
{
    gl_Position = position;
    outTextureCoord = textureCoord;
}
```
`default.frag`
```glsl
#version 330

in vec2 outTextureCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    fragColor = texture(textureSampler, outTextureCoord);
}
```
This will be enough assets for now. We'll come back to the rest later once we've done the rest of the setup.

### Listinging to registration events

To listen to events, just get the event dispatcher with `getEventDispatcher()` and call `listenToEvent` on it in your client constructor.

The events we need to listen to are:
- `ResourceCategoryRegistrationEvent`
- `ResourceSourceRegistrationEvent`
- `ResourceRegistrationEvent` for shaders and textures (more later)

See below for an example of how to listen to these events:
```java
public FlappyBirdClient(String namespace, int ticksPerSecond) {
    super(namespace, ticksPerSecond);
    //Listen to events
    getEventDispatcher().listenToEvent(ResourceCategoryRegistrationEvent.EVENT, e -> { });
    getEventDispatcher().listenToEvent(ResourceSourceRegistrationEvent.EVENT, e -> { });
    getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.SHADER), e -> { });
    getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.TEXTURE), e -> { });
    getEventDispatcher().listenToEvent(ConfigureTextures.EVENT, e -> { });
}
```

### Configure Filesystem

Now to actually do something with these:
Since we're using the default resource locations, we can just call `registerEngineDefaults` for resource categories.
```java
getEventDispatcher().listenToEvent(ResourceCategoryRegistrationEvent.EVENT, (ResourceCategoryRegistrationEvent) event -> {
    ResourceCategory.registerEngineDefaults(event.getRegistry(), ID);
});
```
For the resource sources, we are going to just tell the filesystem to look for resources in the `assets` folder of this project. For more complicated projects you may have mod soruces, or os filesystem sources. We will jsut include everything in the resources folder for now. Since we will be reffering back to this source later, we need to store an identifier to this source as a field on our client.

```java
getEventDispatcher().listenToEvent(ResourceSourceRegistrationEvent.EVENT, (ResourceSourceRegistrationEvent) event -> {
    //Register and init filesystem things
    //Create resource sources for this client
    ResourceSource mainSource = new MainSource(getInstance());
    //Define roots for these resources based on default resoruce categories
    clientSource.registerDefaultSources(ID);
    //register this source
    CLIENT_RESOURCE_SOURCE = event.registerResourceSource(ID, "main",mainSource);
});
```

### Registering Shaders

Shaders require a bit more setup - we need to not only register the shader resources, but also configure a shader program for the renderer to use. Here we specify the mesh format, and define all the uniforms that this shader needs to run in addition to all of the shader stages that are required.
```java
getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.SHADER), e -> {
    ResourceRegistrationEvent event = (ResourceRegistrationEvent) e;
    //Register shader resources
    DEFAULT_VERTEX_SHADER = event.registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.SHADER, "default.vert").getIdentifier();
    DEFAULT_FRAGMENT_SHADER = event.registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.SHADER, "default.frag").getIdentifier();
    //Configure the shader program
    DEFAULT_SHADER_PROGRAM_CONFIG = ShaderProgramConfig.builder()
            .vertexFormat(MESH_FORMAT)
            .addShader(Shader.Type.VERTEX, DEFAULT_VERTEX_SHADER)
            .addShader(Shader.Type.FRAGMENT, DEFAULT_FRAGMENT_SHADER)
            .addUniform(new Uniform("textureSampler"))
            .build();
});
```
To configure the mesh format you just need to tell it what the vertex format for the elements rendered with this shader looks like. These need to match the layouts defined in the shaders themselves:
```java
public static final VertexFormat MESH_FORMAT = VertexFormat.builder()
        .addElement(VertexAttribute.XYZ_POSITION)
        .addElement(VertexAttribute.UV)
        .build();
```

### Registering Textures

Like shaders textures require two steps. Registering texture resources and configuring textures to use an atlas. Technically the seccond step is optional, but it is best practice to register your textures to an atlas to reduce uploads to the gpu in the form of texture binds.
```java
getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.TEXTURE), e -> {
    //Register texture resources
    BIRD_TEXTURE = ((ResourceRegistrationEvent) e).registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.TEXTURE, "bird.png").getIdentifier();
});
getEventDispatcher().listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
    ConfigureTexturesEvent event = (ConfigureTexturesEvent) e;
    //Register a default atlas with the name "atlas"
    TEXTURE_ATLAS = event.registerAtlas(ID, "atlas");
    //Add textures to atlas
    event.addTexture(BIRD_TEXTURE, TEXTURE_ATLAS);
});
```

## Registering Entity Templates
Before we can add anything to a scene (next step), we need to register our entity templates.
That means registering the components our entities will use, the systems that will update them, and creating template entities that are composed of our components


## Creating a Scene and Render Graph
Now; we've got all the resources we need, but now we need to tell the renderer how to render them This comes in two parts, defining the renderer and defining a scene.

To create a scene we need a class that extends `Scene` and a bit of setup and cleanup code:
```java
    static class DefaultScene extends Scene {

        public DefaultScene(Identifier renderGraph, Routine... routines) {
            super(renderGraph, List.of(routines));
        }

        @Override
        public void init() {
            GameClient client = (GameClient) ClientBase.getInstance();
            Manager manager = client.getManager();

            client.getTextureCache().generateAtlas(GameTextures.DEFAULT_SCENE_ATLAS);
            setMeshCache(new MeshCache(client.getModelRegistry(), client.getMeshRegistry(), client.getTextureCache()));

            manager.createEntityFromTemplate(GameEntities.SMILE_SQUARE_ENTITY);
            manager.createEntityFromTemplate(GameEntities.SAD_SQUARE_ENTITY);
            playerEntity = manager.createEntityFromTemplate(GameEntities.PLAYER_ENTITY);
        }

        @Override
        public void cleanup() {
            //Things to do with scene cleanup
        }
    }
```