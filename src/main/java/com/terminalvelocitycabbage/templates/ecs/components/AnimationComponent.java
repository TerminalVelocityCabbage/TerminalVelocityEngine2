package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class AnimationComponent implements Component {

    private Identifier animation;
    private float currentTime;

    @Override
    public void parseComponentField(String field, String value) {
        switch (field) {
            case "animation" -> {
                this.animation = Identifier.fromString(value, "animation");
            }
        }
    }

    @Override
    public void setDefaults() {
        this.animation = null;
        this.currentTime = 0;
    }

    public Identifier getAnimation() {
        return animation;
    }

    public void setAnimation(Identifier animation) {
        this.animation = animation;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    public void update(float delta) {
        this.currentTime += delta;
    }
}
