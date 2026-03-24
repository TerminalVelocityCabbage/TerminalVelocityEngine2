package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import java.util.Map;
import java.util.Optional;

public record TVAnimationController(
        Map<String, TVAnimationControllerVariable> variables,
        Map<String, TVAnimationControllerAnimation> animations
) {

    public record TVAnimationControllerVariable(
            String name,
            String type //Eventually need to map to a class type
    ) { }
    
    public record TVAnimationControllerAnimation(
            String animation, //The identifier of the animation that this controller is controlling
            Optional<String> influence, //An expression that must evaluate to a float that controls the multiplier of the influence of the animation
            Optional<String> trigger, //For non looping animations, a string ID that can be used to trigger the animation to play once
            Optional<Map<String, String>> layers, //Optional map of layer names to influence expressions to control the influence of a specific layer based on an expression
            Optional<Integer> priority, //An optional integer that determines the priority of this animation relative to other animations. Default: 1
            Optional<String> blend, //An optional expression that determines how this animation should blend with other animations. Particularly useful for animations with conflicting priorities. (override, additive) Default: additive
            Optional<String> ease, //How to ease the animation in and out. (linear, step, sin, quadratic, cubic, quartic, quintic, exponential, circular, back, elastic, bounce, catmulrom) Default: linear
            Optional<Float> fadeIn, //An optional float that determines how long in seconds this animation should take to fade in.
            Optional<Float> fadeOut, //An optional float that determines how long in seconds this animation should take to fade out. A fade out is triggered when the animation is finished playing or when it is interrupted.
            Optional<Float> speed, //An optional expression that determines the speed of this animation. Default: 1.0
            Optional<Float> progress //An optional expression that determines the progress of this animation as a float between 0 and 1. If this is defined, the animation's time is set to `progress * duration`.
    ) { }

}
