package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class UIContext {

    public static final Padding DEFAULT_PADDING = new Padding(0, 0, 0, 0);
    public static final Sizing DEFAULT_SIZING = new Sizing(SizingAxis.fit(), SizingAxis.fit());
    public static final ChildAlignment DEFAULT_ALIGNMENT = new ChildAlignment(LayoutAlignmentX.LEFT, LayoutAlignmentY.TOP);
    public static final LayoutConfig DEFAULT_LAYOUT = new LayoutConfig(DEFAULT_SIZING, DEFAULT_PADDING, 0, DEFAULT_ALIGNMENT, LayoutDirection.LEFT_TO_RIGHT);

    private final Map<Integer, UIElementData> lastFrameElementData = new HashMap<>();
    private final Stack<Integer> idStack = new Stack<>();
    private final Stack<Integer> autoIdCounterStack = new Stack<>();

    private LayoutElement rootElement;
    private LayoutElement currentElement;

    public UIContext() {
        autoIdCounterStack.push(0);
    }

    public UIElementData getElementData(int id) {
        return lastFrameElementData.getOrDefault(id, UIElementData.DEFAULT);
    }

    public void pushId(int id) {
        idStack.push(id);
        autoIdCounterStack.push(0);
    }

    public void popId() {
        idStack.pop();
        autoIdCounterStack.pop();
    }

    public int generateAutoId() {
        int parentId = idStack.isEmpty() ? 0 : idStack.peek();
        int counter = autoIdCounterStack.pop();
        counter++;
        autoIdCounterStack.push(counter);
        
        return hashString("autoId", counter, parentId);
    }

    public int hashString(String label) {
        return hashString(label, 0, idStack.isEmpty() ? 0 : idStack.peek());
    }

    public int hashString(String label, int offset, int baseId) {
        int hash = 0;
        if (label != null) {
            for (int i = 0; i < label.length(); i++) {
                hash = hash * 33 + label.charAt(i);
            }
        }
        hash += offset;
        hash += baseId;
        return hash;
    }

    public void beginFrame(float width, float height) {
        idStack.clear();
        autoIdCounterStack.clear();
        autoIdCounterStack.push(0);

        // Create an implicit root element that fills the window
        int rootId = hashString("window_root", 0, 0);
        ElementDeclaration rootDecl = ElementDeclaration.builder()
                .layout(LayoutConfig.builder()
                        .sizing(new Sizing(SizingAxis.fixed(width), SizingAxis.fixed(height)))
                        .build())
                .build();
        rootElement = new LayoutElement(rootId, rootDecl, null);
        rootElement.setWidth(width);
        rootElement.setHeight(height);
        currentElement = rootElement;
        pushId(rootId);
    }

    public void openElement(int id, ElementDeclaration declaration) {
        LayoutElement element = new LayoutElement(id, declaration, currentElement);
        if (rootElement == null) {
            rootElement = element;
        }
        if (currentElement != null) {
            currentElement.children().add(element);
        }
        currentElement = element;
        pushId(id);
    }

    public void closeElement() {
        if (currentElement != null) {
            currentElement = currentElement.parent();
        }
        popId();
    }

    public void addTextElement(int id, String text, TextElementConfig config) {
        LayoutElement element = new LayoutElement(id, text, config, currentElement);
        if (currentElement != null) {
            currentElement.children().add(element);
        }
    }

    public LayoutElement getRootElement() {
        return rootElement;
    }

    public void endFrame(Map<Integer, UIElementData> newFrameData) {
        lastFrameElementData.clear();
        lastFrameElementData.putAll(newFrameData);
    }
}
