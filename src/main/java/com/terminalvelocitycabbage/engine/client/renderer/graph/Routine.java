package com.terminalvelocitycabbage.engine.client.renderer.graph;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.ComponentFilter;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.MutableInstant;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Quartet;

import java.util.HashMap;
import java.util.Map;

/**
 * A node for an {@link RenderGraph}, specifically for executing a set of ECS systems.
 * Does not have to be called by a Render Graph, can also be useful serverside to chain repeating logic.
 */
public non-sealed class Routine implements GraphNode {

    //This Map stores data about this managed system including:
    // - If it's enabled
    // - When it was last executed (for use in calculating deltaTime)
    // - The Class which defines this System's Logic
    // - The ComponentFilter which this system will operate on the current matching entities with
    Map<Identifier, Quartet<Toggle, MutableInstant, Class<? extends System>, ComponentFilter>> filteredSystems;

    private Routine(Map<Identifier, Quartet<Toggle, MutableInstant, Class<? extends System>, ComponentFilter>> managedSystems) {
        this.filteredSystems = managedSystems;
    }

    /**
     * @return a new instance of {@link Routine.Builder} for use in configuring a new Render Graph.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param manager the ESC manager that this routine operates on, usually returned by the client or server entrypoint
     */
    public void update(Manager manager) {
        Log.info("routine update");
        filteredSystems.forEach((id, quartet) -> {
            if (!quartet.getValue0().getStatus()) return;
            manager.getSystem(quartet.getValue2()).update(manager.getMatchingEntities(quartet.getValue3()), quartet.getValue1().getDeltaTime());
            quartet.getValue1().now();
        });
    }

    /**
     * Pauses the specified node of this routine, when paused a node will be skipped in this routines next update pass
     * @param systemIdentifier the {@link Identifier} for the node that you want to resume
     */
    public void pauseSystem(Identifier systemIdentifier) {
        filteredSystems.get(systemIdentifier).getValue0().disable();
    }

    /**
     * Resumes or "un-pauses" the specified node of this routine
     * @param systemIdentifier the {@link Identifier} for the node that you want to resume
     */
    public void resumeSystem(Identifier systemIdentifier) {
        filteredSystems.get(systemIdentifier).getValue0().enable();
    }

    public static class Builder {

        Map<Identifier, Quartet<Toggle, MutableInstant, Class<? extends System>, ComponentFilter>> systems = new HashMap();

        /**
         * Adds a node to this Routine. addNode calls should be specified in the order that they should be executed.
         * This method automatically enabled this node, and assumes that this system does not need entities filtered,
         * useful for systems that perform logic not on entities.
         * @param nodeIdentifier An {@link Identifier} to identify this node (useful for pausing and publishing events)
         * @param system The system that this node executes
         * @return this Builder for easy chaining of add nodes
         */
        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system) {
            return addNode(nodeIdentifier, system, null, true);
        }

        /**
         * Adds a node to this Routine. addNode calls should be specified in the order that they should be executed.
         * This method automatically enabled this node
         * @param nodeIdentifier An {@link Identifier} to identify this node (useful for pausing and publishing events)
         * @param system The system that this node executes
         * @param componentFilter The component filter that filters entities to be passed to the system specified
         * @return this Builder for easy chaining of add nodes
         */
        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system, ComponentFilter componentFilter) {
            return addNode(nodeIdentifier, system, componentFilter, true);
        }

        /**
         * Adds a node to this Routine. addNode calls should be specified in the order that they should be executed.
         * @param nodeIdentifier An {@link Identifier} to identify this node (useful for pausing and publishing events)
         * @param system The system that this node executes
         * @param componentFilter The component filter that filters entities to be passed to the system specified
         * @param automaticallyEnable A boolean to represent if this node should be executed or paused by default
         * @return this Builder for easy chaining of add nodes
         */
        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system, ComponentFilter componentFilter, boolean automaticallyEnable) {
            if (systems.containsKey(nodeIdentifier)) {
                Log.crash("Could not add system node " + nodeIdentifier + " to Routine",
                        new RuntimeException("node " + nodeIdentifier + " already exists on this Routine."));
            }
            systems.put(nodeIdentifier, new Quartet<>(new Toggle(automaticallyEnable), MutableInstant.ofNow(), system, componentFilter));
            return this;
        }

        /**
         * @return A new instance of {@link Routine} based on this builder configuration.
         */
        public Routine build() {
            return new Routine(systems);
        }

    }

}
