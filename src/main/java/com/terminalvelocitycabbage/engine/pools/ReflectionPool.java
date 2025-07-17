package com.terminalvelocitycabbage.engine.pools;

import com.terminalvelocitycabbage.engine.util.ClassUtils;

import java.lang.reflect.Constructor;

public class ReflectionPool<T extends Poolable> extends TypePool<T> {

    Constructor<T> constructor;

    public ReflectionPool(Class<T> type, int initialCapacity, int maxCapacity) {
        super(initialCapacity, maxCapacity);
        setConstructorOrError(type);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, 0);
    }

    public ReflectionPool(Class<T> type) {
        this(type, 0);
    }

    /**
     * Sets the constructor for the type of object in this pool to one found by ClassUtils
     * @param type the type of object that this reflection pool stores
     */
    private void setConstructorOrError(Class<T> type) {
        constructor = ClassUtils.findConstructor(type);
        if (constructor == null) throw new RuntimeException("Could not find no-arg constructor for type: " + type.getName());
    }

    /**
     * an internal method for creating objects within this class by type using a no-arg constructor
     * @return a new object in this reflection pool
     */
    @Override
    protected T createObject() {
        try {
            T object = constructor.newInstance((Object[])null);
            object.setDefaults();
            return object;
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of: " + constructor.getDeclaringClass().getName(), e);
        }
    }
}