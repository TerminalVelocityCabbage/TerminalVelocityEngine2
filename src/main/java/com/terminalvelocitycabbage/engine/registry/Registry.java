package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.*;

public class Registry<T> {

    private final Map<Identifier, T> registryContents; //The contents of this registry
    private final List<String> priorities; //A list of namespaces which define the prioritized retrieval order of this registry
    private final T defaultItem; //The default item if an attempted retrieval finds no results

    /**
     * Creates a new registry with type T.
     * @param defaultItem The item to be returned by this registry if none other is found
     */
    public Registry(T defaultItem) {
        this.registryContents = new HashMap<>();
        this.priorities = new ArrayList<>();
        this.defaultItem = defaultItem;
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     */
    public void register(Identifier identifier, T item) {
        registryContents.put(identifier, item);
        var namespace = identifier.getNamespace();
        if (!priorities.contains(namespace)) {
            priorities.add(namespace);
        }
    }

    /**
     * Retrieves a specific resource by its identifier
     * @param identifier The identifier of the specific resource you wish to retrieve
     * @return The requested item or the default item if not found
     */
    public T retrieveSpecific(Identifier identifier) {
        if (registryContents.containsKey(identifier)) {
            return registryContents.get(identifier);
        }

        Log.warn("Tried to get item which is not registered on this registry: " + identifier.toString());
        return defaultItem;
    }

    /**
     * Retrieves an item of the highest priority from this registry based on the name of the item
     * @param itemName The name of the item you wish to retrieve
     * @return The item requested or the default item if none found
     */
    public T retrievePriority(String itemName) {

        Map<String, T> items = new HashMap();

        registryContents.forEach((identifier, item) -> {
            if (identifier.getResourceName().equals(itemName)) items.put(identifier.getNamespace(), item);
        });

        for (String namespace : priorities) {
            if (items.containsKey(namespace)) {
                return items.get(namespace);
            }
        }

        Log.warn("Tried to get item which is not registered on this registry: " + itemName);
        return defaultItem;
    }

    /**
     * Overwrites the priority namespaces of this registry
     * @param namespaces A collection of strings to define the namespaces in the order of first priority in item retrieval
     */
    public void setPriorities(String... namespaces) {
        priorities.clear();
        priorities.addAll(List.of(namespaces));
    }
}
