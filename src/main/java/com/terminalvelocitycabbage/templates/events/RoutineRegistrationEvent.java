package com.terminalvelocitycabbage.templates.events;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoutineRegistrationEvent extends RegistryEvent<Routine> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "routine_registration");

    private final Manager manager;
    private final GameFileSystem fileSystem;

    public RoutineRegistrationEvent(Registry<Routine> routineRegistry, Manager manager, GameFileSystem fileSystem) {
        super(EVENT, routineRegistry);
        this.manager = manager;
        this.fileSystem = fileSystem;
    }

    public Identifier registerStep(String namespace, String name) {
        return new Identifier(namespace, "routine_step", name);
    }

    public Routine registerRoutine(Routine routine) {
        return register(routine).getElement();
    }

    public Routine registerRoutineFromFile(String namespace, String resourceName) {
        Identifier fileResource = routineIdentifierOf(namespace, resourceName);
        Resource resource = fileSystem.getResource(ResourceCategory.ROUTINE, fileResource);
        if (resource == null) {
            Log.error("Could not find routine file: " + fileResource);
            return null;
        }

        Config config = TomlFormat.instance().createParser().parse(resource.asString());
        Config routineConfig = config.get("routine");
        String name = routineConfig.get("name");

        Routine.Builder builder = Routine.builder(namespace, name);

        List<Config> steps = config.get("steps");
        if (steps != null) {
            for (Config stepConfig : steps) {
                String idStr = stepConfig.get("id");
                Identifier stepId = Identifier.fromString(idStr, "routine_step");

                if (stepConfig.contains("system")) {
                    String systemName = stepConfig.get("system");
                    builder.addStep(stepId, manager.getSystemClass(systemName.replace("_", "")));
                } else if (stepConfig.contains("parallel")) {
                    List<String> parallelSystems = stepConfig.get("parallel");
                    Set<Class<? extends System>> systemClasses = new HashSet<>();
                    for (String systemName : parallelSystems) {
                        systemClasses.add(manager.getSystemClass(systemName.replace("_", "")));
                    }
                    builder.addParallelStep(stepId, systemClasses);
                }
            }
        }

        return registerRoutine(builder.build());
    }

    public Identifier routineIdentifierOf(String namespace, String name) {
        return ResourceCategory.ROUTINE.identifierOf(namespace, name + ".routine.toml");
    }
}
