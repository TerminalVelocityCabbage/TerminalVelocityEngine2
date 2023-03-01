package com.terminalvelocitycabbage.templates.events;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class ServerLifecycleEvent extends Event {

	public static final String PRE_INIT = "serverLifecycleEventPreInit";
	public static final String INIT = "serverLifecycleEventInit";
	public static final String PRE_BIND = "serverLifecycleEventPreBind";
	public static final String STARTED = "serverLifecycleEventStart";
	public static final String STOPPING = "serverLifecycleEventStopping";
	public static final String STOPPED = "serverLifecycleEventStopped";

	Server server;

	public ServerLifecycleEvent(Identifier name, Server server) {
		super(name);
		this.server = server;
	}

	public Server getServer() {
		return server;
	}
}
