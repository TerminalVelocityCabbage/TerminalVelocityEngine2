package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.ui.data.*;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.*;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.State;
import com.terminalvelocitycabbage.engine.util.Color;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.templates.events.UIScrollEvent;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Base class for UI render nodes.
 * Users should extend this class and implement {@link #declareUI()} to define their interface.
 */
public abstract class UIRenderNode extends RenderNode implements UILayoutEngine.TextMeasurer {

    private List<LayoutElement> sortedFloatingCache;
    private Map<Integer, Integer> declarationOrderCache;

    public UIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    public void init(RenderGraph renderGraph) {
        super.init(renderGraph);
        for (Identifier eventId : getInterestedEvents()) {
            getUIContext().listenTo(eventId);
        }
    }

    /**
     * @return The list of event identifiers that this render node is interested in.
     *         By default, includes click and scroll events.
     */
    protected abstract Identifier[] getInterestedEvents();

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        sortedFloatingCache = null;
        declarationOrderCache = null;

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

    /**
     * Hook for users to define the UI structure.
     */
    protected abstract void declareUI();

    //TODO implement tailwind style declarations
    protected UIElement container(String declaration, Runnable children) {
        //TODO replace null with string constructor of ElementDeclaration
        return container(getUIContext().generateAutoId(), (ElementDeclaration) null, children);
    }

    //TODO implement tailwind style declarations
    protected UIElement container(int id, String declaration, Runnable children) {
        //TODO replace null with string constructor of ElementDeclaration
        return container(id, (ElementDeclaration) null, children);
    }

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
     * Starts an element declaration with a specific ID label.
     * @param idLabel The label to hash for this element's ID.
     * @param declaration The configuration for this element.
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement container(String idLabel, ElementDeclaration declaration, Runnable children) {
        return container(id(idLabel), declaration, children);
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

    //TODO implement tailwind style declarations
    protected UIElement text(String text, String config) {
        //TODO replace null config passed to string constructor of TextElementConfig
        return text(text, (TextElementConfig) null);
    }

    /**
     * Declares a text element.
     * @param text The string to display.
     * @param config The configuration for the text.
     * @return A handle to the declared element.
     */
    protected UIElement text(String text, TextElementConfig config) {
        return text(getUIContext().generateAutoId(), text, config);
    }

    /**
     * Declares a text element with a specific ID label.
     * @param idLabel The label to hash for this element's ID.
     * @param text The string to display.
     * @param config The configuration for the text.
     * @return A handle to the declared element.
     */
    protected UIElement text(String idLabel, String text, TextElementConfig config) {
        return text(id(idLabel), text, config);
    }

    /**
     * Declares a text element with a specific ID.
     * @param id The ID for this element.
     * @param text The string to display.
     * @param config The configuration for the text.
     * @return A handle to the declared element.
     */
    protected UIElement text(int id, String text, TextElementConfig config) {
        getUIContext().addTextElement(id, text, config);
        return new UIElement(id, getUIContext());
    }

    //TODO implement tailwind style declarations
    protected UIElement image(String config) {
        //TODO replace null config passed to string constructor of ImageElementConfig
        return image((ImageElementConfig) null);
    }

    /**
     * Declares an image element.
     * @param config The configuration for the image.
     * @return A handle to the declared element.
     */
    protected UIElement image(ImageElementConfig config) {
        return image(getUIContext().generateAutoId(), config);
    }

    /**
     * Declares an image element with a specific ID label.
     * @param idLabel The label to hash for this element's ID.
     * @param config The configuration for the image.
     * @return A handle to the declared element.
     */
    protected UIElement image(String idLabel, ImageElementConfig config) {
        return image(id(idLabel), config);
    }

    /**
     * Declares an image element with a specific ID.
     * @param id The ID for this element.
     * @param config The configuration for the image.
     * @return A handle to the declared element.
     */
    protected UIElement image(int id, ImageElementConfig config) {
        return container(id, ElementDeclaration.builder().image(config).build(), null);
    }

    /**
     * Declares a scrollable container.
     * @param containerDecl The configuration for the container itself.
     * @param scrollbarDecl The configuration for the scrollbar (width, background, etc.).
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement scrollableContainer(ElementDeclaration containerDecl, ElementDeclaration scrollbarDecl, Runnable children) {
        return scrollableContainer(getUIContext().generateAutoId(), containerDecl, scrollbarDecl, children);
    }

    /**
     * Declares a scrollable container with a specific ID label.
     * @param idLabel The label to hash for this element's ID.
     * @param containerDecl The configuration for the container itself.
     * @param scrollbarDecl The configuration for the scrollbar (width, background, etc.).
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement scrollableContainer(String idLabel, ElementDeclaration containerDecl, ElementDeclaration scrollbarDecl, Runnable children) {
        return scrollableContainer(id(idLabel), containerDecl, scrollbarDecl, children);
    }

    /**
     * Declares a scrollable container with a specific ID.
     * @param id The ID for this element.
     * @param containerDecl The configuration for the container itself.
     * @param scrollbarDecl The configuration for the scrollbar (width, background, etc.).
     * @param children A Runnable that declares the child elements.
     * @return A handle to the declared element.
     */
    protected UIElement scrollableContainer(int id, ElementDeclaration containerDecl, ElementDeclaration scrollbarDecl, Runnable children) {
        State<Float> scrollPct = useState(id + "_scroll_pct", 0.0f);

        int contentContainerId = getUIContext().hashString(id + "_scroll_container", 0, id);
        UIElement containerElement = new UIElement(id, getUIContext());
        UIElement contentElement = new UIElement(contentContainerId, getUIContext());

        float containerHeight = containerElement.height();
        float contentHeight = contentElement.preferredHeight();

        float scrollableRange = Math.max(0, contentHeight - containerHeight);

        final float SCROLL_SPEED = 20f;

        // Handle scroll input
        if (scrollableRange > 0) {
            float totalDelta = 0;
            for (Event event : heardEvents(id, UIScrollEvent.EVENT)) {
                if (event instanceof UIScrollEvent scrollEvent) {
                    totalDelta += scrollEvent.getDelta().y * SCROLL_SPEED;
                }
            }
            if (totalDelta != 0) {
                float deltaPct = totalDelta / scrollableRange;
                scrollPct.setValue(Math.max(0f, Math.min(1f, scrollPct.getValue() - deltaPct)));
            }
        } else {
            scrollPct.setValue(0f);
        }

        float contentOffset = scrollPct.getValue() * scrollableRange;

        float scrollbarHeight = contentHeight > 0 ? (containerHeight * (containerHeight / contentHeight)) : containerHeight;
        scrollbarHeight = Math.max(Math.min(containerHeight, 20f), Math.min(containerHeight, scrollbarHeight));

        float scrollbarOffset = scrollPct.getValue() * (containerHeight - scrollbarHeight);

        var finalContainerDecl = new ElementDeclaration(
                containerDecl.layout(),
                containerDecl.backgroundColor(),
                containerDecl.cornerRadius(),
                containerDecl.aspectRatio(),
                containerDecl.image(),
                containerDecl.floating(),
                ClipElementConfig.builder()
                        .vertical(true)
                        .childOffset(new Vector2f(0, contentOffset))
                        .build(),
                containerDecl.border()
        );

        var contentContainerDecl = ElementDeclaration.builder()
                .layout(LayoutConfig.builder()
                        .sizing(new Sizing(SizingAxis.grow(), SizingAxis.fit()))
                        .layoutDirection(containerDecl.layout().layoutDirection())
                        .wrap(containerDecl.layout().wrap())
                        .childGap(containerDecl.layout().childGap())
                        .childAlignment(containerDecl.layout().childAlignment())
                        .build())
                .build();

        var finalScrollbarDecl = new ElementDeclaration(
                new LayoutConfig(
                        new Sizing(scrollbarDecl.layout().sizing().width(), SizingAxis.fixed(scrollbarHeight)),
                        scrollbarDecl.layout().padding(),
                        scrollbarDecl.layout().margin(),
                        scrollbarDecl.layout().childGap(),
                        scrollbarDecl.layout().childAlignment(),
                        scrollbarDecl.layout().layoutDirection(),
                        scrollbarDecl.layout().wrap()
                ),
                scrollbarDecl.backgroundColor(),
                scrollbarDecl.cornerRadius(),
                scrollbarDecl.aspectRatio(),
                scrollbarDecl.image(),
                FloatingElementConfig.builder()
                        .offset(new Vector2f(0, scrollbarOffset))
                        .expand(scrollbarDecl.floating() != null ? scrollbarDecl.floating().expand() : null)
                        .attachTo(UI.FloatingAttachToElement.ELEMENT_WITH_ID, id)
                        .attachPoints(new FloatingAttachPoints(UI.FloatingAttachPointType.TOP_RIGHT, UI.FloatingAttachPointType.TOP_RIGHT))
                        .pointerCaptureMode(UI.PointerCaptureMode.CAPTURE)
                        .build(),
                scrollbarDecl.clip(),
                scrollbarDecl.border()
        );

        return container(id, finalContainerDecl, () -> {
            container(id + "_scroll_container", contentContainerDecl, children);
            // Only show scrollbar if content is larger than container
            if (scrollableRange > 0) {
                container(id + "_scrollbar", finalScrollbarDecl, () -> {});
            }
        });
    }


    /**
     * Generates a stable integer ID from a string label.
     * @param label The label to hash.
     * @return A hashed ID.
     */
    protected int id(String label) {
        return getUIContext().hashString(label);
    }

    /**
     * Retrieves or creates a persistent state object tied to the current element's declaration order.
     * @param initialValue The value to initialize the state with if it doesn't exist.
     * @param <T> The type of the state value.
     * @return The persistent State object.
     */
    protected <T> State<T> useState(T initialValue) {
        int stateId = getUIContext().getNextHookId();
        return getUIContext().getOrCreatePersistentState(stateId, initialValue);
    }

    /**
     * Retrieves or creates a persistent state object tied to a specific key within the current element.
     * @param key A unique key for this state within the current element.
     * @param initialValue The value to initialize the state with if it doesn't exist.
     * @param <T> The type of the state value.
     * @return The persistent State object.
     */
    protected <T> State<T> useState(String key, T initialValue) {
        int stateId = getUIContext().getHookId(key);
        return getUIContext().getOrCreatePersistentState(stateId, initialValue);
    }

    /**
     * Checks if the element with the given ID is currently hovered by the pointer.
     * @param id The ID of the element to check.
     * @return true if hovered, false otherwise.
     */
    protected boolean isHovered(int id) {
        Vector2f mousePos = getUIContext().getInputState().getMousePosition();
        if (isEventCapturedByHigherFloatingElement(mousePos, id)) {
            return false;
        }
        return isPositionInside(mousePos, id);
    }

    /**
     * Checks if the given position is inside the element with the given ID.
     * @param pos The position to check.
     * @param elementId The ID of the element.
     * @return true if inside, false otherwise.
     */
    protected boolean isPositionInside(Vector2f pos, int elementId) {
        UIElementData data = getUIContext().getElementData(elementId);
        if (!data.found()) return false;

        BoundingBox bb = data.boundingBox();
        return pos.x >= bb.position().x && pos.x <= bb.position().x + bb.size().x &&
                pos.y >= bb.position().y && pos.y <= bb.position().y + bb.size().y;
    }

    /**
     * Checks if an event of the given type targeting the specified element occurred this frame.
     * @param elementId The ID of the element to check.
     * @param eventType The identifier of the event type.
     * @return The Event if heard, null otherwise.
     */
    protected Event heardEvent(int elementId, Identifier eventType) {
        for (Event event : getUIContext().getInputState().getEvents()) {
            if (event.getId().equals(eventType) && isEventTargetingElement(elementId)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Checks if a global event of the given type occurred this frame.
     * @param eventType The identifier of the event type.
     * @return The Event if heard, null otherwise.
     */
    protected Event heardEvent(Identifier eventType) {
        for (Event event : getUIContext().getInputState().getEvents()) {
            if (event.getId().equals(eventType)) {
                return event;
            }
        }
        return null;
    }

    /**
     * @param elementId The ID of the element to check.
     * @param eventType The identifier of the event type.
     * @return A list of all events of the given type that targeted the specified element this frame.
     */
    protected List<Event> heardEvents(int elementId, Identifier eventType) {
        List<Event> results = new ArrayList<>();
        for (Event event : getUIContext().getInputState().getEvents()) {
            if (event.getId().equals(eventType) && isEventTargetingElement(elementId)) {
                results.add(event);
            }
        }
        return results;
    }

    private boolean isEventTargetingElement(int elementId) {
        var mousePos = getUIContext().getInputState().getMousePosition();
        return isPositionInside(mousePos, elementId) && !isEventCapturedByHigherFloatingElement(mousePos, elementId);
    }

    /**
     * @param eventType The identifier of the event type.
     * @return A list of all global events of the given type that occurred this frame.
     */
    protected List<Event> heardEvents(Identifier eventType) {
        List<Event> results = new ArrayList<>();
        for (Event event : getUIContext().getInputState().getEvents()) {
            if (event.getId().equals(eventType)) {
                results.add(event);
            }
        }
        return results;
    }

    private boolean isEventCapturedByHigherFloatingElement(Vector2f pos, int elementId) {

        var root = getUIContext().getLastFrameRootElement();
        if (root == null) return false;

        var element = findElementById(root, elementId);
        if (pos == null || element == null) return false;

        if (sortedFloatingCache == null) {
            List<LayoutElement> allFloating = new ArrayList<>();
            collectCapturingFloatingElements(root, allFloating);
            
            List<LayoutElement> allElements = new ArrayList<>();
            collectAllElements(root, allElements);
            
            declarationOrderCache = new HashMap<>();
            for (int i = 0; i < allElements.size(); i++) {
                declarationOrderCache.put(allElements.get(i).id(), i);
            }

            allFloating.sort((e1, e2) -> {
                int z1 = e1.declaration().floating().zIndex();
                int z2 = e2.declaration().floating().zIndex();
                if (z1 != z2) return Integer.compare(z2, z1); // Higher z-index first
                return Integer.compare(declarationOrderCache.get(e2.id()), declarationOrderCache.get(e1.id())); // Later declaration first
            });
            
            sortedFloatingCache = allFloating;
        }

        boolean targetIsFloating = element.declaration() != null && element.declaration().floating() != null;
        int targetZ = targetIsFloating ? element.declaration().floating().zIndex() : Integer.MIN_VALUE;
        int targetDeclIndex = declarationOrderCache.get(elementId);

        for (LayoutElement floatingElement : sortedFloatingCache) {
            if (floatingElement.id() == elementId) break; // Reached the element itself

            // Does this floating element capture input and cover the position?
            if (floatingElement.declaration().floating().pointerCaptureMode() == UI.PointerCaptureMode.CAPTURE &&
                isPositionInside(pos, floatingElement.id())) {
                
                int fZ = floatingElement.declaration().floating().zIndex();
                if (fZ > targetZ) return true;
                if (fZ == targetZ && declarationOrderCache.get(floatingElement.id()) > targetDeclIndex) return true;
            }
        }

        return false;
    }

    private void collectAllElements(LayoutElement element, List<LayoutElement> results) {
        if (element == null) return;
        results.add(element);
        for (LayoutElement child : element.children()) {
            collectAllElements(child, results);
        }
    }

    private LayoutElement findElementById(LayoutElement root, int id) {
        if (root.id() == id) return root;
        for (LayoutElement child : root.children()) {
            LayoutElement found = findElementById(child, id);
            if (found != null) return found;
        }
        return null;
    }

    private void collectCapturingFloatingElements(LayoutElement element, List<LayoutElement> results) {
        if (element == null) return;
        if (element.declaration() != null && element.declaration().floating() != null) {
            results.add(element);
        }
        for (LayoutElement child : element.children()) {
            collectCapturingFloatingElements(child, results);
        }
    }

    private void collectElementData(LayoutElement element, Map<Integer, UIElementData> dataMap) {
        if (element == null) return;
        dataMap.put(element.id(), new UIElementData(
                new BoundingBox(new Vector2f(element.getPosition()), new Vector2f(element.getSize())),
                new Vector2f(element.getPreferredWidth(), element.getPreferredHeight()),
                true));
        for (LayoutElement child : element.children()) {
            collectElementData(child, dataMap);
        }
    }

    private void renderElement(long nvg, LayoutElement element) {

        if (element.isText()) {
            renderText(nvg, element);
        } else {
            renderContainer(nvg, element);

            boolean clipping = element.declaration() != null && element.declaration().clip() != null && (element.declaration().clip().horizontal() || element.declaration().clip().vertical());
            if (clipping) {
                nvgSave(nvg);
                nvgIntersectScissor(nvg, element.getX(), element.getY(), element.getWidth(), element.getHeight());
            }

            // Render non-floating children first
            for (LayoutElement child : element.children()) {
                if (child.declaration() == null || child.declaration().floating() == null) {
                    renderElement(nvg, child);
                }
            }

            // Then render floating children (effectively on top) sorted by zIndex
            List<LayoutElement> floatingChildren = new ArrayList<>();
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) {
                    floatingChildren.add(child);
                }
            }
            // Sort by zIndex, then by declaration order
            floatingChildren.sort((e1, e2) -> {
                int zDiff = Integer.compare(e1.declaration().floating().zIndex(), e2.declaration().floating().zIndex());
                if (zDiff != 0) return zDiff;
                return Integer.compare(element.children().indexOf(e1), element.children().indexOf(e2));
            });
            for (LayoutElement floatingChild : floatingChildren) {
                renderElement(nvg, floatingChild);
            }

            if (clipping) {
                nvgRestore(nvg);
            }
        }
    }

    private void renderContainer(long nvg, LayoutElement element) {

        ElementDeclaration decl = element.declaration();
        if (decl == null) return;

        float x = element.getX();
        float y = element.getY();
        float w = element.getWidth();
        float h = element.getHeight();

        CornerRadius cr = decl.cornerRadius() != null ? decl.cornerRadius() : UIContext.DEFAULT_CORNER_RADIUS;

        // 1. Background
        if (decl.backgroundColor() != null) {
            try (MemoryStack stack = stackPush()) {
                nvgBeginPath(nvg);
                nvgRoundedRectVarying(nvg, x, y, w, h, cr.topLeft(), cr.topRight(), cr.bottomRight(), cr.bottomLeft());
                NVGColor color = NVGColor.malloc(stack);
                nvgRGBAf(decl.backgroundColor().r(), decl.backgroundColor().g(), decl.backgroundColor().b(), decl.backgroundColor().a(), color);
                nvgFillColor(nvg, color);
                nvgFill(nvg);
            }
        }

        // 2. Borders
        if (decl.border() != null) {
            BorderElementConfig border = decl.border();
            BorderWidth bw = border.width();
            Color bc = border.color();

            try (MemoryStack stack = stackPush()) {
                NVGColor color = NVGColor.malloc(stack);
                nvgRGBAf(bc.r(), bc.g(), bc.b(), bc.a(), color);

                // If all border widths are equal, we can use a single stroke
                if (bw.left() == bw.right() && bw.left() == bw.top() && bw.left() == bw.bottom() && bw.left() > 0) {
                    float strokeWidth = bw.left();
                    nvgBeginPath(nvg);
                    // Inset the path by half the stroke width to make it an "inside" border
                    float inset = strokeWidth / 2f;
                    nvgRoundedRectVarying(nvg, x + inset, y + inset, w - strokeWidth, h - strokeWidth,
                            Math.max(0, cr.topLeft() - inset),
                            Math.max(0, cr.topRight() - inset),
                            Math.max(0, cr.bottomRight() - inset),
                            Math.max(0, cr.bottomLeft() - inset));
                    nvgStrokeColor(nvg, color);
                    nvgStrokeWidth(nvg, strokeWidth);
                    nvgStroke(nvg);
                } else {
                    // Non-uniform borders: Draw as individual rectangles
                    // Top
                    if (bw.top() > 0) {
                        nvgBeginPath(nvg);
                        nvgRect(nvg, x, y, w, bw.top());
                        nvgFillColor(nvg, color);
                        nvgFill(nvg);
                    }
                    // Bottom
                    if (bw.bottom() > 0) {
                        nvgBeginPath(nvg);
                        nvgRect(nvg, x, y + h - bw.bottom(), w, bw.bottom());
                        nvgFillColor(nvg, color);
                        nvgFill(nvg);
                    }
                    // Left
                    if (bw.left() > 0) {
                        nvgBeginPath(nvg);
                        nvgRect(nvg, x, y, bw.left(), h);
                        nvgFillColor(nvg, color);
                        nvgFill(nvg);
                    }
                    // Right
                    if (bw.right() > 0) {
                        nvgBeginPath(nvg);
                        nvgRect(nvg, x + w - bw.right(), y, bw.right(), h);
                        nvgFillColor(nvg, color);
                        nvgFill(nvg);
                    }
                }
            }
        }

        // TODO: render images, etc.
    }

    private void renderText(long nvg, LayoutElement element) {
        TextElementConfig config = element.textConfig();
        if (config == null) return;

        try (MemoryStack stack = stackPush()) {
            nvgFontSize(nvg, config.fontSize());
            nvgTextLetterSpacing(nvg, config.letterSpacing());
            nvgTextLineHeight(nvg, config.lineHeight() > 0 ? config.lineHeight() / (float) config.fontSize() : 1.0f);

            if (config.fontIdentifier() != null) {
                ClientBase.getInstance().getFontRegistry().get(config.fontIdentifier()).getOrLoadFont(nvg);
                nvgFontFace(nvg, config.fontIdentifier().toString());
            }

            int align = NVG_ALIGN_TOP;
            align |= switch (config.textAlignment()) {
                case LEFT -> NVG_ALIGN_LEFT;
                case CENTER -> NVG_ALIGN_CENTER;
                case RIGHT -> NVG_ALIGN_RIGHT;
            };
            nvgTextAlign(nvg, align);

            NVGColor color = NVGColor.malloc(stack);
            if (config.textColor() != null) {
                nvgRGBAf(config.textColor().r(), config.textColor().g(), config.textColor().b(), config.textColor().a(), color);
            } else {
                nvgRGBAf(1, 1, 1, 1, color); // Default white
            }
            nvgFillColor(nvg, color);

            float x = element.getX();
            if (config.wrapMode() == UI.TextWrapMode.NONE) {
                if (config.textAlignment() == UI.TextAlignment.CENTER) {
                    x += element.getWidth() / 2f;
                } else if (config.textAlignment() == UI.TextAlignment.RIGHT) {
                    x += element.getWidth();
                }
                nvgText(nvg, x, element.getY(), element.text());
            } else {
                nvgTextBox(nvg, x, element.getY(), element.getWidth(), element.text());
            }
        }
    }

    @Override
    public Vector2f measureText(String text, TextElementConfig config, float maxWidth) {
        long nvg = ClientBase.getInstance().getNvgContext();
        nvgFontSize(nvg, config.fontSize());
        nvgTextLetterSpacing(nvg, config.letterSpacing());
        nvgTextLineHeight(nvg, config.lineHeight() > 0 ? config.lineHeight() / (float) config.fontSize() : 1.0f);
        int align = NVG_ALIGN_TOP;
        align |= switch (config.textAlignment()) {
            case LEFT -> NVG_ALIGN_LEFT;
            case CENTER -> NVG_ALIGN_CENTER;
            case RIGHT -> NVG_ALIGN_RIGHT;
        };
        nvgTextAlign(nvg, align);
        if (config.fontIdentifier() != null) {
            ClientBase.getInstance().getFontRegistry().get(config.fontIdentifier()).getOrLoadFont(nvg);
            nvgFontFace(nvg, config.fontIdentifier().toString());
        }
        float[] bounds = new float[4];
        if (config.wrapMode() != UI.TextWrapMode.NONE) {
            nvgTextBoxBounds(nvg, 0, 0, maxWidth, text, bounds);
        } else {
            nvgTextBounds(nvg, 0, 0, text, bounds);
        }
        return new Vector2f(bounds[2] - bounds[0], bounds[3] - bounds[1]);
    }

    protected UIContext getUIContext() {
        return ClientBase.getInstance().getUIContext();
    }

}
