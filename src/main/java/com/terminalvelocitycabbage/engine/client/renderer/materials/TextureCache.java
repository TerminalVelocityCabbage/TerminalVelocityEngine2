package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.debug.Log;
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

    public TextureCache(Map<Identifier, Texture> textures) {
        this.textures = textures;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param identifier The identifier for the texture you wish to retrieve from this cache
     * @return The requested texture or null (in the future this will return a default texture)
     */
    public Texture getTexture(Identifier identifier) {
        if (textures.containsKey(identifier)) {
            return textures.get(identifier);
        }
        return textures.get(null); //TODO replace null with default texture
    }

    /**
     * cleans up all textures
     */
    public void cleanup() {
        textures.values().forEach(Texture::cleanup);
    }

    public static class Builder {

        private final Map<Identifier, Map<Identifier, Resource>> texturesToCompileToAtlas;
        private final Map<Identifier, Resource> singleTextures;

        private final Map<Identifier, Texture> textures;

        public Builder() {
            texturesToCompileToAtlas = new HashMap<>();
            textures = new HashMap<>();
            singleTextures = new HashMap<>();
        }

        public Builder addAtlas(Identifier atlasId) {
            texturesToCompileToAtlas.putIfAbsent(atlasId, new HashMap<>());
            return this;
        }

        public Builder addTexture(Identifier textureId, Resource textureResource) {
            singleTextures.putIfAbsent(textureId, textureResource);
            return this;
        }

        public Builder addTexture(Identifier textureId, Resource textureResource, Identifier... atlasId) {
            for (Identifier identifier : atlasId) {
                if (!texturesToCompileToAtlas.containsKey(identifier)) {
                    Log.crash("Atlas with ID " + identifier + " does not exist on this TextureCache builder, use addAtlas() to add an atlas before trying to add a texture to it");
                }
                texturesToCompileToAtlas.get(identifier).put(textureId, textureResource);
            }
            return this;
        }

        public TextureCache build() {

            //Add single textures
            singleTextures.forEach((textureId, textureResource) -> {
                if (textures.containsKey(textureId)) Log.warn("Overriding texture " + textureId + " with single texture, likely due to a duplicate texture identifier");
                textures.put(textureId, new SingleTexture(textureId, textureResource));
            });
            //Create Atlases
            texturesToCompileToAtlas.forEach((atlasId, textures) -> {
                var atlas = new Atlas(textures);
                for (Identifier textureId : textures.keySet()) {
                    if (textures.containsKey(textureId)) Log.warn("Overriding texture " + textureId + " with atlas texture, likely due to a duplicate texture identifier");
                    this.textures.put(textureId, atlas);
                }
            });

            return new TextureCache(textures);
        }
    }
}
