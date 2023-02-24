package com.terminalvelocitycabbage.engine.networking;

import com.github.simplenet.Client;
import com.github.simplenet.Server;
import com.github.simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class SerializablePacket implements Serializable {

    public abstract void interpretReceivedByClient(Client client);

    public abstract void interpretReceivedByServer(Server server, Client clientSender);

    public Packet pack(NetworkedSide entrypointInstance, Class<? extends SerializablePacket> aClass) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(aClass.cast(this));
            return Packet.builder()
                    .putInt(entrypointInstance.getPacketRegistry().getOpcodeForPacket(aClass))
                    .putInt(bos.size())
                    .putBytes(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
