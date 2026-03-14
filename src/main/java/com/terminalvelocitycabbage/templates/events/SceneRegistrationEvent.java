package com.terminalvelocitycabbage.templates.events;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.List;

public class SceneRegistrationEvent extends RegistryEvent<Scene> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "scene_registration");

    private final GameFileSystem fileSystem;
    private final Registry<Routine> routineRegistry;

    public SceneRegistrationEvent(Registry<Scene> registry, GameFileSystem fileSystem, Registry<Routine> routineRegistry) {
        super(EVENT, registry);
        this.fileSystem = fileSystem;
        this.routineRegistry = routineRegistry;
    }

    public Identifier registerScene(String namespace, String name, Scene scene) {
        return register(new Identifier(namespace, "scene", name), scene).getIdentifier();
    }

    /**
     * Registers a scene from a toml file
     * @param namespace the namespace of the scene to register
     * @param resourceName the name of the resource to load the scene from
     * @return the identifier of the registered scene
     */
    public Identifier registerSceneFromFile(String namespace, String resourceName) {
        Identifier fileResource = sceneIdentifierOf(namespace, resourceName);
        Resource resource = fileSystem.getResource(ResourceCategory.SCENE, fileResource);
        if (resource == null) {
            Log.error("Could not find scene file: " + fileResource);
            return null;
        }

        Config config = TomlFormat.instance().createParser().parse(resource.asString());
        Config sceneConfig = config.get("scene");
        if (sceneConfig == null) {
            Log.error("Scene file does not contain a [scene] section: " + fileResource);
            return null;
        }

        String name = sceneConfig.get("name");
        if (name == null) {
            Log.error("Scene section does not contain a 'name' field: " + fileResource);
            return null;
        }

        Scene.Builder builder = Scene.builder();

        // Render Graph
        if (sceneConfig.contains("render_graph")) {
            builder.renderGraph(Identifier.fromString(sceneConfig.get("render_graph"), "render_graph"));
        }

        // Routines
        if (sceneConfig.contains("routines")) {
            List<String> routineIds = sceneConfig.get("routines");
            for (String routineId : routineIds) {
                Identifier identifier = Identifier.fromString(routineId, "routine");
                Routine routine = routineRegistry.get(identifier);
                if (routine == null) {
                    Log.error("Could not find routine: " + identifier);
                    continue;
                }
                builder.routines(routine);
            }
        }

        // Input Controllers
        if (sceneConfig.contains("input_controllers")) {
            List<String> inputControllerIds = sceneConfig.get("input_controllers");
            for (String inputControllerId : inputControllerIds) {
                builder.inputControllers(Identifier.fromString(inputControllerId, "controller"));
            }
        }

        // Texture Atlases
        if (sceneConfig.contains("texture_atlases")) {
            List<String> textureAtlasIds = sceneConfig.get("texture_atlases");
            for (String textureAtlasId : textureAtlasIds) {
                builder.textureAtlases(Identifier.fromString(textureAtlasId, "atlas"));
            }
        }

        // Entities
        List<Config> entities = config.get("entities");
        if (entities != null) {
            for (Config entityConfig : entities) {
                builder.entities(manager -> {
                    Entity entity;
                    if (entityConfig.contains("template")) {
                        entity = manager.createEntityFromTemplate(Identifier.fromString(entityConfig.get("template"), "entity_template"));
                    } else {
                        entity = manager.createEntity();
                    }

                    if (entityConfig.contains("component")) {
                        Config components = entityConfig.get("component");
                        for (Config.Entry entry : components.entrySet()) {
                            String componentId = entry.getKey().replace("_", "");
                            Object value = entry.getValue();

                            Class<? extends Component> componentClass = manager.getComponentClass(componentId);
                            if (componentClass == null) {
                                Log.error("Could not find component class for ID: " + componentId);
                                continue;
                            }

                            Component component = entity.hasComponent(componentClass) ? entity.getComponent(componentClass) : entity.addComponent(componentClass);
                            if (value instanceof Config componentProps) {
                                for (Config.Entry propEntry : componentProps.entrySet()) {
                                    Object entryValue = propEntry.getValue();
                                    String stringValue = entryValue == null ? "null" : entryValue.toString();
                                    component.parseComponentField(propEntry.getKey(), stringValue);
                                }
                            }
                        }
                    }
                });
            }
        }

        return registerScene(namespace, name, builder.build());
    }

    public Identifier sceneIdentifierOf(String namespace, String name) {
        return ResourceCategory.SCENE.identifierOf(namespace, name);
    }
}
