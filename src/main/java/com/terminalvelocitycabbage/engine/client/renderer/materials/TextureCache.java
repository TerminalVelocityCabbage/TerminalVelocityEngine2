package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a cache that stores all textures that a game uses until we migrate to using a texture array so
 * all textures are at the ready
 */
public class TextureCache {

    //The list of textures that this cache holds
    private final Map<Identifier, Map<Identifier, Resource>> texturesToCompileToAtlas;

    private final Map<Identifier, Resource> singleTextures;
    private final Map<Identifier, Texture> generatedTextures;
    private final Map<Identifier, Texture> fontAtlasTextures;

    public TextureCache(Map<Identifier, Map<Identifier, Resource>> texturesToCompileToAtlas, Map<Identifier, Resource> singleTextures) {
        this.texturesToCompileToAtlas = texturesToCompileToAtlas;

        this.singleTextures = singleTextures;
        this.generatedTextures = new HashMap<>();
        this.fontAtlasTextures = new HashMap<>();
    }

    public void generateAtlas(Identifier atlasIdentifier) {
        var textures = texturesToCompileToAtlas.get(atlasIdentifier);
        var atlas = new Atlas(textures);
        for (Identifier textureId : textures.keySet()) {
            this.generatedTextures.put(textureId, atlas);
        }
    }

    public void generateFontAtlas(Identifier fontIdentifier, int[] glyphIds) {
        Log.info("Generating font atlas for " + fontIdentifier);
        this.fontAtlasTextures.put(fontIdentifier, new FontAtlas(ClientBase.getInstance().getFontRegistry().get(fontIdentifier), glyphIds));
    }

    /**
     * @param texture The identifier for the texture you wish to retrieve from this cache
     * @return The requested texture or null (in the future this will return a default texture)
     */
    public Texture getTexture(Identifier texture) {
        if (generatedTextures.containsKey(texture)) {
            return generatedTextures.get(texture);
        } else if (singleTextures.containsKey(texture)) {
            //TODO add generateTexture method to do this at scene init for games to optimize this
            //TODO add a way to see if a texture is generated then generate it off thread with default texture like Rust skins
            var generatedTexture = new SingleTexture(texture, singleTextures.get(texture));
            generatedTextures.put(texture, generatedTexture);
            return generatedTexture;
        } else {
            Log.warn("Texture " + texture + " not found in cache, returning default texture");
            return generatedTextures.get(null); //TODO replace null with default texture
        }
    }

    /**
     * Cleans up the given atlas (frees all memory associated from the gpu)
     * @param atlasIdentifier The atlas that needs to be cleaned up
     */
    public void cleanupAtlas(Identifier atlasIdentifier) {
        boolean clean = false;
        for (Identifier textureId : texturesToCompileToAtlas.get(atlasIdentifier).keySet()) {
            if (!clean) {
                generatedTextures.get(textureId).cleanup();
                clean = true;
            }
            generatedTextures.remove(textureId);
        }
    }

    /**
     * cleans up all textures
     */
    public void cleanup() {
        generatedTextures.values().forEach(Texture::cleanup);
    }
}
