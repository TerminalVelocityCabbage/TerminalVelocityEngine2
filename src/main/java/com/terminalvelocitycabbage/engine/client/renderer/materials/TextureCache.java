package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a cache that stores all textures that a game uses until we migrate to using a texture array so
 * all textures are at the ready
 */
//TODO add atlases to this cache and some utility methods to get UV from model and transform it into either basic texture uvs or atlas uvs
public class TextureCache {

    //The list of textures that this cache holds
    private final Map<Identifier, Texture> textures;

    public TextureCache() {
        textures = new HashMap<>();
    }

    /**
     * @param textureId the id for the new texture
     * @param textureResource a resource pointing to the texture to create if not in the default location
     */
    public void createTexture(Identifier textureId, Resource textureResource) {
        textures.putIfAbsent(textureId, new Texture(textureId, textureResource));
    }

    /**
     * @param identifier The identifier for the texture you wish to retrieve from this cache
     * @return The requested texture or null (in the future this will return a default texture)
     */
    public Texture getTexture(Identifier identifier) {
        return textures.get(textures.containsKey(identifier) ? identifier : null); //TODO replace null with default texture
    }

    /**
     * cleans up all textures
     */
    public void cleanup() {
        textures.values().forEach(Texture::cleanup);
    }

    /**
     * @return The size of this cache
     */
    public int size() {
        return textures.size();
    }

    /**
     * @return A map of all textures and identifiers on this cache
     */
    public Map<Identifier, Texture> getTextures() {
        return textures;
    }
}
