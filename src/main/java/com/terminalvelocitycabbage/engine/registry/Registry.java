package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.*;

public class Registry<T> {

    protected final LinkedHashMap<Identifier, T> registryContents; //The contents of this registry
    protected final T defaultItem; //The default item if an attempted retrieval finds no results

    /**
     * Creates a new registry with type T.
     * @param defaultItem The item to be returned by this registry if none other is found
     */
    public Registry(T defaultItem) {
        this.registryContents = new LinkedHashMap<>();
        this.defaultItem = defaultItem;
    }

    /**
     * Creates a new registry with type T.
     * The default item of this constructor is null
     */
    public Registry() {
        this(null);
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     */
    public RegistryPair<T> register(Identifier identifier, T item) {
        if (registryContents.containsKey(identifier)) {
            Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored. This will likely cause issues later on (probably crashes).");
            return null;
        }
        registryContents.put(identifier, item);
        return new RegistryPair<>(identifier, item);
    }

    /**
     * Replaces the specified identifier with a new value
     * @param identifier The identifier of the object you want to replace
     * @param newItem The object to replace it with
     * @return A registry pair representing the new object and it's registry identifier
     */
    public RegistryPair<T> replace(Identifier identifier, T newItem) {
        if (!registryContents.containsKey(identifier)) {
            Log.warn("Cannot replace registry item with ID: " + identifier.toString() + " since it does not exist in this registry.");
            return null;
        }
        registryContents.replace(identifier, newItem);
        return new RegistryPair<>(identifier, newItem);
    }

    /**
     * Retrieves a specific resource by its identifier
     * @param identifier The identifier of the specific resource you wish to retrieve
     * @return The requested item or the default item if not found
     */
    public T get(Identifier identifier) {
        return registryContents.get(identifier);
    }

    public LinkedHashMap<Identifier, T> getRegistryContents() {
        return registryContents;
    }
}
