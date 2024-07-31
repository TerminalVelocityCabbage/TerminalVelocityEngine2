package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.animation.AnimationController;
import com.terminalvelocitycabbage.engine.ecs.Component;

public class ModelAnimationControllerComponent implements Component {

    AnimationController animationController;

    @Override
    public void setDefaults() {
        animationController = null;
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public void setAnimationController(AnimationController animationController) {
        this.animationController = animationController;
    }
}
