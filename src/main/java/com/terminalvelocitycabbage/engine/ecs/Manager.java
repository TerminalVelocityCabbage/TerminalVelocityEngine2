package com.terminalvelocitycabbage.engine.ecs;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.pools.MultiPool;
import com.terminalvelocitycabbage.engine.pools.ReflectionPool;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import javax.management.ReflectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The Manager is what any implementation should interact with to "manage" their entities components and systems.
 * This is where you register your systems, add your entities, and store your components.
 */
public class Manager {

    //The list of created components that can be added to any entity
    Set<Component> componentTypeSet;
    Map<String, List<Class<? extends Component>>> componentTagMap;
    //The pool of free components
    MultiPool componentPool;

    //The pool of free entities
    ReflectionPool<Entity> entityPool;
    //The list of active entities
    List<Entity> activeEntities;

    //The list of systems that runs on this manager
    Map<Class<? extends System>, System> systems;

    public Manager() {
        componentTypeSet = new HashSet<>();
        componentTagMap = new HashMap<>();
        activeEntities = new ArrayList<>();
        systems = new HashMap<>();

        componentPool = new MultiPool();
        entityPool = new ReflectionPool<>(Entity.class, 0);
    }

    /**
     * Adds a component to the componentTypeSet
     * @param componentType the class of the component you wish to add to the pool
     * @param <T> The type of the component, must extend {@link Component}
     */
    public <T extends Component> void registerComponent(Class<T> componentType) {
        registerComponent(componentType, 0);
    }

    /**
     * Adds a component to the componentTypeSet
     * @param componentType the class of the component you wish to add to the pool
     * @param initialPoolSize The number of empty component to fill this pool with
     * @param <T> The type of the component, must extend {@link Component}
     */
    public <T extends Component> void registerComponent(Class<T> componentType, int initialPoolSize) {
        try {
            componentTypeSet.add(componentType.getDeclaredConstructor().newInstance());
            componentPool.getPool(componentType, true, initialPoolSize);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.crash("Could not Create Component", new RuntimeException(e));
        }
    }

    /**
     * Adds a component to the componentTypeSet
     * @param componentType the class of the component you wish to add to the pool
     * @param <T> The type of the component, must extend {@link Component}
     */
    public <T extends Component> void registerComponent(Class<T> componentType, String... componentTags) {
        registerComponent(componentType, 0, componentTags);
    }

    /**
     * Adds a component to the componentTypeSet
     * @param componentType the class of the component you wish to add to the pool
     * @param initialPoolSize The number of empty component to fill this pool with
     * @param <T> The type of the component, must extend {@link Component}
     */
    public <T extends Component> void registerComponent(Class<T> componentType, int initialPoolSize, String... componentTags) {
        try {
            componentTypeSet.add(componentType.getDeclaredConstructor().newInstance());
            componentPool.getPool(componentType, true, initialPoolSize);
            Arrays.stream(componentTags).toList().forEach(tag -> {
                if (!componentTagMap.containsKey(tag)) componentTagMap.put(tag, new ArrayList<>());
                componentTagMap.get(tag).add(componentType);
            });
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.crash("Could not Create Component", new RuntimeException(e));
        }
    }

    /**
     * Gets a component of the type requested from the componentTypeSet
     * @param type the class of the component you wish to retrieve
     * @param <T> any component which extends {@link Component}
     * @return a component object from the componentTypeSet of the type requested.
     */
    public <T extends Component> T obtainComponent(Class<T> type) {
        if (!componentPool.hasType(type)) Log.crash("Could not retrieve pool of type " + type.getName() + " has this pool been added?", new RuntimeException("No pool exists of type " + type.getName()));
        return componentPool.obtain(type);
    }

    /**
     * @param tag a string representing a tag associated with the components you want to retrieve
     * @param <T> any class that implements component
     * @return a list of components with this tag
     */
    public <T extends Component> List<T> obtainComponentsOf(String tag) {
        List<T> components = new ArrayList<>();
        for (Class<? extends Component> componentType: getComponentTypesOf(tag)) {
            components.add((T)obtainComponent(componentType));
        }
        return components;
    }

    /**
     * @param tag a string representing a tag associated with the components you want to retrieve
     * @return a list of component types associated with the given tag
     */
    public List<Class<? extends Component>> getComponentTypesOf(String tag) {
        if (!componentTagMap.containsKey(tag)) Log.warn("No components exist on tag " + tag);
        return componentTagMap.get(tag);
    }

    /**
     * creates a new entity and adds it to the active entities list for modification later
     *
     * @return the newly created entity
     */
    public Entity createEntity() {
        Entity entity = entityPool.obtain();
        entity.setManager(this);
        activeEntities.add(entity);
        return entity;
    }

    /**
     * creates a new entity and adds it to the active entities list for modification later
     *
     * @return the newly created entity
     */
    public Entity createEntity(Entity template) {
        Entity entity = entityPool.obtain();
        entity.setManager(this);
        entity.copyFrom(template);
        activeEntities.add(entity);
        return entity;
    }

    /**
     * Removes the entity from the active entity list and adds it back to the entity pool for later use
     * @param entity The entity that is no longer in use
     */
    public void freeEntity(Entity entity) {
        entity.removeAllComponents();
        activeEntities.remove(entity);
        entityPool.free(entity);
    }

    /**
     * @return the list of active entities on this manager
     */
    public List<Entity> getEntities() {
        return activeEntities;
    }

    /**
     * Gets all entities that match the provided filter
     * @param filter the filter for which you want to get matching entities
     * @return a List of entities that match the filter provided
     */
    //TODO implement some sort of cache for common returned lists of entities by component filter and update those lists with the master list for future use
    public List<Entity> getMatchingEntities(ComponentFilter filter) {
        if (filter == null) return Collections.emptyList();
        return filter.filter(activeEntities);
    }

    /**
     * Gets all entities that match the provided filter
     * @param filter the filter for which you want to get matching entities
     * @return a List of entities that match the filter provided
     */
    public Entity getFirstMatchingEntity(ComponentFilter filter) {
        return filter.filter(activeEntities).get(0);
    }

    /**
     * @param componentClass the class of the entity you want to retrieve
     * @param <T> and class that extends component
     * @return the first matching component to the class you requested
     */
    public <T extends Component> T getComponentOfFirstMatchingEntity(Class<T> componentClass) {
        return getFirstMatchingEntity(ComponentFilter.builder().oneOf(componentClass).build()).getComponent(componentClass);
    }

    /**
     * Gets the entity in this Manager with the specified ID if it exists
     *
     * @param id the UUID of this entity (you can use UUID.fromString() to get this if you only have a string)
     * @return the entity requested or null
     */
    public Entity getEntityWithID(UUID id) {
        for (Entity entity : activeEntities) {
            if (entity.getID().equals(id)) return entity;
        }
        return null;
    }

    /**
     * Creates and registers a system of class type specified
     *
     * @param systemClass The class for the type of system you wish to create
     * @param <T> The class for the type of system you want to create
     * @return The system you just created
     */
    public <T extends System> T createSystem(Class<T> systemClass) {
        try {
            T system = ClassUtils.createInstance(systemClass);
            systems.put(systemClass, system);
            return system;
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param systemClass The class which represents which system we want to retrieve
     * @return the instance in this manager of the requested system
     */
    public System getSystem(Class<? extends System> systemClass) {
        return systems.get(systemClass);
    }
}
