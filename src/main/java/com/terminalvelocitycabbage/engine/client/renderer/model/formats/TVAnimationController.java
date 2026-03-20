package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import java.util.Map;

public record TVAnimationController(
        Map<String, TVAnimationControllerVariable> variables,
        Map<String, TVAnimationControllerAnimation> animations
) {

    public record TVAnimationControllerVariable(
            String name,
            String type //Eventually need to map to a class type
    ) { }
    
    public record TVAnimationControllerAnimation(
            String name, //The name of the animation that this controller is controlling
            String when, //An expression that must evaluate to true or false to determine if this controller should apply any modification to the influence
            String influence //An expression that must evaluate to a float that controls the multiplier of the influence of the animation
    ) { }

}
