package com.terminalvelocitycabbage.engine.client;

import com.github.simplenet.Client;
import com.terminalvelocitycabbage.engine.MainEntrypoint;
import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.window.InputCallbackListener;
import com.terminalvelocitycabbage.engine.client.window.WindowManager;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.mod.ModLoader;
import com.terminalvelocitycabbage.engine.networking.*;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler;
import com.terminalvelocitycabbage.engine.util.MutableInstant;
import com.terminalvelocitycabbage.engine.util.TickManager;
import com.terminalvelocitycabbage.templates.events.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public abstract class ClientBase extends MainEntrypoint implements NetworkedSide {

    //A singleton to represent the client for this program
    private static ClientBase instance;

    //Game loop stuff
    private final WindowManager windowManager;
    private final Registry<RenderGraph> renderGraphRegistry;
    private final Registry<Routine> routineRegistry;
    private final TickManager tickManager;
    private final TickManager inputTickManager;
    private final Manager manager;
    private final Scheduler scheduler;
    private long deltaTime; //Tick delta time not render time
    private final MutableInstant tickClock;

    //Networking stuff
    private final Client client;
    private final PacketRegistry packetRegistry;

    //Scope Stuff
    private final Registry<Mod> modRegistry;

    //Resources Stuff
    private final GameFileSystem fileSystem;

    //Input stuff
    private final InputHandler inputHandler;
    private final InputCallbackListener inputCallbackListener;

    //Scene Stuff
    private final Registry<Scene> sceneRegistry;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        inputTickManager = new TickManager(200); //TODO verify if 200hz input polling is good
        manager = new Manager();
        scheduler = new Scheduler();
        tickClock = MutableInstant.ofNow();
        modRegistry = new Registry<>();
        fileSystem = new GameFileSystem();
        windowManager = new WindowManager();
        renderGraphRegistry = new Registry<>();
        routineRegistry = new Registry<>();
        packetRegistry = new PacketRegistry();
        inputHandler = new InputHandler();
        inputCallbackListener = new InputCallbackListener();
        sceneRegistry = new Registry<>();
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
        getInstance().init();
        getInstance().run();
        getInstance().destroy();
    }

    @Override
    public void init() {
        client.onConnect(this::onConnect);
        client.preDisconnect(this::onPreDisconnect);
        client.postDisconnect(this::onDisconnected);
        eventDispatcher.dispatchEvent(new ResourceCategoryRegistrationEvent(ResourceCategoryRegistrationEvent.EVENT, fileSystem.getResourceCategoryRegistry()));
        eventDispatcher.dispatchEvent(new ResourceSourceRegistrationEvent(ResourceSourceRegistrationEvent.EVENT, fileSystem.getSourceRegistry()));
        Log.info(Arrays.toString(fileSystem.getResourceCategoryRegistry().getRegistryContents().values().toArray()));
        for (ResourceCategory category : fileSystem.getResourceCategoryRegistry().getRegistryContents().values()) {
            eventDispatcher.dispatchEvent(new ResourceRegistrationEvent(fileSystem, category));
        }
        fileSystem.init();
        eventDispatcher.dispatchEvent(new InputHandlerRegistrationEvent(inputHandler));
        eventDispatcher.dispatchEvent(new EntityComponentRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new EntitySystemRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new EntityTemplateRegistrationEvent(manager));
        eventDispatcher.dispatchEvent(new RoutineRegistrationEvent(routineRegistry));
        eventDispatcher.dispatchEvent(new RendererRegistrationEvent(renderGraphRegistry));
        eventDispatcher.dispatchEvent(new SceneRegistrationEvent(sceneRegistry));
        windowManager.init();
    }

    //TODO this automatically
    public void modInit() {
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().init());
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
        deltaTime = 1;
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
        deltaTime = tickClock.getDeltaTime();
        tickClock.now();
        //Update the input handlers for use in game logic
        inputTickManager.update();
        while (inputTickManager.hasTick()) {
            inputHandler.update(getWindowManager().getFocusedWindow(), getWindowManager().getMousedOverWindow(), deltaTime);
            inputCallbackListener.reset();
        }
        //update the tick manager for game logic
        tickManager.update();
        while (tickManager.hasTick()) {
            tick();
        }
    }

    /**
     * The code to be executed every tick
     * This is mainly used for networking tasks, most things for clients should happen every frame
     */
    public void tick() {
        getScheduler().tick();
    }

    @Override
    public void destroy() {
        windowManager.destroy();
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().destroy());
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }

    public Manager getManager() {
        return manager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Registry<RenderGraph> getRenderGraphRegistry() {
        return renderGraphRegistry;
    }

    @Override
    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
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

    public Registry<Scene> getSceneRegistry() {
        return sceneRegistry;
    }
}
