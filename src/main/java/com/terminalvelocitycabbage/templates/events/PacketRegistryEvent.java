package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.networking.PacketRegistry;
import com.terminalvelocitycabbage.engine.networking.SerializablePacket;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class PacketRegistryEvent extends Event {

    PacketRegistry packetRegistry;

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "packet_registry");

    public PacketRegistryEvent(PacketRegistry registry) {
        super(EVENT);
        this.packetRegistry = registry;
    }

    public void registerPacket(Class<? extends SerializablePacket> packetType) {
        packetRegistry.registerPacket(packetType);
    }
}
