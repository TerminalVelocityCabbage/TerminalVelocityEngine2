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

    public static Builder builder() {
        return new Builder();
    }

    public void update(Manager manager) {
        Log.info("routine update");
        filteredSystems.forEach((id, quartet) -> {
            if (!quartet.getValue0().getStatus()) return;
            manager.getSystem(quartet.getValue2()).update(manager.getMatchingEntities(quartet.getValue3()), quartet.getValue1().getDeltaTime());
            quartet.getValue1().now();
        });
    }

    public void pauseSystem(Identifier systemIdentifier) {
        filteredSystems.get(systemIdentifier).getValue0().disable();
    }

    public void resumeSystem(Identifier systemIdentifier) {
        filteredSystems.get(systemIdentifier).getValue0().enable();
    }

    public static class Builder {

        Map<Identifier, Quartet<Toggle, MutableInstant, Class<? extends System>, ComponentFilter>> systems = new HashMap();

        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system) {
            return addNode(nodeIdentifier, system, null, true);
        }

        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system, ComponentFilter componentFilter) {
            return addNode(nodeIdentifier, system, componentFilter, true);
        }

        public Builder addNode(Identifier nodeIdentifier, Class<? extends System> system, ComponentFilter componentFilter, boolean automaticallyEnable) {
            if (systems.containsKey(nodeIdentifier)) {
                Log.crash("Could not add system node " + nodeIdentifier + " to Routine",
                        new RuntimeException("node " + nodeIdentifier + " already exists on this Routine."));
            }
            systems.put(nodeIdentifier, new Quartet<>(new Toggle(automaticallyEnable), MutableInstant.ofNow(), system, componentFilter));
            return this;
        }

        public Routine build() {
            return new Routine(systems);
        }

    }

}
