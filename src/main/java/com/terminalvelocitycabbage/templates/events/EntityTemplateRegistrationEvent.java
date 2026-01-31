package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class EntityTemplateRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "EntityTemplateRegistrationEvent");

    private final Manager manager;

    public EntityTemplateRegistrationEvent(Manager manager) {
        super(EVENT);
        this.manager = manager;
    }

    public Identifier createEntityTemplate(String namespace, String entityName, Manager.EntityTemplateCreationCallback callback) {
        return manager.createEntityTemplate(new Identifier(namespace, "entity_template", entityName), callback).getIdentifier();
    }
}
