package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.TargetProperties;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class RenderGraphStageExecutionEvent extends Event {

    boolean enabled;
    TargetProperties properties;
    long deltaTime;

    public RenderGraphStageExecutionEvent(Identifier name, TargetProperties properties, long deltaTime, boolean paused) {
        super(name);
        this.properties = properties;
        this.deltaTime = deltaTime;
        this.enabled = paused;
    }

    public static Identifier pre(Identifier graphNodeIdentifier) {
        return TerminalVelocityEngine.identifierOf("event", "render_graph_stage_execution_pre-[" + graphNodeIdentifier.toString() + "]");
    }

    public static Identifier post(Identifier graphNodeIdentifier) {
        return TerminalVelocityEngine.identifierOf("event", "render_graph_stage_execution_post-[" + graphNodeIdentifier.toString() + "]");
    }

    public TargetProperties getProperties() {
        return properties;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
