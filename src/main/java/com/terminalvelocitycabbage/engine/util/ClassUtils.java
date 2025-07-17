package com.terminalvelocitycabbage.engine.util;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

	public static <T> T createInstance(Class<T> clazz, Object param) throws ReflectionException {
		try {
			return clazz.getDeclaredConstructor(param.getClass()).newInstance(param);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not instantiate instance of class: " + clazz.getName());
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz) throws ReflectionException {
		try {
			return clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not fond constructor for class: " + clazz.getName());
		}
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz) throws ReflectionException {
		try {
			return clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(e, "Could not fond constructor for class: " + clazz.getName());
		}
	}

	public static <T> Constructor<T> findConstructor(Class<T> clazz) {
		try {
			return getConstructor(clazz);
		} catch (ReflectionException e) {
			try {
				Constructor<T> constructor = getDeclaredConstructor(clazz);
				constructor.setAccessible(true);
				return constructor;
			} catch (ReflectionException ex) {
				return null;
			}
		}
	}

	public static Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
		Set<String> classNames = new HashSet<>();
		try (JarFile jarFile = new JarFile(givenFile)) {
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName()
							.replace("/", ".")
							.replace(".class", "");
					classNames.add(className);
				}
			}
			return classNames;
		}
	}

	public static Set<Class<?>> getClassesFromJarFile(File jarFile) throws IOException, ClassNotFoundException {
		Set<String> classNames = getClassNamesFromJarFile(jarFile);
		Set<Class<?>> classes = new HashSet<>(classNames.size());
		try (URLClassLoader cl = URLClassLoader.newInstance(
				new URL[] { new URL("jar:file:" + jarFile + "!/") })) {
			for (String name : classNames) {
				Class<?> clazz = cl.loadClass(name); // Load the class by its name
				classes.add(clazz);
			}
		}
		return classes;
	}

}
