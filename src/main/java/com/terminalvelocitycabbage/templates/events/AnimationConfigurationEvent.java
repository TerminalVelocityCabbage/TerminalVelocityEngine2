package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.AnimationControllerManager;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimation;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimationController;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.function.Function;

public class AnimationConfigurationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "animation_configuration");

    private final Registry<TVAnimation> tvAnimationRegistry;
    private final Registry<TVAnimationController> tvAnimationControllerRegistry;
    private final AnimationControllerManager animationControllerManager;

    public AnimationConfigurationEvent(Registry<TVAnimation> tvAnimationRegistry, Registry<TVAnimationController> tvAnimationControllerRegistry, AnimationControllerManager animationControllerManager) {
        super(EVENT);
        this.tvAnimationRegistry = tvAnimationRegistry;
        this.tvAnimationControllerRegistry = tvAnimationControllerRegistry;
        this.animationControllerManager = animationControllerManager;
    }

    private Identifier registerTVAnimation(String namespace, String modelName, String animationName) {
        return registerTVAnimation(namespace, ResourceCategory.ANIMATION.identifierOf(namespace, modelName + "/" + animationName));
    }

    private Identifier registerTVAnimation(String namespace, Identifier animationResource) {
        TVAnimation animation = TVAnimation.of(animationResource);
        return tvAnimationRegistry.register(new Identifier(namespace, "animation", animationResource.name()), animation);
    }

    public Identifier registerTVAnimationController(String namespace, String modelName, String controllerName) {
        return registerTVAnimationController(namespace, ResourceCategory.ANIMATION_CONTROLLER.identifierOf(namespace, modelName + "/" + controllerName));
    }

    public Identifier registerTVAnimationController(String namespace, Identifier controllerResource) {
        TVAnimationController controller = TVAnimationController.of(controllerResource, animationControllerManager);
        // Register referenced animations
        for (TVAnimationController.TVAnimationControllerAnimation anim : controller.animations().values()) {
            Identifier animId = Identifier.fromString(anim.animation(), ResourceCategory.ANIMATION.name());
            registerTVAnimation(namespace, animId);
        }
        return tvAnimationControllerRegistry.register(new Identifier(namespace, "animation_controller", controllerResource.name()), controller);
    }

    public <T> void registerVariable(String name, Class<T> type, Function<Entity, T> provider) {
        animationControllerManager.registerVariable(name, type, provider);
    }

}
