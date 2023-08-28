package com.terminalvelocitycabbage.engine.client;

import com.github.simplenet.Client;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.client.window.WindowManager;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.mod.ModManager;
import com.terminalvelocitycabbage.engine.networking.*;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.util.TickManager;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ClientBase extends Entrypoint implements NetworkedSide {

    //A singleton to represent the client for this program
    private static ClientBase instance;

    //Game loop stuff
    private WindowManager windowManager;
    private Registry<RendererBase> rendererRegistry;
    private TickManager tickManager;

    //Networking stuff
    private Client client;
    private PacketRegistry packetRegistry;

    //Scope Stuff
    private EventDispatcher eventDispatcher;
    private ModManager modManager;
    private Registry<Mod> modRegistry;

    //Resources Stuff
    private GameFileSystem fileSystem;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        eventDispatcher = new EventDispatcher();
        eventDispatcher.addPublisher(getNamespace(), this);
        modManager = new ModManager();
        modRegistry = new Registry<>(null);
        fileSystem = new GameFileSystem();
        windowManager = new WindowManager();
        rendererRegistry = new Registry<>();
        packetRegistry = new PacketRegistry();
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
        modManager.loadAndRegisterMods(Side.CLIENT);
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
        getModRegistry().getRegistryContents().values().forEach(mod -> mod.entrypoint().preInit());
        windowManager.init();
    }

    public void modInit() {
        getModRegistry().getRegistryContents().values().forEach(mod -> mod.entrypoint().init());
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
        windowManager.loop();
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

    @Override
    public void destroy() {
        windowManager.destroy();
        getModRegistry().getRegistryContents().values().forEach(mod -> mod.entrypoint().destroy());
    }

    public abstract void keyCallback(long window, int key, int scancode, int action, int mods);

    public Registry<Mod> getModRegistry() {
        return modRegistry;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }

    public Registry<RendererBase> getRendererRegistry() {
        return rendererRegistry;
    }

    @Override
    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }
}
