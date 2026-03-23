package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import org.junit.jupiter.api.Test;
import org.joml.Vector3f;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrunchComplexTypeTest {

    // Simulated Entity class
    static class Entity {
        Vector3f pos;
        Vector3f velocity;

        public Entity(Vector3f pos, Vector3f velocity) {
            this.pos = pos;
            this.velocity = velocity;
        }
    }

    // Storage for registered variables
    private final Map<String, Function<Entity, Double>> variableProviders = new HashMap<>();
    private final List<String> variableNames = new ArrayList<>();

    public <T> void registerAnimationControllerVariable(String variable, Class<T> type, Function<Entity, T> provider) {
        if (type == Vector3f.class) {
            addVariable(variable + ".x", entity -> (double) ((Vector3f) provider.apply(entity)).x());
            addVariable(variable + ".y", entity -> (double) ((Vector3f) provider.apply(entity)).y());
            addVariable(variable + ".z", entity -> (double) ((Vector3f) provider.apply(entity)).z());
            addVariable(variable + ".length", entity -> (double) ((Vector3f) provider.apply(entity)).length());
        } else if (type == Float.class || type == Double.class) {
            addVariable(variable, entity -> ((Number) provider.apply(entity)).doubleValue());
        } else if (type == Boolean.class) {
            addVariable(variable, entity -> (Boolean) provider.apply(entity) ? 1.0 : 0.0);
        }
    }

    private void addVariable(String name, Function<Entity, Double> doubleProvider) {
        variableNames.add(name);
        variableProviders.put(name, doubleProvider);
    }

    @Test
    public void testVector3fSupport() {
        // Register variables
        registerAnimationControllerVariable("pos", Vector3f.class, entity -> entity.pos);
        registerAnimationControllerVariable("vel", Vector3f.class, entity -> entity.velocity);

        // Setup Crunch environment
        EvaluationEnvironment env = new EvaluationEnvironment();
        env.setVariableNames(variableNames.toArray(new String[0]));

        // Add "dot" function for two vectors (takes 6 components)
        // Usage: dot(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z)
        env.addFunction("dot", 6, args -> args[0] * args[3] + args[1] * args[4] + args[2] * args[5]);

        // Test 1: Component access
        String exprStr1 = "pos.x + pos.y + pos.z";
        CompiledExpression expr1 = Crunch.compileExpression(exprStr1, env);
        Entity entity1 = new Entity(new Vector3f(1, 2, 3), new Vector3f(0, 0, 0));
        assertEquals(6.0, expr1.evaluate(getVariableValues(entity1)), 0.001);

        // Test 2: Length access
        String exprStr2 = "pos.length";
        CompiledExpression expr2 = Crunch.compileExpression(exprStr2, env);
        assertEquals(Math.sqrt(1*1 + 2*2 + 3*3), expr2.evaluate(getVariableValues(entity1)), 0.001);

        // Test 3: Dot product
        String exprStr3 = "dot(pos.x, pos.y, pos.z, vel.x, vel.y, vel.z)";
        CompiledExpression expr3 = Crunch.compileExpression(exprStr3, env);
        Entity entity2 = new Entity(new Vector3f(1, 0, 0), new Vector3f(0, 1, 0));
        assertEquals(0.0, expr3.evaluate(getVariableValues(entity2)), 0.001);
        
        Entity entity3 = new Entity(new Vector3f(1, 2, 3), new Vector3f(4, 5, 6));
        // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
        assertEquals(32.0, expr3.evaluate(getVariableValues(entity3)), 0.001);
    }

    private double[] getVariableValues(Entity entity) {
        double[] values = new double[variableNames.size()];
        for (int i = 0; i < variableNames.size(); i++) {
            values[i] = variableProviders.get(variableNames.get(i)).apply(entity);
        }
        return values;
    }
}
