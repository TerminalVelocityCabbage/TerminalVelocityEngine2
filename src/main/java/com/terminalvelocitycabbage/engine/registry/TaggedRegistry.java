package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaggedRegistry<T> extends Registry<T> {

    HashMap<String, Set<RegistryPair<T>>> taggedLookup = new HashMap<>();

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     * @param tags All the tags that this registry item should be associated with
     */
    public RegistryPair<T> register(Identifier identifier, T item, String... tags) {
        var pair = super.register(identifier, item);
        for (String tag : tags) {
            if (!taggedLookup.containsKey(tag)) taggedLookup.put(tag, new HashSet<>());
            taggedLookup.get(tag).add(pair);
        }
        return new RegistryPair<>(identifier, item);
    }

    /**
     * Returns all registered items with the given tag
     * @param tag The tag to retrieve items from
     * @return A set of RegistryPairs made up of all items registered with this tag
     */
    public Set<RegistryPair<T>> getTagged(String tag) {
        if (!taggedLookup.containsKey(tag)) Log.warn("Tried to get tagged items with tag " + tag + " which does not exist in this registry.");
        return taggedLookup.getOrDefault(tag, new HashSet<>());
    }

}
