package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.RoutineSystemExecutionEvent;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * A node for an {@link RenderGraph}, specifically for executing a set of ECS systems.
 * Does not have to be called by a Render Graph, can also be useful serverside to chain repeating logic.
 */
public non-sealed class Routine implements GraphNode {

    private final Map<Identifier, Step> steps;
    private final ForkJoinPool pool;

    private Routine(Map<Identifier, Step> steps) {
        this.steps = steps;
        this.pool = new ForkJoinPool();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void update(Manager manager, EventDispatcher eventDispatcher, float deltaTime) {
        for (Map.Entry<Identifier, Step> step : steps.entrySet()) {
            eventDispatcher.dispatchEvent(new RoutineSystemExecutionEvent(RoutineSystemExecutionEvent.pre(step.getKey()), manager));
            step.getValue().execute(manager, deltaTime, pool);
            eventDispatcher.dispatchEvent(new RoutineSystemExecutionEvent(RoutineSystemExecutionEvent.post(step.getKey()), manager));
        }
    }

    public void shutdown() {
        pool.shutdown();
    }

    private interface Step {
        void execute(Manager manager, float deltaTime, ForkJoinPool pool);
    }

    private record SequentialStep(Class<? extends System> system) implements Step {

        @Override
        public void execute(Manager manager, float deltaTime, ForkJoinPool pool) {
            manager.getSystem(system).update(manager, deltaTime);
        }
    }

    private record ParallelStep(Set<Class<? extends System> > systems) implements Step {

        @Override
        public void execute(Manager manager, float deltaTime, ForkJoinPool pool) {
            Set<ForkJoinTask<?>> tasks = new HashSet<>(systems.size());
            for (Class<? extends System> system : systems) {
                tasks.add(pool.submit(() -> manager.getSystem(system).update(manager, deltaTime)));
            }
            tasks.forEach(ForkJoinTask::join);
        }
    }

    public static class Builder {

        private final Map<Identifier, Step> steps;

        private Builder() {
            steps = new LinkedHashMap<>();
        }

        public Builder addStep(Identifier stepIdentifier, Class<? extends System> system) {
            steps.put(stepIdentifier, new SequentialStep(system));
            return this;
        }

        public Builder addParallelStep(Identifier stepIdentifier, Class<? extends System>... systems) {
            if (systems.length == 0) Log.crash("Error adding parallel step to routine", new IllegalArgumentException("At least one system required"));
            steps.put(stepIdentifier, new ParallelStep(Set.of(systems)));
            return this;
        }

        public Routine build() {
            return new Routine(steps);
        }
    }

}
