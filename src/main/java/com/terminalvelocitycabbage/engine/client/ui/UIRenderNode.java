package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.ui.data.*;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.ImageElementConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.TextElementConfig;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Base class for UI render nodes.
 * Users should extend this class and implement {@link #declareUI()} to define their interface.
 */
public abstract class UIRenderNode extends RenderNode implements UILayoutEngine.TextMeasurer {

    public UIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        UIContext context = getUIContext();
        context.beginFrame(properties.getWidth(), properties.getHeight());
        
        // 1. Declare UI
        declareUI();
        context.closeElement(); // Close the window root
        
        // 2. Layout
        UILayoutEngine layoutEngine = new UILayoutEngine(this);
        layoutEngine.runLayout(context.getRootElement(), properties.getWidth(), properties.getHeight());
        
        // 3. Store layout for next frame queries
        Map<Integer, UIElementData> nextFrameData = new HashMap<>();
        collectElementData(context.getRootElement(), nextFrameData);
        context.endFrame(nextFrameData);
        
        // 4. Render
        long nvg = ClientBase.getInstance().getNvgContext();
        nvgBeginFrame(nvg, properties.getWidth(), properties.getHeight(), 1.0f); // TODO devicePixelRatio
        renderElement(nvg, context.getRootElement());
        nvgEndFrame(nvg);
    }

    private void collectElementData(LayoutElement element, Map<Integer, UIElementData> dataMap) {
        if (element == null) return;
        dataMap.put(element.id(), new UIElementData(new BoundingBox(new Vector2f(element.getPosition()), new Vector2f(element.getSize())), true));
        for (LayoutElement child : element.children()) {
            collectElementData(child, dataMap);
        }
    }

    private void renderElement(long nvg, LayoutElement element) {

        Log.debug("Rendering element " + element.id());
        
        if (element.isText()) {
            renderText(nvg, element);
        } else {
            renderContainer(nvg, element);
            for (LayoutElement child : element.children()) {
                renderElement(nvg, child);
            }
        }
    }

    private void renderContainer(long nvg, LayoutElement element) {

        ElementDeclaration decl = element.declaration();
        if (decl == null) return;
        
        if (decl.backgroundColor() != null) {
            try (MemoryStack stack = stackPush()) {
                nvgBeginPath(nvg);
                if (decl.cornerRadius() != null) {
                    nvgRoundedRect(nvg, element.getX(), element.getY(), element.getWidth(), element.getHeight(), decl.cornerRadius().topLeft()); // TODO: individual corners
                } else {
                    nvgRect(nvg, element.getX(), element.getY(), element.getWidth(), element.getHeight());
                }
                NVGColor color = NVGColor.malloc(stack);
                nvgRGBAf(decl.backgroundColor().r(), decl.backgroundColor().g(), decl.backgroundColor().b(), decl.backgroundColor().a(), color);
                nvgFillColor(nvg, color);
                nvgFill(nvg);
            }
        }
        
        // TODO: render borders, images, etc.
    }

    private void renderText(long nvg, LayoutElement element) {
        TextElementConfig config = element.textConfig();
        if (config == null) return;
        
        try (MemoryStack stack = stackPush()) {
            nvgFontSize(nvg, config.fontSize());
            if (config.fontIdentifier() != null) {
                ClientBase.getInstance().getFontRegistry().get(config.fontIdentifier()).getOrLoadFont(nvg);
                nvgFontFace(nvg, config.fontIdentifier().toString());
            }
            nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP); // TODO: config alignment
            NVGColor color = NVGColor.malloc(stack);
            if (config.textColor() != null) {
                nvgRGBAf(config.textColor().r(), config.textColor().g(), config.textColor().b(), config.textColor().a(), color);
            } else {
                nvgRGBAf(1, 1, 1, 1, color); // Default white
            }
            nvgFillColor(nvg, color);
            nvgText(nvg, element.getX(), element.getY(), element.text());
        }
    }

    @Override
    public Vector2f measureText(String text, TextElementConfig config) {
        long nvg = ClientBase.getInstance().getNvgContext();
        nvgFontSize(nvg, config.fontSize());
        if (config.fontIdentifier() != null) {
            ClientBase.getInstance().getFontRegistry().get(config.fontIdentifier()).getOrLoadFont(nvg);
            nvgFontFace(nvg, config.fontIdentifier().toString());
        }
        float[] bounds = new float[4];
        nvgTextBounds(nvg, 0, 0, text, bounds);
        return new Vector2f(bounds[2] - bounds[0], bounds[3] - bounds[1]);
    }

    protected UIContext getUIContext() {
        return ClientBase.getInstance().getUIContext();
    }

    /**
     * Hook for users to define the UI structure.
     */
    protected abstract void declareUI();

    // --- UI Declaration API ---

    /**
     * Starts an element declaration with the given configuration and child elements.
     * @param declaration The configuration for this element.
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement container(ElementDeclaration declaration, Runnable children) {
        return container(getUIContext().generateAutoId(), declaration, children);
    }

    /**
     * Starts an element declaration with a specific ID.
     * @param id The ID for this element, typically generated via {@link #id(String)}.
     * @param declaration The configuration for this element.
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement container(int id, ElementDeclaration declaration, Runnable children) {
        getUIContext().openElement(id, declaration);
        if (children != null) {
            children.run();
        }
        getUIContext().closeElement();
        return new UIElement(id, getUIContext());
    }

    /**
     * Declares a text element.
     * @param text The string to display.
     * @param config The configuration for the text.
     * @return A handle to the declared element.
     */
    protected UIElement text(String text, TextElementConfig config) {
        int id = getUIContext().generateAutoId();
        getUIContext().addTextElement(id, text, config);
        return new UIElement(id, getUIContext());
    }

    /**
     * Declares an image element.
     * @param config The configuration for the image.
     * @return A handle to the declared element.
     */
    protected UIElement image(ImageElementConfig config) {
        return container(ElementDeclaration.builder().image(config).build(), null);
    }

    /**
     * Generates a stable integer ID from a string label.
     * @param label The label to hash.
     * @return A hashed ID.
     */
    protected int id(String label) {
        return getUIContext().hashString(label);
    }

    // --- Query API ---

    /**
     * Checks if the element with the given ID is currently hovered by the pointer.
     * @param id The ID of the element to check.
     * @return true if hovered, false otherwise.
     */
    protected boolean isHovered(int id) {
        UIElementData data = getUIContext().getElementData(id);
        if (!data.found()) return false;

        BoundingBox bb = data.boundingBox();
        double mx = ClientBase.getInstance().getInputCallbackListener().getMouseX();
        double my = ClientBase.getInstance().getInputCallbackListener().getMouseY();

        return mx >= bb.position().x && mx <= bb.position().x + bb.size().x && my >= bb.position().y && my <= bb.position().y + bb.size().y;
    }

    /**
     * Checks if the element with the given ID was clicked this frame.
     * @param id The ID of the element to check.
     * @return true if clicked, false otherwise.
     */
    protected boolean isClicked(int id) {
        // TODO implementation - needs integration with InputHandler button states
        return isHovered(id) && false; // placeholder
    }

}
