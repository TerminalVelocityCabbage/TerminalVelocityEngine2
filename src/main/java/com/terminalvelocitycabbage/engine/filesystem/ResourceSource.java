package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * Defines a resource root for an entrypoint or asset pack
 */
public class ResourceSource {

    int priority;
    Registry<ResourceRoot> resourceRootRegistry;

    public ResourceSource(int priority) {
        this.priority = priority;
        this.resourceRootRegistry = new Registry<>(null);
    }

    public Registry<ResourceRoot> getResourceRootRegistry() {
        return resourceRootRegistry;
    }

    /**
     * Registers a resource root at location "assets/namespace/root_name/" where namespace belongs to the identifier and
     * root_name is the name of the identifier.
     *
     * Example: registerDefaultSourceRoot(ResourceType.MODEL, (identifier) game:models) will result in a path at
     * assets/game/models
     *
     * @param type the type of resource held in this root
     * @param identifier the namespace:name of this resource root
     */
    public void registerDefaultSourceRoot(ResourceType type, Identifier identifier) {
        getResourceRootRegistry().register(identifier, new ResourceRoot(type, "assets/" + identifier.getNamespace() + "/" + identifier.getName()));
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
