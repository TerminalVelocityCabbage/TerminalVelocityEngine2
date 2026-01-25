package com.terminalvelocitycabbage.engine.server;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.MainEntrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.mod.ModLoader;
import com.terminalvelocitycabbage.engine.networking.NetworkedSide;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.templates.events.PacketRegistryEvent;
import com.terminalvelocitycabbage.templates.events.ServerLifecycleEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ServerBase extends MainEntrypoint implements NetworkedSide {

    //A singleton to represent the server for this program
    private static ServerBase instance;

    //A boolean that controls whether this server's loop should stop
    private boolean shouldStop;

    //Networking stuff
    private Server server;
    private String address;
    private int port;

    public ServerBase(String namespace, int ticksPerSecond) {
        super(namespace, ticksPerSecond);
        instance = this;
    }

    /**
     * Starts this server program
     */
    public void start() {
        ModLoader.loadAndRegisterMods(this, Side.SERVER, modRegistry);
        eventDispatcher.dispatchEvent(new PacketRegistryEvent(getPacketRegistry()));
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.PRE_INIT, server));
        getInstance().init();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.INIT, server));
        getInstance().run();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.STOPPING, server));
        getInstance().destroy();
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.STOPPED, server));
    }

    @Override
    public void init() {

        tickClock.now();

        server = new Server();

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

        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().init());
    }

    /**
     * initializes the game loop
     */
    private void run() {

        //There should be no more packets registered to this packet registry after the game has been initialized
        getPacketRegistry().lock();

        //Establish connection
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.PRE_BIND, server));
        bind();

        //Dispatch started event
        eventDispatcher.dispatchEvent(new ServerLifecycleEvent(ServerLifecycleEvent.STARTED, server));

        //As long as the server should run we run it
        while (!shouldStop) {
            deltaTime = tickClock.getDeltaTime();
            runtime += deltaTime;
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
        modRegistry.getRegistryContents().values().forEach(mod -> mod.getEntrypoint().destroy());
    }

    /**
     * Gets the singleton instance of this server
     * @return This server instance
     */
    public static ServerBase getInstance() {
        return instance;
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
}
