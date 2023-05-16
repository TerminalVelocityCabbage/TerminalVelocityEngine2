package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A type of registry which gives priority to certain namespaces.
 * This may be used for example in a resourcepack where some assets may be loaded of the same name but from different
 * sources on the virtual filesystem
 * @param <T> The type of registry this refers to
 */
public class PrioritizedRegistry<T> extends Registry<T> {

    private final List<String> priorities; //A list of namespaces which define the prioritized retrieval order of this registry

    /**
     * Creates a new registry with type T.
     *
     * @param defaultItem The item to be returned by this registry if none other is found
     */
    public PrioritizedRegistry(T defaultItem) {
        super(defaultItem);
        this.priorities = new ArrayList<>();
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     */
    @Override
    public void register(Identifier identifier, T item) {
        super.register(identifier, item);
        var namespace = identifier.getNamespace();
        if (!priorities.contains(namespace)) {
            priorities.add(namespace);
        }
    }


    /**
     * Retrieves an item of the highest priority from this registry based on the name of the item
     * @param itemName The name of the item you wish to retrieve
     * @return The item requested or the default item if none found
     */
    public T retrievePriority(String itemName) {

        Map<String, T> items = new HashMap();

        registryContents.forEach((identifier, item) -> {
            if (identifier.getName().equals(itemName)) items.put(identifier.getNamespace(), item);
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
