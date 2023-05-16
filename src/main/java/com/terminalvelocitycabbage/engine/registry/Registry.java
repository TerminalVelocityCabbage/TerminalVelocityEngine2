package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.*;

public class Registry<T> {

    protected final LinkedHashMap<Identifier, T> registryContents; //The contents of this registry
    protected final T defaultItem; //The default item if an attempted retrieval finds no results
    public static final String PATTERN = "[A-Za-z0-9_]+:[A-Za-z0-9_]+";

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
    public void register(Identifier identifier, T item) {
        if (registryContents.containsKey(identifier)) {
            Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored.");
            return;
        }
        registryContents.put(identifier, item);
    }

    /**
     * Retrieves a specific resource by its identifier
     * @param identifier The identifier of the specific resource you wish to retrieve
     * @return The requested item or the default item if not found
     */
    public T get(Identifier identifier) {
        return get(identifier.toString());
    }

    /**
     * Retrieves a specific resource by its identifier in string format
     * @param identifier The identifier of the specific resource you wish to retrieve
     * @return The requested item or the default item if not found
     */
    public T get(String identifier) {

        if (!identifier.matches(PATTERN)) Log.crash("Could not find registry item with identifier " + identifier, new RuntimeException("identifier does not match pattern " + PATTERN));

        for (Map.Entry<Identifier, T> identifierTEntry : registryContents.entrySet()) {
            if (identifierTEntry.getKey().equalsString(identifier)) return identifierTEntry.getValue();
        }

        Log.warn("Tried to get item which is not registered on this registry: " + identifier);
        return defaultItem;
    }

    public LinkedHashMap<Identifier, T> getRegistryContents() {
        return registryContents;
    }
}
