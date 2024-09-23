package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Entity;
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

    /**
     * creates a new entity and adds it to the active entities list for modification later
     *
     * @return the newly created entity
     */
    public Entity createEntity() {
        return manager.createEntity();
    }

    /**
     * creates a new entity and adds it to the active entities list for modification later
     *
     * @return the newly created entity
     */
    public Entity createEntity(Entity template) {
        return manager.createEntity(template);
    }
}
