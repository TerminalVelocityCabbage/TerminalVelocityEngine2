package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimationEvaluator;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnimationControllerComponent implements Component {

    private Identifier controllerIdentifier;
    private final Map<String, AnimationState> animationStates = new HashMap<>();
    private final Set<String> activeTriggers = new HashSet<>();

    @Override
    public void parseComponentField(String field, String value) {
        if (field.equals("controller")) {
            this.controllerIdentifier = Identifier.fromString(value, "animation_controller");
        }
    }

    @Override
    public void setDefaults() {
        this.controllerIdentifier = null;
        this.animationStates.clear();
        this.activeTriggers.clear();
    }

    public Identifier getControllerIdentifier() {
        return controllerIdentifier;
    }

    public void setControllerIdentifier(Identifier controllerIdentifier) {
        this.controllerIdentifier = controllerIdentifier;
    }

    public void trigger(String triggerName) {
        activeTriggers.add(triggerName);
    }

    public Map<String, AnimationState> getAnimationStates() {
        return animationStates;
    }

    public Set<String> getActiveTriggers() {
        return activeTriggers;
    }

    public Matrix4f[] getBoneMatrices(Model model) {
        return TVAnimationEvaluator.evaluate(this, model.skeleton());
    }

    public static class AnimationState {
        private float currentTime;
        private float influence;
        private float speed = 1.0f;

        public float getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(float currentTime) {
            this.currentTime = currentTime;
        }

        public float getInfluence() {
            return influence;
        }

        public void setInfluence(float influence) {
            this.influence = influence;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }
    }
}
