package com.terminalvelocitycabbage.engine.client;

import com.github.simplenet.Client;
import com.terminalvelocitycabbage.engine.MainEntrypoint;
import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.renderer.Font;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.client.sound.SoundDeviceManager;
import com.terminalvelocitycabbage.engine.client.sound.SoundManager;
import com.terminalvelocitycabbage.engine.client.ui.UIContext;
import com.terminalvelocitycabbage.engine.client.window.InputCallbackListener;
import com.terminalvelocitycabbage.engine.client.window.WindowManager;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.mod.ModLoader;
import com.terminalvelocitycabbage.engine.networking.NetworkedSide;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.networking.SyncPacketRegistryPacket;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.util.TickManager;
import com.terminalvelocitycabbage.templates.events.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ClientBase extends MainEntrypoint implements NetworkedSide {

    //A singleton to represent the client for this program
    private static ClientBase instance;

    //Game loop stuff
    private final WindowManager windowManager;
    private final Registry<RenderGraph> renderGraphRegistry;
    private final Registry<Font> fontRegistry;
    private final SoundManager soundManager;
    private final SoundDeviceManager soundDeviceManager;

    //Scene stuff
    protected final Registry<Mesh> meshRegistry;
    protected final Registry<Model> modelRegistry;
    protected TextureCache textureCache;

    //Networking stuff
    private final Client client;

    //Input stuff
    private final InputHandler inputHandler;
    private final InputCallbackListener inputCallbackListener;
    private final TickManager inputTickManager;

    //UI stuff
    private final UIContext uiContext;
    private long nvgContext;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace, ticksPerSecond);
        instance = this;
        inputTickManager = new TickManager(200); //TODO verify if 200hz input polling is good
        windowManager = new WindowManager();
        renderGraphRegistry = new Registry<>();
        fontRegistry = new Registry<>();
        soundManager = new SoundManager();
        soundDeviceManager = new SoundDeviceManager();
        inputHandler = new InputHandler();
        inputCallbackListener = new InputCallbackListener();
        uiContext = new UIContext();
        meshRegistry = new Registry<>();
        modelRegistry = new Registry<>();
        client = new Client();
    }

    /**
     * Gets the singleton instance of this client
     * @return this client instance
     */
    public static ClientBase getInstance() {
        return instance;
    }

    /**
     * Starts this client program
     */
    public void start() {
        ModLoader.loadAndRegisterMods(this, Side.CLIENT, modRegistry);
        init();
        run();
        destroy();
    }

    @Override
    public void init() {
        client.onConnect(this::onConnect);
        client.preDisconnect(this::onPreDisconnect);
        client.postDisconnect(this::onDisconnected);
        eventDispatcher.dispatchEvent(new ResourceCategoryRegistrationEvent(fileSystem.getResourceCategoryRegistry()));
        eventDispatcher.dispatchEvent(new ResourceSourceRegistrationEvent(fileSystem.getSourceRegistry(), getInstance()));
        for (ResourceCategory category : fileSystem.getResourceCategoryRegistry().getRegistryContents().values()) {
            eventDispatcher.dispatchEvent(new ResourceRegistrationEvent(fileSystem, category));
        }
        fileSystem.init();
        eventDispatcher.dispatchEvent(new GameStateRegistrationEvent(stateHandler));
        eventDispatcher.dispatchEvent(new InputHandlerRegistrationEvent(inputHandler));
        eventDispatcher.dispatchEvent(new EntityComponentRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new EntitySystemRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new RoutineRegistrationEvent(routineRegistry));
        var configureTexturesEvent = new ConfigureTexturesEvent(fileSystem);
        eventDispatcher.dispatchEvent(configureTexturesEvent);
        textureCache = new TextureCache(configureTexturesEvent.getTexturesToCompileToAtlas(), configureTexturesEvent.getSingleTextures());
        eventDispatcher.dispatchEvent(new RendererRegistrationEvent(renderGraphRegistry));
        eventDispatcher.dispatchEvent(new FontRegistrationEvent(fontRegistry));
        eventDispatcher.dispatchEvent(new SceneRegistrationEvent(sceneRegistry));
        eventDispatcher.dispatchEvent(new MeshRegistrationEvent(meshRegistry));
        eventDispatcher.dispatchEvent(new ModelConfigRegistrationEvent(modelRegistry));
        soundDeviceManager.init();
        eventDispatcher.dispatchEvent(new SoundRegistrationEvent(soundManager));
        eventDispatcher.dispatchEvent(new EntityTemplateRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new LocalizedTextKeyRegistrationEvent(localizer.getTranslationRegistry()));
        localizer.init();
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().init());
        windowManager.init();
    }

    public void connect(String address, int port) {
        client.connect(address, port);
    }

    public void disconnect() {
        client.close();
    }

    public void onConnect() {

        //create a packet decoder callback, this automatically decodes incoming SerializablePackets and executes them.
        client.readIntAlways(opcode -> {
            client.readInt(sizeOfPacketInBytes -> {
                client.readBytes(sizeOfPacketInBytes, packetInBytes -> {
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(packetInBytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
                        SerializablePacket packet = (SerializablePacket) ois.readObject();
                        packet.interpretReceivedByClient(client);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });

        //Send a packet to the server requesting the Packet Registry opcodes
        sendPacket(new SyncPacketRegistryPacket(), SyncPacketRegistryPacket.class);
    }

    public void sendPacket(SerializablePacket packet, Class<? extends SerializablePacket> packetClass) {
        packet.pack(instance, packetClass).queueAndFlush(client);
    }

    public void onPreDisconnect() {
        //...
    }

    public void onDisconnected() {
        //...
    }

    /**
     * initializes the game loop
     */
    private void run() {

        //Make sure the first frame has a somewhat valid deltatime
        tickClock.now();

        //Start the logic loop
        while (!windowManager.loop()) {
            update();
        }
    }

    /**
     * The code to be executed every logic frame. NOT every renderer frame, that is handled by each window.
     */
    public void update() {
        //Update the tick timer
        tickClock.now();
        //Update the input handlers for use in game logic
        inputTickManager.update();
        while (inputTickManager.hasTick()) {
            inputHandler.update(getWindowManager().getFocusedWindow(), getWindowManager().getMousedOverWindow(), getDeltaTime());
            inputCallbackListener.reset();
        }
        //update the tick manager for game logic
        tickManager.update();
        while (tickManager.hasTick()) {
            tick();
        }
        //update the scheduler
        scheduler.update();
    }

    @Override
    public void destroy() {
        windowManager.destroy();
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().destroy());
        soundManager.cleanup();
        soundDeviceManager.cleanup();
    }

    public Registry<RenderGraph> getRenderGraphRegistry() {
        return renderGraphRegistry;
    }

    public Registry<Font> getFontRegistry() {
        return fontRegistry;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public InputCallbackListener getInputCallbackListener() {
        return inputCallbackListener;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public UIContext getUIContext() {
        return uiContext;
    }

    public long getNvgContext() {
        return nvgContext;
    }

    public void setNvgContext(long nvgContext) {
        this.nvgContext = nvgContext;
    }

    public Registry<Mesh> getMeshRegistry() {
        return meshRegistry;
    }

    public Registry<Model> getModelRegistry() {
        return modelRegistry;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public long getRuntime() {
        return getWindowManager().runtime(getWindowManager().getFocusedWindow());
    }

    @Override
    public long getDeltaTime() {
        return getWindowManager().deltaTime(getWindowManager().getFocusedWindow());
    }
}
