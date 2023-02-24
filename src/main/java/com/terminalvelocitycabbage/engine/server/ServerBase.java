package com.terminalvelocitycabbage.engine.server;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.networking.NetworkedSide;
import com.terminalvelocitycabbage.engine.networking.PacketRegistry;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.util.TickManager;

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
    private static PacketRegistry packetRegistry;

    public ServerBase(String namespace, int ticksPerSecond) {
        super(namespace);
        instance = this;
        tickManager = new TickManager(ticksPerSecond);
        server = new Server();
    }

    /**
     * Starts this server program
     */
    public void start() {
        getInstance().init();
        getInstance().run();
        getInstance().destroy();
    }

    @Override
    public void init() {

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
    }

    /**
     * initializes the game loop
     */
    private void run() {

        //Establish connection
        bind();

        //There should be no more packets registered to this packet registry after the game has been initialized
        packetRegistry.lock();

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
    public abstract void tick();

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
}
