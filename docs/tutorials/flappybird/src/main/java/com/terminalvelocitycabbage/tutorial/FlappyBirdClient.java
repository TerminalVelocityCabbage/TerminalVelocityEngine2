package com.terminalvelocitycabbage.tutorial;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.controller.BooleanController;
import com.terminalvelocitycabbage.engine.client.input.controller.Controller;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import com.terminalvelocitycabbage.engine.client.input.types.KeyboardInput;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshCache;
import com.terminalvelocitycabbage.engine.client.renderer.shader.Shader;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.renderer.shader.Uniform;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.sources.MainSource;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.templates.ecs.components.*;
import com.terminalvelocitycabbage.templates.events.*;
import com.terminalvelocitycabbage.templates.meshes.SquareDataMesh;

import java.util.List;

public class FlappyBirdClient extends ClientBase {

    //This client's identifier (namespace)
    public static final String ID = "flappybird";

    //Resource stuff
    public static Identifier CLIENT_RESOURCE_SOURCE;

    //Shader stuff
    public static Identifier DEFAULT_VERTEX_SHADER;
    public static Identifier DEFAULT_FRAGMENT_SHADER;
    public static ShaderProgramConfig DEFAULT_SHADER_PROGRAM_CONFIG;

    //Textures and atlases
    public static Identifier BIRD_TEXTURE;
    public static Identifier TEXTURE_ATLAS;

    //Meshes and Models
    public static Identifier SPRITE_MESH;
    public static Identifier BIRD_MODEL;

    //Renderer configs
    public static final VertexFormat MESH_FORMAT = VertexFormat.builder()
            .addElement(VertexAttribute.XYZ_POSITION)
            .addElement(VertexAttribute.UV)
            .build();
    public static Routine DEFAULT_ROUTINE;
    public static Identifier RENDER_GRAPH;
    private static Identifier DEFAULT_SCENE;

    //Entity stuff
    public static Identifier BIRD_ENTITY;
    public static Identifier PLAYER_CAMERA_ENTITY;

    public FlappyBirdClient(String namespace, int ticksPerSecond) {
        super(namespace, ticksPerSecond);
        //Listen to events
        getEventDispatcher().listenToEvent(ResourceCategoryRegistrationEvent.EVENT, e -> {
            //Register engine defaults
            ResourceCategory.registerEngineDefaults(((ResourceCategoryRegistrationEvent) e).getRegistry(), ID);
        });
        getEventDispatcher().listenToEvent(ResourceSourceRegistrationEvent.EVENT, e -> {
            //Register and init filesystem things
            //Create resource sources for this client
            ResourceSource mainSource = new MainSource(getInstance());
            //Define roots for these resources based on default resoruce categories
            mainSource.registerDefaultSources(ID);
            //register this source
            CLIENT_RESOURCE_SOURCE = ((ResourceSourceRegistrationEvent) e).registerResourceSource(ID, "main", mainSource);
        });
        getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.SHADER), e -> {
            ResourceRegistrationEvent event = (ResourceRegistrationEvent) e;
            //Register shader resources
            DEFAULT_VERTEX_SHADER = event.registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.SHADER, "default.vert").getIdentifier();
            DEFAULT_FRAGMENT_SHADER = event.registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.SHADER, "default.frag").getIdentifier();
            //Configure the shader program
            DEFAULT_SHADER_PROGRAM_CONFIG = ShaderProgramConfig.builder()
                    .vertexFormat(MESH_FORMAT)
                    .addShader(Shader.Type.VERTEX, DEFAULT_VERTEX_SHADER)
                    .addShader(Shader.Type.FRAGMENT, DEFAULT_FRAGMENT_SHADER)
                    .addUniform(new Uniform("textureSampler"))
                    .addUniform(new Uniform("projectionMatrix"))
                    .addUniform(new Uniform("viewMatrix"))
                    .addUniform(new Uniform("modelMatrix"))
                    .build();
        });
        getEventDispatcher().listenToEvent(ResourceRegistrationEvent.getEventNameFromCategory(ResourceCategory.TEXTURE), e -> {
            //Register texture resources
            BIRD_TEXTURE = ((ResourceRegistrationEvent) e).registerResource(CLIENT_RESOURCE_SOURCE, ResourceCategory.TEXTURE, "bird.png").getIdentifier();
        });
        getEventDispatcher().listenToEvent(ConfigureTexturesEvent.EVENT, e -> {
            ConfigureTexturesEvent event = (ConfigureTexturesEvent) e;
            //Register a default atlas
            TEXTURE_ATLAS = event.registerAtlas(ID, "atlas");
            //Add textures to atlas
            event.addTexture(BIRD_TEXTURE, TEXTURE_ATLAS);
        });
        getEventDispatcher().listenToEvent(MeshRegistrationEvent.EVENT, e -> {
            MeshRegistrationEvent event = (MeshRegistrationEvent) e;
            SPRITE_MESH = event.registerMesh(ID, "sprite", new Mesh(MESH_FORMAT, new SquareDataMesh()));
        });
        getEventDispatcher().listenToEvent(ModelConfigRegistrationEvent.EVENT, e -> {
            ModelConfigRegistrationEvent event = (ModelConfigRegistrationEvent) e;
            BIRD_MODEL = event.registerModel(ID, "brid", SPRITE_MESH, BIRD_TEXTURE);
        });
        getEventDispatcher().listenToEvent(EntityComponentRegistrationEvent.EVENT, e -> {
            EntityComponentRegistrationEvent event = (EntityComponentRegistrationEvent) e;
            event.registerComponent(ModelComponent.class);
            event.registerComponent(TransformationComponent.class);
            event.registerComponent(PositionComponent.class);
            event.registerComponent(FixedOrthoCameraComponent.class);
            event.registerComponent(VelocityComponent.class);
        });
        getEventDispatcher().listenToEvent(EntitySystemRegistrationEvent.EVENT, e -> {
            EntitySystemRegistrationEvent event = (EntitySystemRegistrationEvent) e;
            event.createSystem(GravitySystem.class);
            event.createSystem(AccelerationSystem.class);
        });
        getEventDispatcher().listenToEvent(EntityTemplateRegistrationEvent.EVENT, e -> {
            EntityTemplateRegistrationEvent event = (EntityTemplateRegistrationEvent) e;
            BIRD_ENTITY = event.createEntityTemplate(ID, "bird", entity -> {
                entity.addComponent(ModelComponent.class).setModel(BIRD_MODEL);
                entity.addComponent(TransformationComponent.class).setPosition(-200, 0, -2).setScale(120f);
                entity.addComponent(VelocityComponent.class).setVelocity(0, .5f, 0);
            });
            PLAYER_CAMERA_ENTITY = event.createEntityTemplate(ID, "player_camera", entity -> {
                entity.addComponent(PositionComponent.class);
                entity.addComponent(FixedOrthoCameraComponent.class);
            });
        });
        getEventDispatcher().listenToEvent(RoutineRegistrationEvent.EVENT, e -> {
            RoutineRegistrationEvent event = (RoutineRegistrationEvent) e;
            DEFAULT_ROUTINE = event.registerRoutine(Routine.builder(ID, "update_bird_positions")
                    .addStep(event.registerStep(ID, "gravity"), GravitySystem.class)
                    .addStep(event.registerStep(ID, "acceleration"), AccelerationSystem.class)
                    .build());
            Log.info(DEFAULT_ROUTINE);
        });
        getEventDispatcher().listenToEvent(RendererRegistrationEvent.EVENT, e -> {
            RendererRegistrationEvent event = (RendererRegistrationEvent) e;
            RENDER_GRAPH = event.registerGraph(ID, "render_graph",
                    new RenderGraph(RenderGraph.RenderPath.builder()
                            .addRoutineNode(DEFAULT_ROUTINE)
                            .addRenderNode(event.registerNode(ID, "draw_scene"), DrawSceneRenderNode.class, DEFAULT_SHADER_PROGRAM_CONFIG)
                    )
            );
        });
        getEventDispatcher().listenToEvent(SceneRegistrationEvent.EVENT, e -> {
            SceneRegistrationEvent event = (SceneRegistrationEvent) e;
            DEFAULT_SCENE = event.registerScene(ID, "scene", new DefaultScene(RENDER_GRAPH, List.of()));
        });
        getEventDispatcher().listenToEvent(InputHandlerRegistrationEvent.EVENT, e -> {
            InputHandlerRegistrationEvent event = (InputHandlerRegistrationEvent) e;

            var inputHandler = event.getInputHandler();
            //Register Controls
            Control exitControl = inputHandler.registerControlListener(new KeyboardKeyControl(KeyboardInput.Key.ESCAPE));
            Control flapControl = inputHandler.registerControlListener(new KeyboardKeyControl(KeyboardInput.Key.SPACE));
            //Register Controllers
            inputHandler.registerController(ID, "exit_game", new CloseGameController(exitControl));
            inputHandler.registerController(ID, "flap", new JumpController(flapControl));
        });
    }

    public static void main(String[] args) {
        FlappyBirdClient client = new FlappyBirdClient("flappybird", 60);
        client.start();
    }

    @Override
    public void init() {
        super.init();

        //Create window properties
        WindowProperties windowProperties = new WindowProperties(800, 600, "Flappy Bird", DEFAULT_SCENE);
        //Create window
        long window = getWindowManager().createNewWindow(windowProperties);
        //Focus window
        getWindowManager().focusWindow(window);
    }

    public static class DrawSceneRenderNode extends RenderNode {

        public DrawSceneRenderNode(ShaderProgramConfig shaderProgramConfig) {
            super(shaderProgramConfig);
        }

        @Override
        public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

            var client = FlappyBirdClient.getInstance();
            var player = client.getManager().getFirstEntityWith(FixedOrthoCameraComponent.class);
            var camera = player.getComponent(FixedOrthoCameraComponent.class);
            var shaderProgram = getShaderProgram();

            if (properties.isResized()) {
                camera.updateProjectionMatrix(properties.getWidth(), properties.getHeight());
            }

            shaderProgram.bind();
            shaderProgram.getUniform("textureSampler").setUniform(0);
            shaderProgram.getUniform("projectionMatrix").setUniform(camera.getProjectionMatrix());
            shaderProgram.getUniform("viewMatrix").setUniform(camera.getViewMatrix(player));


            var entities = client.getManager().getEntitiesWith(ModelComponent.class, TransformationComponent.class);

            //Render entities
            for (Entity entity : entities) {
                var modelIdentifier = entity.getComponent(ModelComponent.class).getModel();
                var model = client.getModelRegistry().get(modelIdentifier);
                var mesh = scene.getMeshCache().getMesh(modelIdentifier);
                var texture = client.getTextureCache().getTexture(model.getTextureIdentifier());
                var transformationComponent = entity.getComponent(TransformationComponent.class);

                texture.bind();
                shaderProgram.getUniform("modelMatrix").setUniform(transformationComponent.getTransformationMatrix());
                if (mesh.getFormat().equals(shaderProgram.getConfig().getVertexFormat())) mesh.render();
            }

            shaderProgram.unbind();
        }
    }

    public static class DefaultScene extends Scene {

        public DefaultScene(Identifier renderGraph, List<Routine> routines) {
            super(renderGraph, routines);
        }

        @Override
        public void init() {
            var client = FlappyBirdClient.getInstance();
            var manager = client.getManager();

            client.getTextureCache().generateAtlas(TEXTURE_ATLAS);
            setMeshCache(new MeshCache(client.getModelRegistry(), client.getMeshRegistry(), client.getTextureCache()));

            manager.createEntityFromTemplate(BIRD_ENTITY);
            manager.createEntityFromTemplate(PLAYER_CAMERA_ENTITY);
        }

        @Override
        public void cleanup() {
            var client = FlappyBirdClient.getInstance();
            client.getTextureCache().cleanupAtlas(TEXTURE_ATLAS);
            getMeshCache().cleanup();
        }
    }

    public static class GravitySystem extends System {

        private static final float GRAVITY = 9.8E-4f;

        @Override
        public void update(Manager manager, float deltaTime) {
            manager.getEntitiesWith(VelocityComponent.class).forEach(entity -> {
                entity.getComponent(VelocityComponent.class).addVelocity(0, -GRAVITY * deltaTime, 0);
            });
        }
    }

    public static class AccelerationSystem extends System {

        @Override
        public void update(Manager manager, float deltaTime) {
            manager.getEntitiesWith(VelocityComponent.class, TransformationComponent.class).forEach(entity -> {
                var velocity = entity.getComponent(VelocityComponent.class).getVelocity();
                entity.getComponent(TransformationComponent.class).translate(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
            });
        }
    }

    public static class CloseGameController extends BooleanController {

        public CloseGameController(Control... controls) {
            super(ButtonAction.PRESSED, false, controls);
        }

        @Override
        public void act() {
            if (isEnabled()) FlappyBirdClient.getInstance().getWindowManager().closeFocusedWindow();
        }
    }

    public static class JumpController extends BooleanController {

        public JumpController(Control... controls) {
            super(ButtonAction.PRESSED, false, controls);
        }

        @Override
        public void act() {
            if (isEnabled()) {
                var manager = ClientBase.getInstance().getManager();
                manager.getEntitiesWith(TransformationComponent.class, VelocityComponent.class).forEach(entity -> {
                    entity.getComponent(VelocityComponent.class).setVelocity(0, .5f, 0);
                });
            }
        }
    }
}