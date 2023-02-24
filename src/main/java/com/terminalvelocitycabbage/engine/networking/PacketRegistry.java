package com.terminalvelocitycabbage.engine.networking;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.server.ServerBase;

import java.io.Serializable;
import java.util.HashMap;

public class PacketRegistry implements Serializable {

    //A map of all registered packet classes and their associated opcode
    private final HashMap<Class<? extends SerializablePacket>, Integer> packetTypes;
    //A boolean to know whether this packet registry can be added to or not
    boolean locked;

    public PacketRegistry() {
        this.packetTypes = new HashMap<>();

        //by default a registry is not locked, but there comes a point where no more packets should be allowed to be
        //registered to keep the server and client's synced
        this.locked = false;

        //Every packet registry's first packet is to be that which syncs this registry
        packetTypes.put(SyncPacketRegistryPacket.class, 0);
    }

    /**
     * registers a packet of the type specified so that it's opcode can be synced with a client later
     * @param packet the class of the packet which you are registering an opcode for
     */
    public void registerPacket(Class<? extends SerializablePacket> packet) {
        if (packetTypes.containsKey(packet)) {
            Log.warn("Tried to register packet of same type " + packet + " twice, the second addition has been ignored.");
            return;
        }
        packetTypes.put(packet, packetTypes.size());
        Log.info("Registered packet: " + packet.getName());
    }

    /**
     * Gets the opcode for the packet requested
     * @param packetClass the class for the packet which you wish to retrieve
     * @return an integer representing the opcode for this packet
     */
    public int getOpcodeForPacket(Class<? extends SerializablePacket> packetClass) {
        if (!packetTypes.containsKey(packetClass)) Log.crash("Could not get opcode for packet " + packetClass.getName(), new RuntimeException("No packet registered for class " + packetClass.getName()));
        return packetTypes.get(packetClass);
    }

    /**
     * Syncs this packet registry to the one provided, to be used when a client joins a server and needs its packet opcodes synced
     * @param packetRegistry the packet registry received by the server to set this client's packet registry to.
     */
    public void sync(PacketRegistry packetRegistry) {
        if (ServerBase.getInstance() != null) Log.crash("Not allowed to call sync on server packet registry", new RuntimeException("Illegal call to PacketRegistry#sync() on Server packet registry"));
        if (packetTypes.size() > 1) Log.crash("Can't sync an already populated packet registry", new RuntimeException("Tried to call sync on packet registry with packets already synced"));
        packetRegistry.packetTypes.forEach((aClass, integer) -> {
            if (!aClass.equals(SyncPacketRegistryPacket.class)) registerPacket(aClass);
        });
        lock();
    }

    /**
     * Locks this packet registry
     * Game developers should not call this method, it is for engine use only and will be automatically locked for you.
     */
    public void lock() {
        this.locked = true;
    }

}
