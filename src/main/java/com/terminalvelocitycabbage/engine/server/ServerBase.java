package com.terminalvelocitycabbage.engine.server;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.mod.ModLoader;
import com.terminalvelocitycabbage.engine.networking.NetworkedSide;
import com.terminalvelocitycabbage.engine.networking.PacketRegistry;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler;
import com.terminalvelocitycabbage.engine.util.TickManager;
import com.terminalvelocitycabbage.templates.events.ServerLifecycleEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ServerBase extends Entrypoint implements NetworkedSide {

    //A singleton to represent the server for this program
    private static ServerBase instance;

    //A boolean that controls whether this server's loop should stop
    private boolean shouldStop;

    //Game loop stuff
    private TickManager tickManager;

    //Networking stuff
    private Server server;
    private String address;
    private int port;
    private PacketRegistry packetRegistry;

    //Scope Stuff
    private EventDispatcher eventDispatcher;
    private Registry<Mod> modRegistry;
    private Manager manager;
    private Scheduler scheduler;

    //Resources Stuff
    private GameFileSystem fileSystem;

    public ServerBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        eventDispatcher = new EventDispatcher();
        eventDispatcher.addPublisher(getNamespace(), this);
        modRegistry = new Registry<>(null);
        manager = new Manager();
        fileSystem = new GameFileSystem();
    }

    /**
     * Starts this server program
     */
    public void start() {
        ModLoader.loadAndRegisterMods(Side.SERVER);
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.PRE_INIT), server));
        getInstance().init();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.INIT), server));
        getInstance().run();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.STOPPING), server));
        getInstance().destroy();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.STOPPED), server));
    }

    @Override
    public void init() {
        preInit();

        server = new Server();
        packetRegistry = new PacketRegistry();

        server.onConnect(client -> {
            //Read packets and dispatch events based on opcode
            client.readIntAlways(opcode -> {
                client.readInt(bytesSize -> {
                    client.readBytes(bytesSize, bytes -> {
                        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
                            SerializablePacket received = (SerializablePacket) ois.readObject();
                            received.interpretReceivedByServer(server, client);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
            });
        });

        getModRegistry().getRegistryContents().values().forEach(mod -> mod.getEntrypoint().preInit());
    }

    public void modInit() {
        getModRegistry().getRegistryContents().values().forEach(mod -> mod.getEntrypoint().init());
    }

    /**
     * initializes the game loop
     */
    private void run() {

        //There should be no more packets registered to this packet registry after the game has been initialized
        packetRegistry.lock();

        //Establish connection
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.PRE_BIND), server));
        bind();

        //Dispatch started event
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(identifierOf(ServerLifecycleEvent.STARTED), server));

        //As long as the server should run we run it
        while (!shouldStop) {
            //Adds the current elapsed time to the tick manager
            tickManager.update();
            //Execute all remaining ticks
            while (tickManager.hasTick()) {
                tick();
            }
        }
    }

    public void bind() {
        //Bind this server to the network first
        server.bind(address, port);
        Log.info("Server bound to: " + address + ":" + port);
    }

    @Override
    public void destroy() {
        server.close();
        getModRegistry().getRegistryContents().values().forEach(mod -> mod.getEntrypoint().destroy());
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
    public void tick() {
        getScheduler().tick();
    }

    /**
     * Marks this server as ready to stop
     */
    public void stop() {
        shouldStop = true;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public Manager getManager() {
        return manager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Registry<Mod> getModRegistry() {
        return modRegistry;
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }
}
