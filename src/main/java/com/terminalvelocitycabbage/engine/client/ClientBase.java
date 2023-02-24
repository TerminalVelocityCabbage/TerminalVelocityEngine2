package com.terminalvelocitycabbage.engine.client;

import com.github.simplenet.Client;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.networking.NetworkedSide;
import com.terminalvelocitycabbage.engine.networking.PacketRegistry;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.networking.SyncPacketRegistryPacket;
import com.terminalvelocitycabbage.engine.util.TickManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ClientBase extends Entrypoint implements NetworkedSide {

    //A singleton to represent the client for this program
    private static ClientBase instance;

    //Game loop stuff
    private Window window;
    private RendererBase renderer;
    private TickManager tickManager;

    //Networking stuff
    private Client client;
    private PacketRegistry packetRegistry;

    public ClientBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        packetRegistry = new PacketRegistry();
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
        getInstance().init();
        getInstance().run();
        getInstance().destroy();
    }

    @Override
    public void init() {
        window = new Window();
        client = new Client();
        client.onConnect(this::onConnect);
        client.preDisconnect(this::onPreDisconnect);
        client.postDisconnect(this::onDisconnected);
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
        window.run();
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

    public abstract void keyCallback(long window, int key, int scancode, int action, int mods);
}
