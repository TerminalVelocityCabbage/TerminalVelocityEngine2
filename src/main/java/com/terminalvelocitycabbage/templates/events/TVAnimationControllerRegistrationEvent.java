package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimationController;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class TVAnimationControllerRegistrationEvent extends RegistryEvent<TVAnimationController> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "tv_animation_controller_registration");

    public TVAnimationControllerRegistrationEvent(Registry<TVAnimationController> registry) {
        super(EVENT, registry);
    }

    public Identifier registerTVAnimationController(String namespace, String modelName, String controllerName) {
        return registerTVAnimationController(namespace, ResourceCategory.ANIMATION_CONTROLLER.identifierOf(namespace, modelName + "/" + controllerName));
    }

    public Identifier registerTVAnimationController(String namespace, Identifier controllerResource) {
        TVAnimationController controller = TVAnimationController.of(controllerResource);
        return register(new Identifier(namespace, "tv_animation_controller", controllerResource.name()), controller).getIdentifier();
    }
}
