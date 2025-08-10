package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
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

    static final Projection PROJECTION = new Projection(Projection.Type.ORTHOGONAL, 0, 100);
    //MeshCache meshCache;
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
        this.elementRegistry = new Registry<>();
        registerUIElements(new ElementRegistry(elementRegistry));
        //TODO generate mesh cache from registered elements and their styles
        //this.meshCache = new MeshCache();
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

        context = new UIContext(properties);
        var rootElement = new Element(
                null,
                new Layout(
                        new Layout.Dimension(properties.getWidth(), Layout.Unit.PIXELS),
                        new Layout.Dimension(properties.getHeight(), Layout.Unit.PIXELS),
                        Layout.Anchor.CENTER_CENTER, Layout.PlacementDirection.CENTER_CENTER),
                Style.builder().build());
        context.setPreviousContainer(null);
        context.setPreviousElement(null);
        context.setCurrentContainer(rootElement);
        context.setCurrentElement(rootElement);

        if (glIsEnabled(GL_DEPTH_TEST)) {
            glDisable(GL_DEPTH_TEST);
            drawUIElements(scene);
            glEnable(GL_DEPTH_TEST);
        } else {
            drawUIElements(scene);
        }

        if (rootElement.style().getTextureIdentifier() != null) {
            ClientBase.getInstance().getTextureCache().getTexture(rootElement.style().getTextureIdentifier()).bind();
            shaderProgram.getUniform("modelMatrix").setUniform(rootElement.layout().getTransformationMatrix(context));
            QUAD_MESH.render();
        }

        //Reset
        shaderProgram.unbind();
    }

    public void startContainer(Identifier elementIdentifier) {

        var thisElement = elementRegistry.get(elementIdentifier);

        context.setPreviousElement(null);
        context.setCurrentElement(null);
        context.setPreviousContainer(context.getCurrentContainer());
        context.setCurrentContainer(thisElement);


    }

    public void endContainer() {

        var thisElement = context.getCurrentContainer();
        var thisLayout = thisElement.layout();
        var style = thisElement.style();

        if (style.getTextureIdentifier() != null) {
            ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier()).bind();
        }

        shaderProgram.getUniform("modelMatrix").setUniform(thisLayout.getTransformationMatrix(context));

        QUAD_MESH.render();

        context.setPreviousElement(thisElement);
        context.setCurrentElement(null);
        context.setPreviousContainer(context.getPreviousContainer().parent());
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

        shaderProgram.getUniform("modelMatrix").setUniform(layout.getTransformationMatrix(context));

        QUAD_MESH.render();

        context.setPreviousElement(context.getCurrentElement());
        context.setCurrentElement(thisElement);

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
