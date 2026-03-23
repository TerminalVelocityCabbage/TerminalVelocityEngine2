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
    private final List<String> variableNames = new ArrayList<>();

    record AnimationVariable<T>(String name, Class<T> type, Function<Entity, T> provider) {}

    public <T> void registerAnimationControllerVariable(String variable, Class<T> type, Function<Entity, T> provider) {
        registeredVariables.put(variable, new AnimationVariable<>(variable, type, provider));
        variableNames.add(variable);
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
        
        // Register variable names in environment
        env.setVariableNames(variableNames.toArray(new String[0]));

        // Add custom "if" function: if(condition, trueValue, falseValue)
        env.addFunction("if", 3, args -> args[0] != 0 ? args[1] : args[2]);
        
        // Add "clamp" function
        env.addFunction("clamp", 3, args -> Math.max(args[1], Math.min(args[2], args[0])));

        // Test expressions from spec (updated with "if")
        // influence = "1.0 - clamp(speed / 5.0, 0.0, if(on_ground, 1.0, 0.0))"
        String exprStr1 = "1.0 - clamp(speed / 5.0, 0.0, if(on_ground, 1.0, 0.0))";
        CompiledExpression expr1 = Crunch.compileExpression(exprStr1, env);

        Entity entity1 = new Entity(2.5f, true, false, 0f);
        assertEquals(0.5, expr1.evaluate(getVariableValues(entity1)), 0.001);

        Entity entity2 = new Entity(2.5f, false, false, 0f);
        assertEquals(1.0, expr1.evaluate(getVariableValues(entity2)), 0.001);

        // influence = "if(!on_ground && !above_water, 1.0, 0.0)"
        String exprStr2 = "if(!on_ground && !above_water, 1.0, 0.0)";
        CompiledExpression expr2 = Crunch.compileExpression(exprStr2, env);

        Entity entity3 = new Entity(0f, false, false, 5f);
        assertEquals(1.0, expr2.evaluate(getVariableValues(entity3)), 0.001);

        Entity entity4 = new Entity(0f, true, false, 5f);
        assertEquals(0.0, expr2.evaluate(getVariableValues(entity4)), 0.001);

        // arm_swinging = "if(height > 10, 1.0, 0.0)"
        String exprStr3 = "if(height > 10, 1.0, 0.0)";
        CompiledExpression expr3 = Crunch.compileExpression(exprStr3, env);

        Entity entity5 = new Entity(0f, true, false, 15f);
        assertEquals(1.0, expr3.evaluate(getVariableValues(entity5)), 0.001);

        Entity entity6 = new Entity(0f, true, false, 5f);
        assertEquals(0.0, expr3.evaluate(getVariableValues(entity6)), 0.001);
    }

    private double[] getVariableValues(Entity entity) {
        double[] values = new double[variableNames.size()];
        for (int i = 0; i < variableNames.size(); i++) {
            Object value = registeredVariables.get(variableNames.get(i)).provider().apply(entity);
            if (value instanceof Boolean b) {
                values[i] = b ? 1.0 : 0.0;
            } else if (value instanceof Number n) {
                values[i] = n.doubleValue();
            }
        }
        return values;
    }
}
