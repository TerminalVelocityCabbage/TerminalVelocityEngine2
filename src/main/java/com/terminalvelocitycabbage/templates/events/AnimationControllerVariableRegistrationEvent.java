package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.function.Function;

public class AnimationControllerVariableRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "animation_controller_variable_registration");

    public interface VariableRegistrar {
        <T> void register(String name, Class<T> type, Function<Entity, T> provider);
    }

    private final VariableRegistrar registrar;

    public AnimationControllerVariableRegistrationEvent(VariableRegistrar registrar) {
        super(EVENT);
        this.registrar = registrar;
    }

    public <T> void registerVariable(String name, Class<T> type, Function<Entity, T> provider) {
        registrar.register(name, type, provider);
    }

}
