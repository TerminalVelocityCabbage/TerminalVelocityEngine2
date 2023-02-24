package com.terminalvelocitycabbage.engine.networking;

public interface NetworkedSide {

    PacketRegistry packetRegistry = new PacketRegistry();

    default PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }
}
