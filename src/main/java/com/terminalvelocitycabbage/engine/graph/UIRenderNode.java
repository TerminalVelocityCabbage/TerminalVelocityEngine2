package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshCache;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.ui.*;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.templates.meshes.SquareDataMesh;

import static org.lwjgl.opengl.GL11.*;

public abstract class UIRenderNode extends RenderNode {

    static final Identifier ROOT_ELEMENT_IDENTIFIER = ClientBase.getInstance().identifierOf("root");
    static final Projection PROJECTION = new Projection(Projection.Type.ORTHOGONAL, 0, 100);
    MeshCache meshCache;
    Registry<Element> elementRegistry;

    public static final VertexFormat UI_ELEMENT_MESH_FORMAT = VertexFormat.builder()
            .addElement(VertexAttribute.XYZ_POSITION)
            .addElement(VertexAttribute.RGB_COLOR)
            .addElement(VertexAttribute.UV)
            .build();
    static final Mesh QUAD_MESH = new Mesh(UI_ELEMENT_MESH_FORMAT, new SquareDataMesh());

    UIContext context;

    public UIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    public void init(RenderGraph renderGraph) {
        super.init(renderGraph);

        //Collect all elements used in this UI so that the textures and meshes can be cached
        this.elementRegistry = new Registry<>();
        var rootElement = new Element(null, new ContainerLayout(new Layout.Dimension(0, Layout.Unit.PIXELS), new Layout.Dimension(0, Layout.Unit.PIXELS), ContainerLayout.Anchor.CENTER_CENTER, ContainerLayout.PlacementDirection.CENTERED), Style.builder().build());
        elementRegistry.register(ROOT_ELEMENT_IDENTIFIER, rootElement);
        registerUIElements(new ElementRegistry(elementRegistry));

        //Create a mesh cache so that textures can be sampled from the UI atlas
        Registry<Mesh> meshRegistry = new Registry<>();
        //All meshes that are provided by the UIRenderNode should be registered to the mesh registry here
        var quadMesh = ClientBase.getInstance().identifierOf("quad");
        meshRegistry.register(quadMesh, QUAD_MESH);
        //Pair up meshes with textures here based on all textures used in element styles
        Registry<Model> modelRegistry = new Registry<>();
        elementRegistry.getRegistryContents().forEach((identifier, element) -> {
            var texture = element.getStyle().getTextureIdentifier();
            //TODO this will eventually depend on what type of texture it is using, eventually we will have textures with unique edge, corner, and middle conditions
            if (texture != null) modelRegistry.register(identifier, new Model(quadMesh, texture));
        });
        //Generate the mesh cache from the paired meshes and textures (models)
        this.meshCache = new MeshCache(modelRegistry, meshRegistry, ClientBase.getInstance().getTextureCache());
    }

    public abstract void registerUIElements(ElementRegistry elementRegistry);

    public abstract void drawUIElements(Scene scene);

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        //Set the shader up for rendering
        var shaderProgram = getShaderProgram();
        if (properties.isResized()) PROJECTION.updateProjectionMatrix(properties.getWidth(), properties.getHeight());
        shaderProgram.bind();
        shaderProgram.getUniform("textureSampler").setUniform(0);
        shaderProgram.getUniform("projectionMatrix").setUniform(PROJECTION.getProjectionMatrix());

        elementRegistry.get(ROOT_ELEMENT_IDENTIFIER).getLayout().setDimensions(properties.getWidth(), properties.getHeight());
        context = new UIContext(properties);
        context.setPreviousContainer(null);
        context.setPreviousElement(null);
        context.setCurrentContainer(ROOT_ELEMENT_IDENTIFIER);
        context.setCurrentElement(null);

        Log.info("Rendering UI -------------------------");
        if (glIsEnabled(GL_DEPTH_TEST)) {
            glDisable(GL_DEPTH_TEST);
            drawUIElements(scene);
            glEnable(GL_DEPTH_TEST);
        } else {
            drawUIElements(scene);
        }

        //Reset
        shaderProgram.unbind();
    }

    public boolean startContainer(Identifier elementIdentifier) {

        Log.info(elementIdentifier + " started");

        context.setPreviousElement(null);
        context.setCurrentElement(null);
        context.setPreviousContainer(context.getCurrentContainer());
        var current = elementRegistry.get(elementIdentifier);
        current.setParent(context.getCurrentContainer());
        current.getLayout().computeDimensions(elementRegistry.get(context.getCurrentContainer()).getLayout());
        context.setCurrentContainer(elementIdentifier);

        return true;
    }

    public void endContainer() {

        Log.info(context.getCurrentContainer() + " ended");
        var thisElement = elementRegistry.get(context.getCurrentContainer());
        var thisLayout = (ContainerLayout) thisElement.getLayout();
        var style = thisElement.getStyle();

        if (style.getTextureIdentifier() != null) {
            ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier()).bind();
        }

        var previousParentContainer = elementRegistry.get(context.getPreviousContainer()).getParent();
        shaderProgram.getUniform("modelMatrix").setUniform(thisLayout.getTransformationMatrix(elementRegistry.get(context.getPreviousContainer()).getLayout()));

        meshCache.getMesh(context.getCurrentContainer()).render();

        context.setPreviousElement(context.getCurrentContainer());
        context.setCurrentElement(null);
        Log.info("setting current container to " + context.getPreviousContainer());
        Log.info("setting previous container to " + previousParentContainer);
        context.setCurrentContainer(context.getPreviousContainer());
        context.setPreviousContainer(previousParentContainer == null ? context.getPreviousContainer() : previousParentContainer);

    }

    public boolean drawBox(Identifier elementIdentifier) {

        var thisElement = elementRegistry.get(elementIdentifier);
        var layout = thisElement.getLayout();
        var style = thisElement.getStyle();

        if (style.getTextureIdentifier() != null) {
            var texture = ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier());
            texture.bind();
        }

        shaderProgram.getUniform("modelMatrix").setUniform(layout.getTransformationMatrix(elementRegistry.get(context.getCurrentContainer()).getLayout()));

        meshCache.getMesh(elementIdentifier).render();

        context.setPreviousElement(context.getCurrentElement());
        context.setCurrentElement(elementIdentifier);

        return true;
    }

    public static class ElementRegistry extends Registry<Element> {

        Registry<Element> elementRegistry;

        public ElementRegistry(Registry<Element> elementRegistry) {
            this.elementRegistry = elementRegistry;
        }

        public Identifier registerElement(Identifier elementID, Layout layout, Style style) {
            return elementRegistry.register(elementID, new Element(null, layout, style)).getIdentifier();
        }
    }

}
