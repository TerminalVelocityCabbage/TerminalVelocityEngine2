package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import redempt.crunch.CompiledExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TVAnimationController(
        Map<String, TVAnimationControllerVariable> variables,
        Map<String, TVAnimationControllerAnimation> animations
) {

    public static TVAnimationController of(Identifier controllerResource, AnimationControllerManager animationControllerManager) {
        Resource resource = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.ANIMATION_CONTROLLER, controllerResource);
        if (resource == null) {
            Log.crash("Could not find animation controller resource: " + controllerResource);
            return null;
        }
        Config config = TomlFormat.instance().createParser().parse(resource.asString());
        return ofConfig(config, animationControllerManager);
    }

    public static TVAnimationController ofConfig(Config config, AnimationControllerManager animationControllerManager) {

        //Load variables
        Map<String, TVAnimationControllerVariable> variables = new HashMap<>();
        Config variablesConfig = config.get("variables");
        if (variablesConfig != null) {
            for (Config.Entry entry : variablesConfig.entrySet()) {
                TVAnimationControllerVariable variable = TVAnimationControllerVariable.of(entry);
                variables.put(variable.name(), variable);
            }
        }

        //Load animations
        Map<String, TVAnimationControllerAnimation> animations = new HashMap<>();
        List<Config> animationsList = config.get("animations");
        if (animationsList != null) {
            for (Config animConfig : animationsList) {
                TVAnimationControllerAnimation animation = TVAnimationControllerAnimation.of(animConfig, animationControllerManager);
                animations.put(animation.animation(), animation);
            }
        }

        return new TVAnimationController(variables, animations);
    }

    public record TVAnimationControllerVariable(
            String name,
            String type //Eventually need to map to a class type
    ) {
        public static TVAnimationControllerVariable of(Config.Entry entry) {
            return new TVAnimationControllerVariable(entry.getKey(), entry.getValue());
        }
    }

    public record TVAnimationControllerAnimation(
            String animation, //The identifier of the animation that this controller is controlling
            Optional<CompiledExpression> influence, //An expression that must evaluate to a float that controls the multiplier of the influence of the animation
            Optional<String> trigger, //For non looping animations, a string ID that can be used to trigger the animation to play once
            Optional<String> postAction, // For triggers: reset, hold
            Optional<Map<String, CompiledExpression>> layers, //Optional map of layer names to influence expressions to control the influence of a specific layer based on an expression
            Optional<Integer> priority, //An optional integer that determines the priority of this animation relative to other animations. Default: 1
            Optional<String> blend, //An optional string that determines how this animation should blend with other animations. Particularly useful for animations with conflicting priorities. (override, additive) Default: additive
            Optional<String> ease, //How to ease the animation in and out. (linear, step, sin, quadratic, cubic, quartic, quintic, exponential, circular, back, elastic, bounce, catmulrom) Default: linear
            Optional<Float> fadeIn, //An optional float that determines how long in seconds this animation should take to fade in.
            Optional<Float> fadeOut, //An optional float that determines how long in seconds this animation should take to fade out. A fade out is triggered when the animation is finished playing or when it is interrupted.
            Optional<CompiledExpression> speed, //An optional expression that determines the speed of this animation. Default: 1.0
            Optional<CompiledExpression> progress //An optional expression that determines the progress of this animation as a float between 0 and 1. If this is defined, the animation's time is set to `progress * duration`.
    ) {
        public static TVAnimationControllerAnimation of(Config animConfig, AnimationControllerManager animationControllerManager) {
            String animIdentifier = animConfig.get("animation");

            Object triggerObj = animConfig.get("trigger");
            Optional<String> trigger = Optional.empty();
            Optional<String> postAction = Optional.empty();
            if (triggerObj instanceof List<?> list && !list.isEmpty()) {
                trigger = Optional.of((String) list.get(0));
                if (list.size() >= 2) {
                    postAction = Optional.of((String) list.get(1));
                }
            } else if (triggerObj instanceof String s) {
                trigger = Optional.of(s);
            }

            Optional<String> influence = Optional.ofNullable(animConfig.get("influence"));
            Optional<Map<String, String>> layers = Optional.ofNullable(animConfig.get("layers"));
            Optional<String> speed = Optional.ofNullable(animConfig.get("speed"));
            Optional<String> progress = Optional.ofNullable(animConfig.get("progress"));

            return new TVAnimationControllerAnimation(
                    animIdentifier,
                    influence.map(animationControllerManager::compileExpression),
                    trigger,
                    postAction,
                    layers.map(l -> {
                        Map<String, CompiledExpression> compiled = new HashMap<>();
                        l.forEach((k, v) -> compiled.put(k, animationControllerManager.compileExpression(v)));
                        return compiled;
                    }),
                    Optional.ofNullable(animConfig.<Number>get("priority")).map(Number::intValue),
                    Optional.ofNullable(animConfig.get("blend")),
                    Optional.ofNullable(animConfig.get("ease")),
                    Optional.ofNullable(animConfig.<Number>get("fade_in")).map(Number::floatValue),
                    Optional.ofNullable(animConfig.<Number>get("fade_out")).map(Number::floatValue),
                    speed.map(animationControllerManager::compileExpression),
                    progress.map(animationControllerManager::compileExpression)
            );
        }
    }

}
