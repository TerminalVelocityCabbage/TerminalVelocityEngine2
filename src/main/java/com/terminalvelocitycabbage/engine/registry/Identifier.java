package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.Objects;

/**
 * A class which represents an identifier to a registered resource on the virtual filesystem
 */
public class Identifier {

	private final String namespace; //The source of this resource
	private final String name; //The item itself

	/**
	 * @param namespace The source of this resource
	 * @param name The name of the item we are identifying
	 */
	public Identifier(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	/**
	 * @param identifierString The string in format namespace:path to convert to an Identifier
	 * @return an identifier with the same format
	 */
	public static Identifier of(String identifierString) {
		var parts = identifierString.split(":");
		if (parts.length != 2) Log.crash("Invalid identifier name " + identifierString + " it must be in format namespace:path");
		return new Identifier(parts[0], parts[1]);
	}

	/**
	 * @return the namespace of this identifier
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return this identified item's name
	 */
	public String getName() {
		return name;
	}

	public static boolean isValid(String identifierString) {
		if (identifierString.isEmpty()) return false;
		if (!identifierString.contains(":")) return false;
		if (identifierString.split(":").length != 2) return false;
		return true;
	}

	@Override
	public String toString() {
		return namespace + ':' + name;
	}

	public boolean equalsString(String identifier) {
		return this.toString().equals(identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return getNamespace().equals(that.getNamespace()) && getName().equals(that.getName());
	}
}
