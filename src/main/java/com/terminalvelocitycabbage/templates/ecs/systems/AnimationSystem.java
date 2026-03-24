package com.terminalvelocitycabbage.templates.ecs.systems;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.AnimationControllerManager;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimation;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVAnimationController;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.ecs.components.AnimationControllerComponent;
import redempt.crunch.CompiledExpression;

public class AnimationSystem extends System {

    @Override
    public void update(Manager manager, float deltaTime) {
        manager.getEntitiesWith(AnimationControllerComponent.class).forEach(entity -> {
            AnimationControllerComponent component = entity.getComponent(AnimationControllerComponent.class);
            if (component.getControllerIdentifier() == null) return;

            TVAnimationController controller = ClientBase.getInstance().getTvAnimationControllerRegistry().get(component.getControllerIdentifier());
            if (controller == null) return;

            AnimationControllerManager animManager = ClientBase.getInstance().getAnimationControllerManager();
            double[] variableValues = animManager.getVariableValues(entity);

            for (TVAnimationController.TVAnimationControllerAnimation anim : controller.animations().values()) {
                AnimationControllerComponent.AnimationState state = component.getAnimationStates().computeIfAbsent(anim.animation(), k -> new AnimationControllerComponent.AnimationState());

                // Handle Triggered Animations
                if (anim.trigger().isPresent()) {
                    String triggerName = anim.trigger().get();
                    if (component.getActiveTriggers().contains(triggerName)) {
                        state.setInfluence(1.0f);
                        state.setCurrentTime(0); // Reset for trigger
                    }
                }

                // Evaluate influence
                if (anim.influence().isPresent()) {
                    CompiledExpression expr = animManager.compileExpression(anim.influence().get());
                    state.setInfluence((float) expr.evaluate(variableValues));
                }

                // Evaluate speed
                if (anim.speed().isPresent()) {
                    CompiledExpression expr = animManager.compileExpression(anim.speed().get());
                    state.setSpeed((float) expr.evaluate(variableValues));
                }

                // Update time
                state.setCurrentTime(state.getCurrentTime() + (deltaTime / 1000.0f) * state.getSpeed());

                // Handle progress (overrides time)
                if (anim.progress().isPresent()) {
                    CompiledExpression expr = animManager.compileExpression(anim.progress().get());
                    float progress = (float) expr.evaluate(variableValues);
                    TVAnimation tvAnim = ClientBase.getInstance().getTvAnimationRegistry().get(Identifier.fromString(anim.animation(), "tv_animation"));
                    if (tvAnim != null) {
                        state.setCurrentTime(progress * tvAnim.metadata().duration());
                    }
                }
            }

            // Clear triggers after processing
            component.getActiveTriggers().clear();
        });
    }
}
