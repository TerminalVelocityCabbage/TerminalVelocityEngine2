package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.templates.events.AnimationControllerFunctionRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.AnimationControllerVariableRegistrationEvent;
import org.joml.Vector3f;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class AnimationControllerManager {

    private final Map<String, Function<Entity, Double>> variableProviders = new HashMap<>();
    private final List<String> variableNames = new ArrayList<>();
    private final Map<String, AnimationControllerFunction> functions = new HashMap<>();
    private final Map<String, CompiledExpression> expressionCache = new HashMap<>();
    private EvaluationEnvironment cachedEnv;

    public void init(EventDispatcher dispatcher) {
        // Register default functions
        registerFunction("if", 3, args -> args[0] != 0 ? args[1] : args[2]);
        registerFunction("clamp", 3, args -> Math.max(args[1], Math.min(args[2], args[0])));
        registerFunction("dot", 6, args -> args[0] * args[3] + args[1] * args[4] + args[2] * args[5]);

        // Dispatch events
        dispatcher.dispatchEvent(new AnimationControllerFunctionRegistrationEvent(this::registerFunction));
        dispatcher.dispatchEvent(new AnimationControllerVariableRegistrationEvent(this::registerVariable));
    }

    public void registerFunction(String name, int args, ToDoubleFunction<double[]> function) {
        functions.put(name, new AnimationControllerFunction(name, args, function));
    }

    public <T> void registerVariable(String name, Class<T> type, Function<Entity, T> provider) {
        if (type == Vector3f.class) {
            addVariable(name + ".x", entity -> (double) ((Vector3f) provider.apply(entity)).x());
            addVariable(name + ".y", entity -> (double) ((Vector3f) provider.apply(entity)).y());
            addVariable(name + ".z", entity -> (double) ((Vector3f) provider.apply(entity)).z());
            addVariable(name + ".length", entity -> (double) ((Vector3f) provider.apply(entity)).length());
        } else if (type == Float.class || type == Double.class || type == float.class || type == double.class) {
            addVariable(name, entity -> ((Number) provider.apply(entity)).doubleValue());
        } else if (type == Boolean.class || type == boolean.class) {
            addVariable(name, entity -> (Boolean) provider.apply(entity) ? 1.0 : 0.0);
        }
    }

    private void addVariable(String name, Function<Entity, Double> provider) {
        variableNames.add(name);
        variableProviders.put(name, provider);
    }

    public EvaluationEnvironment createEvaluationEnvironment() {
        if (cachedEnv != null) return cachedEnv;
        EvaluationEnvironment env = new EvaluationEnvironment();
        env.setVariableNames(variableNames.toArray(new String[0]));
        for (AnimationControllerFunction function : functions.values()) {
            env.addFunction(function.name(), function.args(), function.function());
        }
        cachedEnv = env;
        return env;
    }

    public CompiledExpression compileExpression(String expression) {
        return expressionCache.computeIfAbsent(expression, e -> Crunch.compileExpression(e, createEvaluationEnvironment()));
    }

    public double[] getVariableValues(Entity entity) {
        double[] values = new double[variableNames.size()];
        for (int i = 0; i < variableNames.size(); i++) {
            values[i] = variableProviders.get(variableNames.get(i)).apply(entity);
        }
        return values;
    }

    public record AnimationControllerFunction(String name, int args, ToDoubleFunction<double[]> function) {}

}
