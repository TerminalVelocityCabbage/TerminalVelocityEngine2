package com.terminalvelocitycabbage.engine.client.scene;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshCache;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A scene is a collection of entities and instructions on how to render those entities. In addition to these entities
 * a scene may define some global values that don't necessarily belong to an entity in the scene. These could be
 * variables that the renderer needs like atmospheric changes or something of this sort.
 */
public class Scene {

    private final Identifier renderGraph;
    private final List<Routine> routines;
    private MeshCache meshCache;
    private final List<Identifier> inputControllers;
    private final List<Identifier> textureAtlases;
    private final List<Consumer<Manager>> entityInitializers;
    private final Consumer<Scene> initAction;
    private final Consumer<Scene> cleanupAction;

    protected Scene(Identifier renderGraph, List<Routine> routines, List<Identifier> inputControllers,
                 List<Identifier> textureAtlases, List<Consumer<Manager>> entityInitializers,
                 Consumer<Scene> initAction, Consumer<Scene> cleanupAction) {
        this.renderGraph = renderGraph;
        this.routines = routines;
        this.inputControllers = inputControllers;
        this.textureAtlases = textureAtlases;
        this.entityInitializers = entityInitializers;
        this.initAction = initAction;
        this.cleanupAction = cleanupAction;
    }

    /**
     * The init method is called when a scene is set to be the active scene, in this method you should register the
     * entities that belong to this scene, this could be level information or the UI etc.
     */
    public void init() {
        ClientBase client = ClientBase.getInstance();
        Manager manager = client.getManager();

        // Generate texture atlases
        textureAtlases.forEach(client.getTextureCache()::generateAtlas);

        // Create Mesh Cache
        setMeshCache(new MeshCache(client.getModelRegistry(), client.getMeshRegistry(), client.getTextureCache()));

        // Create Entities
        entityInitializers.forEach(initializer -> initializer.accept(manager));

        if (initAction != null) {
            initAction.accept(this);
        }
    }

    /**
     * This method is called when this scene is replaced with another scene or when the game closes (if this is the
     * active scene when this takes place)
     */
    public void cleanup() {
        ClientBase client = ClientBase.getInstance();

        if (cleanupAction != null) {
            cleanupAction.accept(this);
        }

        // Cleanup mesh cache
        if (meshCache != null) {
            meshCache.cleanup();
        }

        // Cleanup texture atlases
        textureAtlases.forEach(client.getTextureCache()::cleanupAtlas);
    }

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

    public void setMeshCache(MeshCache meshCache) {
        this.meshCache = meshCache;
    }

    public MeshCache getMeshCache() {
        return meshCache;
    }

    /**
     * Adds the specified input controller to this scene's list of input controllers
     * @param controllers The identifier of the input controller to add
     */
    public void addInputControllers(Identifier... controllers) {
        inputControllers.addAll(Arrays.asList(controllers));
    }

    /**
     * @return The list of identifiers that point to the input controllers used by this scene
     */
    public List<Identifier> getInputControllers() {
        return inputControllers;
    }

    /**
     * @return The list of identifiers that point to the texture atlases used by this scene
     */
    public List<Identifier> getTextureAtlases() {
        return textureAtlases;
    }

    /**
     * @return The list of entity initializers for this scene
     */
    public List<Consumer<Manager>> getEntityInitializers() {
        return entityInitializers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier renderGraph;
        private final List<Routine> routines = new ArrayList<>();
        private final List<Identifier> inputControllers = new ArrayList<>();
        private final List<Identifier> textureAtlases = new ArrayList<>();
        private final List<Consumer<Manager>> entityInitializers = new ArrayList<>();
        private Consumer<Scene> initAction;
        private Consumer<Scene> cleanupAction;

        public Builder renderGraph(Identifier renderGraph) {
            this.renderGraph = renderGraph;
            return this;
        }

        public Builder routines(Routine... routines) {
            this.routines.addAll(Arrays.asList(routines));
            return this;
        }

        public Builder inputControllers(Identifier... controllers) {
            this.inputControllers.addAll(Arrays.asList(controllers));
            return this;
        }

        public Builder textureAtlases(Identifier... atlases) {
            this.textureAtlases.addAll(Arrays.asList(atlases));
            return this;
        }

        public Builder entities(Consumer<Manager> entityInitializer) {
            this.entityInitializers.add(entityInitializer);
            return this;
        }

        public Builder onInit(Consumer<Scene> initAction) {
            this.initAction = initAction;
            return this;
        }

        public Builder onCleanup(Consumer<Scene> cleanupAction) {
            this.cleanupAction = cleanupAction;
            return this;
        }

        public Scene build() {
            return new Scene(renderGraph, routines, inputControllers, textureAtlases, entityInitializers, initAction, cleanupAction);
        }
    }
}
