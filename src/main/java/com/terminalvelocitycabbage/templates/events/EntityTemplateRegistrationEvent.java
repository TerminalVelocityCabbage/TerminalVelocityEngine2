package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class EntityTemplateRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("EntityTemplateRegistrationEvent");

    private Manager manager;

    public EntityTemplateRegistrationEvent(Manager manager) {
        super(EVENT);
        this.manager = manager;
    }

    public Identifier createEntityTemplate(Identifier templateIdentifier, Manager.EntityTemplateCreationCallback callback) {
        return manager.createEntityTemplate(templateIdentifier, callback).getIdentifier();
    }
}
