package com.terminalvelocitycabbage.templates.events;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class ServerLifecycleEvent extends Event {

	public static final Identifier PRE_INIT = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_pre_init");
	public static final Identifier INIT = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_init");
	public static final Identifier PRE_BIND = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_pre_bind");
	public static final Identifier STARTED = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_started");
	public static final Identifier STOPPING = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_stopping");
	public static final Identifier STOPPED = TerminalVelocityEngine.identifierOf("event", "server_lifecycle_stopped");

	Server server;

	public ServerLifecycleEvent(Identifier name, Server server) {
		super(name);
		this.server = server;
	}

	public Server getServer() {
		return server;
	}
}
