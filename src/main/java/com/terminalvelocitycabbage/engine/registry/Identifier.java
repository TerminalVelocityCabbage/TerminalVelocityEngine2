package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.Objects;

/**
 * A class which represents an identifier to a registered resource on the virtual filesystem
 */
public record Identifier(
		String namespace,   //The source or origin of this element (tve/minecraft/etc)
		String type,        //The type of this element (registry/resource/asset/etc)
		String category,    //The category of this element (font/model/etc)
		String name         //The actual name of this element (horse/arial/etc)
) {
	
	/**
	 * @param identifierString The string in format namespace:path to convert to an Identifier
	 * @return an identifier with the same format
	 */
	public static Identifier fromString(String identifierString) {
		if (isValid(identifierString)) Log.error("Tried to parse invalid identifier string: " + identifierString);
		var split = identifierString.split(":");
		return new Identifier(split[0], split[1], split[2], split[3]);
	}

    /**
     * @param identifierString The identifier string to check for validity
     * @return true if the identifier string is valid, false otherwise
     */
	public static boolean isValid(String identifierString) {
		var split = identifierString.split(":");
		if (split.length != 4) return false;
		return true;
	}

	@Override
	public String toString() {
		return namespace + ':' + type + ":" + category + ":" + name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Identifier(String namespace1, String status1, String category1, String name1))) return false;
        return Objects.equals(name(), name1) && Objects.equals(type(), status1) && Objects.equals(category(), category1) && Objects.equals(namespace(), namespace1);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace(), type(), category(), name());
	}
}
