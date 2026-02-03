package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.RoutineSystemExecutionEvent;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * A node for an {@link RenderGraph}, specifically for executing a set of ECS systems.
 * Does not have to be called by a Render Graph, can also be useful serverside to chain repeating logic.
 */
public non-sealed class Routine implements GraphNode, Identifiable {

    private final Identifier identifier;
    private final Map<Identifier, Step> steps;
    private final ForkJoinPool pool;

    private Routine(Identifier identifier, Map<Identifier, Step> steps) {
        this.identifier = identifier;
        this.steps = steps;
        this.pool = new ForkJoinPool();
    }

    /**
     * @return A new Builder instance to configure a new Routine
     */
    public static Builder builder(String namespace, String name) {
        return new Builder(new Identifier(namespace, "routine", name));
    }

    /**
     * @param manager The ECS Manager for this entrypoint, passed to the system being executed to operate on
     * @param eventDispatcher The event dispatcher for which to dispatch pre- and post-routine step events for so that
     *                        Mods can inject their own systems into the pipeline
     * @param deltaTime The time since the last execution of this Routine
     */
    public void update(Manager manager, EventDispatcher eventDispatcher, long deltaTime) {
        for (Map.Entry<Identifier, Step> step : steps.entrySet()) {
            eventDispatcher.dispatchEvent(new RoutineSystemExecutionEvent(RoutineSystemExecutionEvent.pre(step.getKey()), manager, deltaTime));
            step.getValue().execute(manager, deltaTime, pool);
            eventDispatcher.dispatchEvent(new RoutineSystemExecutionEvent(RoutineSystemExecutionEvent.post(step.getKey()), manager, deltaTime));
        }
    }

    /**
     * Shuts down this routine and cleans up the forkjoinpool
     */
    public void shutdown() {
        pool.shutdown();
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
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

        private final Identifier identifier;
        private final Map<Identifier, Step> steps;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
            steps = new LinkedHashMap<>();
        }

        /**
         * @param stepIdentifier The identifier used to identify this step in the routine especially for mods
         * @param system The system class which will be executed when this step runs
         * @return This builder for easy chaining of methods
         */
        public Builder addStep(Identifier stepIdentifier, Class<? extends System> system) {
            steps.put(stepIdentifier, new SequentialStep(system));
            return this;
        }

        /**
         * @param stepIdentifier The identifier used to identify this step in the routine, especially for mods
         * @param systems The set of systems which will be executed in parallel during this step in the routine
         *                Note that systems inside this parallel node do not complete in any guaranteed order
         * @return This builder for easy chaining of methods
         */
        public Builder addParallelStep(Identifier stepIdentifier, Class<? extends System>... systems) {
            if (systems.length == 0) Log.crash("Error adding parallel step to routine", new IllegalArgumentException("At least one system required"));
            steps.put(stepIdentifier, new ParallelStep(Set.of(systems)));
            return this;
        }

        /**
         * @return A new Routine instance from the configuration of this builder
         */
        public Routine build() {
            return new Routine(identifier, steps);
        }
    }

}
