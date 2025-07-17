package com.terminalvelocitycabbage.engine.ecs;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.pools.MultiPool;
import com.terminalvelocitycabbage.engine.pools.ReflectionPool;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import javax.management.ReflectionException;
import java.util.*;

/**
 * The Manager is what any implementation should interact with to "manage" their entities components and systems.
 * This is where you register your systems, add your entities, and store your components.
 */
public class Manager {

    //The pool of free components
    MultiPool componentPool;
    //a map of all components and their entities
    Map<Class<? extends Component>, List<Entity>> activeComponents;

    //The pool of free entities
    ReflectionPool<Entity> entityPool;
    //The list of active entities
    Map<UUID, Entity> activeEntities;

    //The list of systems that runs on this manager
    Map<Class<? extends System>, System> systems;

    //A cache of recent queries to the manager
    Map<String, Set<Entity>> entityQueryCache;

    public Manager() {
        activeEntities = new HashMap<>();
        activeComponents = new HashMap<>();
        systems = new HashMap<>();

        componentPool = new MultiPool();
        entityPool = new ReflectionPool<>(Entity.class, 0);

        entityQueryCache = new HashMap<>();
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
        componentPool.getPool(componentType, true, initialPoolSize);
        activeComponents.put(componentType, new ArrayList<>());
    }

    /**
     * Gets a component of the type requested from the componentTypeSet
     * @param type the class of the component you wish to retrieve
     * @param <T> any component which extends {@link Component}
     * @return a component object from the componentTypeSet of the type requested.
     */
    public <T extends Component> T obtainComponent(Class<T> type, Entity obtainer) {
        if (!componentPool.hasType(type)) Log.crash("Could not retrieve pool of type " + type.getName() + " has this pool been added?", new RuntimeException("No pool exists of type " + type.getName()));
        var component = componentPool.obtain(type);
        activeComponents.get(type).add(obtainer);
        return component;
    }

    /**
     * creates a new entity and adds it to the active entities list for modification later
     *
     * @return the newly created entity
     */
    public Entity createEntity() {
        Entity entity = entityPool.obtain();
        entity.setManager(this);
        entity.setID(UUID.randomUUID());
        activeEntities.put(entity.getID(), entity);
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
        entity.setID(UUID.randomUUID());
        activeEntities.put(entity.getID(), entity);
        return entity;
    }

    /**
     * Removes the entity from the active entity list and adds it back to the entity pool for later use
     * @param entity The entity that is no longer in use
     */
    public void freeEntity(Entity entity) {
        entity.removeAllComponents();
        activeEntities.remove(entity.getID());
        entityPool.free(entity);
    }

    /**
     * @return the list of active entities on this manager
     */
    public Collection<Entity> getEntities() {
        return activeEntities.values();
    }

    @SafeVarargs
    private String generateKeyFromComponentQuery(Class<? extends Component>... componentTypes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < componentTypes.length; i++) {
            sb.append(componentTypes[i].getName());
            if (i < componentTypes.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * @param componentType The type of component that was modified on an entity and which will require a new cache generated
     */
    protected void invalidateQueryCacheForComponents(Class<? extends Component> componentType) {
        List<String> keysToRemove = new ArrayList<>();
        entityQueryCache.keySet().forEach(key -> {
            if (key.contains(componentType.getName())) keysToRemove.add(key);
        });
        keysToRemove.forEach(key -> entityQueryCache.remove(key));
    }

    /**
     * @param componentTypes a list of component types which an entity returned must have
     * @return the set of entities which match this selection
     */
    @SafeVarargs
    public final Set<Entity> getEntitiesWith(Class<? extends Component>... componentTypes) {

        //Early exit for unregistered components
        for (Class<? extends Component> componentType : componentTypes) {
            if (!componentPool.hasType(componentType)) Log.crash("No component of type found: " + componentType);
        }

        //check the cache if this query has been made before and is the same as before
        var queryKey = generateKeyFromComponentQuery(componentTypes);
        if (entityQueryCache.containsKey(queryKey)) return entityQueryCache.get(queryKey);

        //Collect all sets of entities based on the requested components
        List<Set<Entity>> entitySets = new ArrayList<>();

        for (Class<? extends Component> componentType : componentTypes) {
            List<Entity> entities = activeComponents.get(componentType);
            if (entities == null) {
                return Collections.emptySet();
            }
            entitySets.add(new HashSet<>(entities));
        }

        // Sort the sets by their size (smallest first for faster intersection)
        entitySets.sort(Comparator.comparingInt(Set::size));

        Iterator<Set<Entity>> iterator = entitySets.iterator();
        if (!iterator.hasNext()) return Collections.emptySet();

        Set<Entity> common = new HashSet<>(iterator.next());
        while (iterator.hasNext()) {
            common.retainAll(iterator.next());
            if (common.isEmpty()) {
                return Collections.emptySet();
            }
        }

        //Cache this query until it is invalidated
        entityQueryCache.put(queryKey, common);

        return common;
    }

    /**
     * @param componentTypes a list of component types which the entity returned must have
     * @return the entity which matches this selection
     */
    @SafeVarargs
    public final Entity getFirstEntityWith(Class<? extends Component>... componentTypes) {
        return getEntitiesWith(componentTypes).iterator().next();
    }

    /**
     * Gets the entity in this Manager with the specified ID if it exists
     *
     * @param id the UUID of this entity (you can use UUID.fromString() to get this if you only have a string)
     * @return the entity requested or null
     */
    public Entity getEntityWithID(UUID id) {
        return activeEntities.get(id);
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
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * @param systemClass The class which represents which system we want to retrieve
     * @return the instance in this manager of the requested system
     */
    public System getSystem(Class<? extends System> systemClass) {
        return systems.get(systemClass);
    }

    /**
     * Frees all entities which should not be persistent between scenes
     */
    public void freeNonPersistentEntities() {
        activeEntities.forEach((uuid, entity) -> {
            if(!entity.isPersistent()) freeEntity(entity);
        });
    }
}
