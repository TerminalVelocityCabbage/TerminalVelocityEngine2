package com.terminalvelocitycabbage.engine.networking;

import com.github.simplenet.Client;
import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.server.ServerBase;

public class SyncPacketRegistryPacket extends SerializablePacket {

    private PacketRegistry packetRegistry;

    @Override
    public void interpretReceivedByServer(Server server, Client client) {
        packetRegistry = ServerBase.getInstance().getPacketRegistry();
        pack(ServerBase.getInstance(), SyncPacketRegistryPacket.class).queueAndFlush(client);
    }

    @Override
    public void interpretReceivedByClient(Client clientSender) {
        ClientBase.getInstance().getPacketRegistry().sync(packetRegistry);
    }
}
