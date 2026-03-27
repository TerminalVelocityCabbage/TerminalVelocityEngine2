package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import org.junit.jupiter.api.Test;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrunchExpressionTest {

    // Simulated Entity class
    static class Entity {
        float speed;
        boolean onGround;
        boolean aboveWater;
        float height;

        public Entity(float speed, boolean onGround, boolean aboveWater, float height) {
            this.speed = speed;
            this.onGround = onGround;
            this.aboveWater = aboveWater;
            this.height = height;
        }
    }

    // Storage for registered variables
    private final Map<String, AnimationVariable<?>> registeredVariables = new HashMap<>();
    private Entity currentEntity;

    record AnimationVariable<T>(String name, Class<T> type, Function<Entity, T> provider) {}

    public <T> void registerAnimationControllerVariable(String variable, Class<T> type, Function<Entity, T> provider) {
        registeredVariables.put(variable, new AnimationVariable<>(variable, type, provider));
    }

    @Test
    public void testCrunchIntegration() {
        // Register variables
        registerAnimationControllerVariable("speed", Float.class, entity -> entity.speed);
        registerAnimationControllerVariable("on_ground", Boolean.class, entity -> entity.onGround);
        registerAnimationControllerVariable("above_water", Boolean.class, entity -> entity.aboveWater);
        registerAnimationControllerVariable("height", Float.class, entity -> entity.height);

        // Setup Crunch environment
        EvaluationEnvironment env = new EvaluationEnvironment();
        
        // Register lazy variables
        registeredVariables.values().forEach(v -> {
            env.addLazyVariable(v.name(), () -> {
                Object value = v.provider().apply(currentEntity);
                if (value instanceof Boolean b) return b ? 1.0 : 0.0;
                if (value instanceof Number n) return n.doubleValue();
                return 0.0;
            });
        });

        // Add custom "if" function: if(condition, trueValue, falseValue)
        env.addFunction("if", 3, args -> args[0] != 0 ? args[1] : args[2]);
        
        // Add "clamp" function
        env.addFunction("clamp", 3, args -> Math.max(args[1], Math.min(args[2], args[0])));

        // Test expressions from spec (updated with "if")
        // influence = "1.0 - clamp(speed / 5.0, 0.0, if(on_ground, 1.0, 0.0))"
        String exprStr1 = "1.0 - clamp(speed / 5.0, 0.0, if(on_ground, 1.0, 0.0))";
        CompiledExpression expr1 = Crunch.compileExpression(exprStr1, env);

        currentEntity = new Entity(2.5f, true, false, 0f);
        assertEquals(0.5, expr1.evaluate(), 0.001);

        currentEntity = new Entity(2.5f, false, false, 0f);
        assertEquals(1.0, expr1.evaluate(), 0.001);

        // influence = "if(!on_ground && !above_water, 1.0, 0.0)"
        String exprStr2 = "if(!on_ground && !above_water, 1.0, 0.0)";
        CompiledExpression expr2 = Crunch.compileExpression(exprStr2, env);

        currentEntity = new Entity(0f, false, false, 5f);
        assertEquals(1.0, expr2.evaluate(), 0.001);

        currentEntity = new Entity(0f, true, false, 5f);
        assertEquals(0.0, expr2.evaluate(), 0.001);

        // arm_swinging = "if(height > 10, 1.0, 0.0)"
        String exprStr3 = "if(height > 10, 1.0, 0.0)";
        CompiledExpression expr3 = Crunch.compileExpression(exprStr3, env);

        currentEntity = new Entity(0f, true, false, 15f);
        assertEquals(1.0, expr3.evaluate(), 0.001);

        currentEntity = new Entity(0f, true, false, 5f);
        assertEquals(0.0, expr3.evaluate(), 0.001);
    }

}
