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
import com.terminalvelocitycabbage.engine.client.ui.Element;
import com.terminalvelocitycabbage.engine.client.ui.Layout;
import com.terminalvelocitycabbage.engine.client.ui.Style;
import com.terminalvelocitycabbage.engine.client.ui.UIContext;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
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
        this.elementRegistry = new Registry<>();
        var rootElement = new Element(
                null,
                new Layout(
                        new Layout.Dimension(0, Layout.Unit.PIXELS),
                        new Layout.Dimension(0, Layout.Unit.PIXELS),
                        Layout.Anchor.CENTER_CENTER, Layout.PlacementDirection.CENTER_CENTER),
                Style.builder().build());
        elementRegistry.register(ROOT_ELEMENT_IDENTIFIER, rootElement);
        registerUIElements(new ElementRegistry(elementRegistry));
        var quadMesh = ClientBase.getInstance().identifierOf("quad");
        Registry<Mesh> meshRegistry = new Registry<>();
        meshRegistry.register(quadMesh, QUAD_MESH);
        Registry<Model> modelRegistry = new Registry<>();
        elementRegistry.getRegistryContents().forEach((identifier, element) -> {
            var texture = element.style().getTextureIdentifier();
            if (texture != null) modelRegistry.register(identifier, new Model(quadMesh, texture));
        });
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

        elementRegistry.get(ROOT_ELEMENT_IDENTIFIER).layout().setDimensions(properties.getWidth(), properties.getHeight());
        context = new UIContext(properties);
        context.setPreviousContainer(null);
        context.setPreviousElement(null);
        context.setCurrentContainer(ROOT_ELEMENT_IDENTIFIER);
        context.setCurrentElement(ROOT_ELEMENT_IDENTIFIER);

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

    public void startContainer(Identifier elementIdentifier) {
        context.setPreviousElement(null);
        context.setCurrentElement(null);
        context.setPreviousContainer(context.getCurrentContainer());
        context.setCurrentContainer(elementIdentifier);
    }

    public void endContainer() {

        var thisElement = elementRegistry.get(context.getCurrentContainer());
        var thisLayout = thisElement.layout();
        var style = thisElement.style();

        if (style.getTextureIdentifier() != null) {
            ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier()).bind();
        }

        shaderProgram.getUniform("modelMatrix").setUniform(thisLayout.getTransformationMatrix(elementRegistry.get(context.getCurrentContainer()).layout()));

        meshCache.getMesh(context.getCurrentContainer()).render();

        context.setPreviousElement(context.getCurrentContainer());
        context.setCurrentElement(null);
        context.setPreviousContainer(elementRegistry.get(context.getPreviousContainer()).parent());
        context.setCurrentContainer(context.getPreviousContainer());

    }

    public void drawBox(Identifier elementIdentifier) {

        var thisElement = elementRegistry.get(elementIdentifier);
        var layout = thisElement.layout();
        var style = thisElement.style();

        if (style.getTextureIdentifier() != null) {
            var texture = ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier());
            texture.bind();
        }

        shaderProgram.getUniform("modelMatrix").setUniform(layout.getTransformationMatrix(elementRegistry.get(context.getCurrentContainer()).layout()));

        meshCache.getMesh(elementIdentifier).render();

        context.setPreviousElement(context.getCurrentElement());
        context.setCurrentElement(elementIdentifier);

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
