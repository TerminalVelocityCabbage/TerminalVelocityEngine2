package com.terminalvelocitycabbage.engine.pools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;

/**
 * A pool of objects that can be re-used when needed
 * @param <T> The type that this pool stores
 */
public abstract class TypePool<T extends Poolable> {

    //The max allowed objects in this pool
    public int maxObjects;
    //The current list of available free objects
    private List<T> freeObjects;

    /**
     * @param initialCapacity The number of objects wanted to be initialized in the freeObjects array
     * @param maxCapacity the maximum number of objects allowed in this pool
     */
    public TypePool(int initialCapacity, int maxCapacity) {
        this.freeObjects = new ArrayList<>();
        maxObjects = maxCapacity;
        for (int i = 0; i < initialCapacity; i++) {
            freeObjects.add(createObject());
        }
    }

    /**
     * @param initialCapacity The number of objects wanted to be initialized in the freeObjects array
     */
    public TypePool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * ...
     */
    public TypePool() {
        this(1);
    }

    /**
     * Creates a new object to represent the type of item in this pool
     * Needs to be implemented when this pool is created
     * @return a new instance of the type of object that this pool contains
     */
    abstract protected T createObject();

    /**
     * Creates a number of free objects in the freeObjects pool
     * @param quantity the number of objects to be created in this pool
     */
    public void fill(int quantity) {
        for (int i = 0; i < quantity; i++) {
            if (freeObjects.size() < maxObjects) {
                freeObjects.add(createObject());
            }
        }
    }

    /**
     * Clears the object pool of al free objects
     */
    public void clear() {
        freeObjects.clear();
    }

    /**
     * gets an object from this pool that is free or creates a new object if there isn't one
     * @return a free object from freeObjects in this pool
     */
    public T obtain() {
        if (freeObjects.isEmpty()) return createObject();
        return freeObjects.remove(freeObjects.size() - 1);
    }

    /**
     * frees the current item as not used
     * resets the item, and adds it to the free objects pool
     * @param item the item you wish to free
     */
    public void free(Poolable item) {
        if (item == null) return;
        if (freeObjects.size() < maxObjects) {
            item.setDefaults();
            freeObjects.add((T) item);
        }
    }

    /**
     * frees the specified items for reuse in this pool
     * @param items the items you wish to free
     */
    public void free(T... items) {
        Arrays.stream(items).forEach(this::free);
    }

    /**
     * Shrinks the number fo free objects down to the specified size, if the number is less than specified
     * nothing will happen.
     * @param maxSize the maximum number of free elements in this list
     */
    public void shrink(int maxSize) {
        freeObjects.subList(min(freeObjects.size(), maxSize), freeObjects.size() - 1).forEach(Poolable::cleanup);
        freeObjects = new ArrayList<>(freeObjects.subList(0, min(freeObjects.size(), maxSize)));
    }
}