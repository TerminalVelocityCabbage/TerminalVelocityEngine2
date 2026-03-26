package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.templates.events.AnimationControllerFunctionRegistrationEvent;
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
    private final Map<String, List<Integer>> variableIndicesByBaseName = new HashMap<>();
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
    }

    public void registerFunction(String name, int args, ToDoubleFunction<double[]> function) {
        functions.put(name, new AnimationControllerFunction(name, args, function));
    }

    public <T> void registerVariable(String name, Class<T> type, Function<Entity, T> provider) {
        List<Integer> indices = new ArrayList<>();
        switch (type.getSimpleName()) {
            case "Vector3f":
                indices.add(addVariable(name + ".x", entity -> (double) ((Vector3f) provider.apply(entity)).x()));
                indices.add(addVariable(name + ".y", entity -> (double) ((Vector3f) provider.apply(entity)).y()));
                indices.add(addVariable(name + ".z", entity -> (double) ((Vector3f) provider.apply(entity)).z()));
                indices.add(addVariable(name + ".length", entity -> (double) ((Vector3f) provider.apply(entity)).length()));
                break;
            case "Float", "Double":
                indices.add(addVariable(name, entity -> ((Number) provider.apply(entity)).doubleValue()));
                break;
            case "Boolean":
                indices.add(addVariable(name, entity -> (Boolean) provider.apply(entity) ? 1.0 : 0.0));
                break;
            default:
                throw new IllegalArgumentException("Unsupported variable type: " + type.getSimpleName() + " There is also no way to register your own yet, report this.");
        }
        variableIndicesByBaseName.put(name, indices);
    }

    private int addVariable(String name, Function<Entity, Double> provider) {
        int index = variableNames.size();
        variableNames.add(name);
        variableProviders.put(name, provider);
        return index;
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

    public double[] getVariableValues(Entity entity, TVAnimationController controller) {
        double[] values = new double[variableNames.size()];
        for (String baseVarName : controller.variables().keySet()) {
            List<Integer> indices = variableIndicesByBaseName.get(baseVarName);
            if (indices != null) {
                for (int index : indices) {
                    values[index] = variableProviders.get(variableNames.get(index)).apply(entity);
                }
            }
        }
        return values;
    }

    public record AnimationControllerFunction(String name, int args, ToDoubleFunction<double[]> function) {}

}
