package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.function.ToDoubleFunction;

public class AnimationControllerFunctionRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "animation_controller_function_registration");

    public interface FunctionRegistrar {
        void register(String name, int args, ToDoubleFunction<double[]> function);
    }

    private final FunctionRegistrar registrar;

    public AnimationControllerFunctionRegistrationEvent(FunctionRegistrar registrar) {
        super(EVENT);
        this.registrar = registrar;
    }

    public void registerFunction(String name, int args, ToDoubleFunction<double[]> function) {
        registrar.register(name, args, function);
    }

}
