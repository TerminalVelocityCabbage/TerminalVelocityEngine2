package com.terminalvelocitycabbage.engine.client.scene;

import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.List;

/**
 * A scene is a collection of entities and instructions on how to render those entities. In addition to these entities
 * a scene may define some global values that don't necessarily belong to an entity in the scene. These could be
 * variables that the renderer needs like atmospheric changes or something of this sort.
 */
public abstract class Scene {

    private Identifier renderGraph;
    private List<Routine> routines;
    private TextureCache textureCache;
    //TODO Mesh Cache

    public Scene(Identifier renderGraph, List<Routine> routines) {
        this.renderGraph = renderGraph;
        this.routines = routines;
    }

    /**
     * The init method is called when a scene is set to be the active scene, in this method you should register the
     * entities that belong to this scene, this could be level information or the UI etc.
     */
    public abstract void init();

    /**
     * An opportunity for the player to create a texture cache for the textures needed in this scene
     * @return A texture cache for this scene
     */
    public abstract TextureCache createTextureCache();

    /**
     * This method is called when this scene is replaced with another scene or when the game closes (if this is the
     * active scene when this takes place)
     */
    public abstract void cleanup();

    /**
     * @return The Identifier that points to this scenes render graph in the render graph registry
     */
    public Identifier getRenderGraph() {
        return renderGraph;
    }

    /**
     * @return The list of routines that run each update cycle on this scene
     */
    public List<Routine> getRoutines() {
        return routines;
    }

    /**
     * @param textureCache The texture cache that this scene should be rendered with
     */
    public void setTextureCache(TextureCache textureCache) {
        this.textureCache = textureCache;
    }

    /**
     * @return This scene's Texture Cache
     */
    public TextureCache getTextureCache() {
        return textureCache;
    }
}
