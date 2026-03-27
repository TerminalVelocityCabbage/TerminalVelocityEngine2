package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.templates.events.AnimationControllerFunctionRegistrationEvent;
import org.joml.Vector3f;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class AnimationControllerManager {

    private final Map<String, Function<Entity, Double>> variableProviders = new HashMap<>();
    private final Map<String, AnimationControllerFunction> functions = new HashMap<>();
    private final Map<String, CompiledExpression> expressionCache = new HashMap<>();
    private EvaluationEnvironment cachedEnv;
    private Entity currentEntity;

    public void setCurrentEntity(Entity currentEntity) {
        this.currentEntity = currentEntity;
    }

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
        switch (type.getSimpleName()) {
            case "Vector3f":
                var vec = (Vector3f) provider.apply(null);
                addVariable(name + ".x", entity -> (double) vec.x());
                addVariable(name + ".y", entity -> (double) vec.y());
                addVariable(name + ".z", entity -> (double) vec.z());
                addVariable(name + ".length", entity -> (double) vec.length());
                break;
            case "Float", "Double":
                addVariable(name, entity -> ((Number) provider.apply(entity)).doubleValue());
                break;
            case "Boolean":
                addVariable(name, entity -> (Boolean) provider.apply(entity) ? 1.0 : 0.0);
                break;
            default:
                throw new IllegalArgumentException("Unsupported variable type: " + type.getSimpleName() + " There is also no way to register your own yet, report this.");
        }
    }

    private void addVariable(String name, Function<Entity, Double> provider) {
        variableProviders.put(name, provider);
    }

    public EvaluationEnvironment createEvaluationEnvironment() {
        if (cachedEnv != null) return cachedEnv;
        EvaluationEnvironment env = new EvaluationEnvironment();
        variableProviders.forEach((name, provider) -> {
            env.addLazyVariable(name, () -> provider.apply(currentEntity));
        });
        for (AnimationControllerFunction function : functions.values()) {
            env.addFunction(function.name(), function.args(), function.function());
        }
        cachedEnv = env;
        return env;
    }

    public CompiledExpression compileExpression(String expression) {
        return expressionCache.computeIfAbsent(expression, e -> Crunch.compileExpression(e, createEvaluationEnvironment()));
    }

    public record AnimationControllerFunction(String name, int args, ToDoubleFunction<double[]> function) {}

}
