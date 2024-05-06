package com.terminalvelocitycabbage.engine.client;

import com.github.simplenet.Client;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.InputMapper;
import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.client.renderer.graph.RenderGraph;
import com.terminalvelocitycabbage.engine.client.window.WindowManager;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.mod.ModLoader;
import com.terminalvelocitycabbage.engine.networking.*;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler;
import com.terminalvelocitycabbage.engine.util.TickManager;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ClientBase extends Entrypoint implements NetworkedSide {

    //A singleton to represent the client for this program
    private static ClientBase instance;

    //Game loop stuff
    private WindowManager windowManager;
    private Registry<Pair<Class<? extends RendererBase>, RenderGraph>> rendererRegistry;
    private TickManager tickManager;
    private TickManager inputTickManager;
    private Manager manager;
    private Scheduler scheduler;

    //Networking stuff
    private Client client;
    private PacketRegistry packetRegistry;

    //Scope Stuff
    private EventDispatcher eventDispatcher;
    private Registry<Mod> modRegistry;

    //Resources Stuff
    private GameFileSystem fileSystem;

    //Input stuff
    private InputHandler inputHandler;
    private InputMapper inputMapper;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        inputTickManager = new TickManager(200); //TODO verify if 200hz input polling is good
        manager = new Manager();
        scheduler = new Scheduler();
        eventDispatcher = new EventDispatcher();
        modRegistry = new Registry<>(null);
        fileSystem = new GameFileSystem();
        windowManager = new WindowManager();
        rendererRegistry = new Registry<>();
        packetRegistry = new PacketRegistry();
        inputHandler = new InputHandler();
        inputMapper = new InputMapper();
        client = new Client();
    }

    /**
     * Gets the singleton instance of this client
     * @return this client instance
     */
    public static ClientBase getInstance() {
        return instance;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Starts this client program
     */
    public void start() {
        ModLoader.loadAndRegisterMods(Side.CLIENT, modRegistry);
        getInstance().init();
        getInstance().run();
        getInstance().destroy();
    }

    @Override
    public void init() {
        preInit();
        client.onConnect(this::onConnect);
        client.preDisconnect(this::onPreDisconnect);
        client.postDisconnect(this::onDisconnected);
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().preInit());
        windowManager.init();
    }

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
        while (!windowManager.loop()) {
            update();
        }
    }

    /**
     * The code to be executed every logic frame. NOT every renderer frame, that is handled by each window.
     */
    public void update() {
        inputTickManager.update();
        while (inputTickManager.hasTick()) {
            inputHandler.update(getWindowManager().getFocusedWindow(), getWindowManager().getMousedOverWindow());
        }
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

    public Registry<Pair<Class<? extends RendererBase>, RenderGraph>> getRendererRegistry() {
        return rendererRegistry;
    }

    @Override
    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public InputMapper getInputMapper() {
        return inputMapper;
    }
}
