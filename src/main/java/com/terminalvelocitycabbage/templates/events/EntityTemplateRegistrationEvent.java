package com.terminalvelocitycabbage.templates.events;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class EntityTemplateRegistrationEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "EntityTemplateRegistrationEvent");

    private final Manager manager;
    private final GameFileSystem fileSystem;

    public EntityTemplateRegistrationEvent(Manager manager, GameFileSystem fileSystem) {
        super(EVENT);
        this.manager = manager;
        this.fileSystem = fileSystem;
    }

    public Identifier createEntityTemplate(String namespace, String entityName, Manager.EntityTemplateCreationCallback callback) {
        return manager.createEntityTemplate(new Identifier(namespace, "entity_template", entityName), callback).getIdentifier();
    }

    public Identifier createEntityTemplateFromFile(String namespace, String resourceName) {
        Identifier fileResource = entityIdentifierOf(namespace, resourceName);
        Resource resource = fileSystem.getResource(ResourceCategory.ENTITY, fileResource);
        if (resource == null) {
            Log.error("Could not find entity template file: " + fileResource);
            return null;
        }

        Config config = TomlFormat.instance().createParser().parse(resource.asString());
        String name = config.get("name");

        return createEntityTemplate(fileResource.namespace(), name, entity -> {
            Config components = config.get("component");
            if (components == null) return;

            for (Config.Entry entry : components.entrySet()) {
                String componentId = entry.getKey();
                Object value = entry.getValue();

                Class<? extends Component> componentClass = manager.getComponentClass(componentId);
                if (componentClass == null) {
                    Log.error("Could not find component class for ID: " + componentId);
                    continue;
                }

                Component component = entity.addComponent(componentClass);
                if (value instanceof Config componentProps) {
                    for (Config.Entry propEntry : componentProps.entrySet()) {
                        Object entryValue = propEntry.getValue();
                        String stringValue = entryValue == null ? "null" : entryValue.toString();
                        component.parseComponentField(propEntry.getKey(), stringValue);
                    }
                }
            }
        });
    }

    public Identifier entityIdentifierOf(String namespace, String name) {
        return new Identifier(namespace, "entity", name + ".entity.toml");
    }
}
