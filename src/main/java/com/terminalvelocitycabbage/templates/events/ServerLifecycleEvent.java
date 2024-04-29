package com.terminalvelocitycabbage.templates.events;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class ServerLifecycleEvent extends Event {

	public static final Identifier PRE_INIT = TerminalVelocityEngine.identifierOf("serverLifecycleEventPreInit");
	public static final Identifier INIT = TerminalVelocityEngine.identifierOf("serverLifecycleEventInit");
	public static final Identifier PRE_BIND = TerminalVelocityEngine.identifierOf("serverLifecycleEventPreBind");
	public static final Identifier STARTED = TerminalVelocityEngine.identifierOf("serverLifecycleEventStart");
	public static final Identifier STOPPING = TerminalVelocityEngine.identifierOf("serverLifecycleEventStopping");
	public static final Identifier STOPPED = TerminalVelocityEngine.identifierOf("serverLifecycleEventStopped");

	Server server;

	public ServerLifecycleEvent(Identifier name, Server server) {
		super(name);
		this.server = server;
	}

	public Server getServer() {
		return server;
	}
}
