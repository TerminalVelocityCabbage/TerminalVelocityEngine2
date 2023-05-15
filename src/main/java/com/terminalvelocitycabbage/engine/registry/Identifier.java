package com.terminalvelocitycabbage.engine.registry;

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
		if (parts.length == 2) {
			return new Identifier(parts[0], parts[1]);
		} else {
			throw new RuntimeException("Invalid identifier name " + identifierString + " it must be in format namespace:path");
		}
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

	@Override
	public String toString() {
		return namespace + ':' + name;
	}
}
