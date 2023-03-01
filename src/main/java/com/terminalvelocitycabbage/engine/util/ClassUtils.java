package com.terminalvelocitycabbage.engine.util;

import javax.management.ReflectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassUtils {

	public static Set<Method> getAllMethodsInHierarchy(Class<?> objectClass) {
		Set<Method> allMethods = new HashSet<>(Arrays.asList(objectClass.getDeclaredMethods()));
		if (objectClass.getSuperclass() != null) {
			allMethods.addAll(ClassUtils.getAllMethodsInHierarchy(objectClass.getSuperclass()));
		}
		return allMethods;
	}

	public static <T> T createInstance(Class<T> clazz) throws ReflectionException {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not instantiate instance of class: " + clazz.getName());
		}
	}

	public static Constructor getConstructor(Class clazz) throws ReflectionException {
		try {
			return clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not fond constructor for class: " + clazz.getName());
		}
	}

	public static Constructor getDeclaredConstructor(Class clazz) throws ReflectionException {
		try {
			return clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not fond constructor for class: " + clazz.getName());
		}
	}

	public static Constructor findConstructor(Class clazz) {
		try {
			return getConstructor(clazz);
		} catch (ReflectionException e) {
			try {
				Constructor constructor = getDeclaredConstructor(clazz);
				constructor.setAccessible(true);
				return constructor;
			} catch (ReflectionException ex) {
				return null;
			}
		}
	}

}
