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
     * @param replaceIfExists Whether to replace an existing item with the same identifier if one already exists
     */
    public Identifier register(Identifier identifier, T item, boolean replaceIfExists) {
        if (contains(identifier)) {
            if (!replaceIfExists) {
                Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored. This will likely cause problems later one (probably crashes)");
                return null;
            }
            replace(identifier, item);
        } else {
            registryContents.put(identifier, item);
        }
        return identifier;
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     */
    public Identifier register(Identifier identifier, T item) {
        if (contains(identifier)) {
            Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored.");
            return null;
        }
        return register(identifier, item, false);
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param item The item to be registered. Must implement {@link Identifiable}
     */
    public Identifier register(T item) {
        if (item instanceof Identifiable identifiable) {
            return register(identifiable.getIdentifier(), item);
        } else {
            Log.error("Cannot register item " + item.getClass().getName() + " since it does not implement Identifiable.",
                    "Eiter implement Identifiable or register the item manually using the register(Identifier, T) method instead.");
        }
        return null;
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     * @param replaceIfExists Whether to replace an existing item with the same identifier if one already exists
     */
    public RegistryPair<T> getAndRegister(Identifier identifier, T item, boolean replaceIfExists) {
        if (contains(identifier)) {
            if (!replaceIfExists) {
                Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored. This will likely cause problems later one (probably crashes)");
                return null;
            }
            replace(identifier, item);
        } else {
            registryContents.put(identifier, item);
        }
        return new RegistryPair<>(identifier, item);
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param identifier The identifier of this registered item
     * @param item The item to be registered
     */
    public RegistryPair<T> getAndRegister(Identifier identifier, T item) {
        if (contains(identifier)) {
            Log.warn("Tried to register item of same identifier " + identifier.toString() + " twice, the second addition has been ignored.");
            return null;
        }
        return getAndRegister(identifier, item, false);
    }

    /**
     * Registers an item to this registry for retrieval later by its identifier or name
     * @param item The item to be registered. Must implement {@link Identifiable}
     */
    public RegistryPair<T> getAndRegister(T item) {
        if (item instanceof Identifiable identifiable) {
            return getAndRegister(identifiable.getIdentifier(), item);
        } else {
            Log.error("Cannot register item " + item.getClass().getName() + " since it does not implement Identifiable.",
                    "Eiter implement Identifiable or register the item manually using the register(Identifier, T) method instead.");
        }
        return null;
    }

    /**
     * Replaces the specified identifier with a new value
     * @param identifier The identifier of the object you want to replace
     * @param newItem The object to replace it with
     * @return A registry pair representing the new object and it's registry identifier
     */
    public RegistryPair<T> replace(Identifier identifier, T newItem) {
        if (!contains(identifier)) {
            Log.warn("Cannot replace registry item with ID: " + identifier.toString() + " since it does not exist in this registry.");
            return null;
        }
        registryContents.replace(identifier, newItem);
        return new RegistryPair<>(identifier, newItem);
    }

    /**
     * @param identifier the identifier to check for
     * @return whether this registry contains the specified identifier
     */
    public boolean contains(Identifier identifier) {
        return registryContents.containsKey(identifier);
    }

    /**
     * Retrieves a specific resource by its identifier
     * @param identifier The identifier of the specific resource you wish to retrieve
     * @return The requested item or the default item if not found
     */
    public T get(Identifier identifier) {
        return registryContents.get(identifier);
    }

    /**
     * @param namespace the namespace (portion before the : in an identifier) to search this registry for
     * @return All identifiers in this registry with that namespace
     */
    public Set<Identifier> getIdentifiersWithNamespace(String namespace) {
        Set<Identifier> identifiers = new LinkedHashSet<>();
        registryContents.keySet().forEach(identifier -> {
            if (identifier.namespace().equals(namespace)) identifiers.add(identifier);
        });
        return identifiers;
    }

    /**
     * @param name the name (portion after the : in an identifier) to search this registry for
     * @return All identifiers in this registry with that name
     */
    public Set<Identifier> getIdentifiersWithName(String name) {
        Set<Identifier> identifiers = new LinkedHashSet<>();
        registryContents.keySet().forEach(identifier -> {
            if (identifier.name().equals(name)) identifiers.add(identifier);
        });
        return identifiers;
    }

    public LinkedHashMap<Identifier, T> getRegistryContents() {
        return registryContents;
    }
}
