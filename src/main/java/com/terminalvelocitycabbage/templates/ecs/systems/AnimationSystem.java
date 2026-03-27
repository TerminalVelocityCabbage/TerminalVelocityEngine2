package com.terminalvelocitycabbage.templates.ecs.systems;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.AnimationControllerManager;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimation;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimationController;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.templates.ecs.components.AnimationControllerComponent;

import java.util.HashMap;
import java.util.Map;

public class AnimationSystem extends System {

    @Override
    public void update(Manager manager, float deltaTime) {
        manager.getEntitiesWith(AnimationControllerComponent.class).forEach(entity -> {
            AnimationControllerComponent component = entity.getComponent(AnimationControllerComponent.class);
            if (component.getControllerIdentifier() == null) return;

            TVAnimationController controller = ClientBase.getInstance().getTvAnimationControllerRegistry().get(component.getControllerIdentifier());
            if (controller == null) return;

            AnimationControllerManager animManager = ClientBase.getInstance().getAnimationControllerManager();
            double[] variableValues = animManager.getVariableValues(entity, controller);

            // 1. Evaluate base target influence for each animation and find max override priority
            int maxOverridePriority = -1;
            Map<String, Float> baseTargets = new HashMap<>();

            for (TVAnimationController.TVAnimationControllerAnimation anim : controller.animations().values()) {
                AnimationControllerComponent.AnimationState state = component.getAnimationStates().get(anim.animation());

                float baseTarget;
                if (anim.influence().isPresent()) {
                    baseTarget = (float) anim.influence().get().evaluate(variableValues);
                } else if (anim.trigger().isPresent()) {
                    String triggerName = anim.trigger().get();
                    if (component.getActiveTriggers().contains(triggerName)) {
                        baseTarget = 1.0f;
                    } else if (state != null) {
                        baseTarget = state.getTargetInfluence();
                        if (anim.postAction().isPresent() && anim.postAction().get().equals("reset")) {
                            TVAnimation tvAnim = ClientBase.getInstance().getTvAnimationRegistry().get(Identifier.fromString(anim.animation(), "animation"));
                            if (tvAnim != null && !tvAnim.metadata().looping() && state.getCurrentTime() >= tvAnim.metadata().duration()) {
                                baseTarget = 0.0f;
                            }
                        }
                    } else {
                        baseTarget = 0.0f;
                    }
                } else {
                    baseTarget = 1.0f;
                }
                baseTargets.put(anim.animation(), baseTarget);

                // Check for override
                float currentInfluence = state == null ? 0 : state.getInfluence();
                if (baseTarget > 0 || currentInfluence > 0) {
                    if (anim.blend().isPresent() && anim.blend().get().equalsIgnoreCase("override")) { // override
                        int priority = anim.priority().orElse(1);
                        if (priority > maxOverridePriority) {
                            maxOverridePriority = priority;
                        }
                    }
                }
            }

            // 2. Process animations with override logic
            for (TVAnimationController.TVAnimationControllerAnimation anim : controller.animations().values()) {
                AnimationControllerComponent.AnimationState state = component.getAnimationStates().computeIfAbsent(anim.animation(), k -> new AnimationControllerComponent.AnimationState());
                float nextTarget = baseTargets.get(anim.animation());

                // Apply override logic
                int priority = anim.priority().orElse(1);
                if (priority < maxOverridePriority) {
                    nextTarget = 0.0f;
                }

                // Handle trigger reset
                if (anim.trigger().isPresent() && component.getActiveTriggers().contains(anim.trigger().get())) {
                    state.setCurrentTime(0);
                }

                // If target influence changed significantly, or we were at rest and target changed, start a new transition
                if (Math.abs(nextTarget - state.getLastEvaluatedTarget()) > 0.1f || (state.getInfluence() == state.getTargetInfluence() && Math.abs(nextTarget - state.getTargetInfluence()) > 0.0001f)) {
                    state.setStartInfluence(state.getInfluence());
                    state.setTargetInfluence(nextTarget);
                    state.setElapsedTransitionTime(0);
                } else {
                    state.setTargetInfluence(nextTarget);
                }
                state.setLastEvaluatedTarget(nextTarget);

                // Handle Fading
                float targetInfluence = state.getTargetInfluence();
                float currentInfluence = state.getInfluence();
                if (currentInfluence != targetInfluence) {
                    float duration = targetInfluence > state.getStartInfluence() ? anim.fadeIn().orElse(0.0f) : anim.fadeOut().orElse(0.0f);
                    if (duration <= 0) {
                        currentInfluence = targetInfluence;
                    } else {
                        state.setElapsedTransitionTime(state.getElapsedTransitionTime() + (deltaTime / 1000.0f));
                        float progress = Math.min(1.0f, state.getElapsedTransitionTime() / duration);

                        Easing.Direction direction = Easing.Direction.IN_OUT;
                        Easing.Function function = Easing.Function.LINEAR;
                        if (anim.ease().isPresent()) {
                            function = Easing.Function.fromString(anim.ease().get());
                        }

                        // Use the easing function to define the transition
                        float easedProgress = Easing.ease(direction, function, progress);
                        currentInfluence = state.getStartInfluence() + (targetInfluence - state.getStartInfluence()) * easedProgress;
                    }
                }
                state.setInfluence(currentInfluence);

                // Evaluate speed
                if (anim.speed().isPresent()) {
                    state.setSpeed((float) anim.speed().get().evaluate(variableValues));
                }

                // Update time
                state.setCurrentTime(state.getCurrentTime() + (deltaTime / 1000.0f) * state.getSpeed());

                // Handle progress (overrides time)
                if (anim.progress().isPresent()) {
                    float progress = (float) anim.progress().get().evaluate(variableValues);
                    TVAnimation tvAnim = ClientBase.getInstance().getTvAnimationRegistry().get(Identifier.fromString(anim.animation(), "animation"));
                    if (tvAnim != null) {
                        float duration = tvAnim.metadata().duration();
                        if (duration == 0) {
                            state.setCurrentTime(0);
                        } else {
                            state.setCurrentTime(progress * duration);
                        }
                    }
                }
            }

            // Clear triggers after processing
            component.getActiveTriggers().clear();
        });
    }
}
