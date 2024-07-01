package com.terminalvelocitycabbage.engine.client.renderer.materials;

import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private Map<Identifier, Texture> textures;

    public TextureCache() {
        textures = new HashMap<>();
    }

    public void createTexture(Identifier textureId) {
        textures.putIfAbsent(textureId, new Texture(textureId));
    }

    public void createTexture(Identifier textureId, Resource textureResource) {
        textures.putIfAbsent(textureId, new Texture(textureId, textureResource));
    }

    public Texture getTexture(Identifier identifier) {
        return textures.get(textures.containsKey(identifier) ? identifier : null); //TODO replace null with default texture
    }

    public void cleanup() {
        textures.values().forEach(Texture::cleanup);
    }

    public int size() {
        return textures.size();
    }

    public Map<Identifier, Texture> getTextures() {
        return textures;
    }
}
