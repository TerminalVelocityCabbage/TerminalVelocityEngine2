package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.ElementDeclaration;
import com.terminalvelocitycabbage.engine.client.ui.data.TextElementConfig;
import java.util.ArrayList;
import java.util.List;

public class LayoutElement {

    private final int id;
    private final ElementDeclaration declaration;
    private final LayoutElement parent;
    private final List<LayoutElement> children = new ArrayList<>();

    // Text specific
    private final String text;
    private final TextElementConfig textConfig;

    // Layout results
    private float preferredWidth;
    private float preferredHeight;
    private float x, y, width, height;

    public LayoutElement(int id, ElementDeclaration declaration, LayoutElement parent) {
        this(id, declaration, null, null, parent);
    }

    public LayoutElement(int id, String text, TextElementConfig textConfig, LayoutElement parent) {
        this(id, null, text, textConfig, parent);
    }

    private LayoutElement(int id, ElementDeclaration declaration, String text, TextElementConfig textConfig, LayoutElement parent) {
        this.id = id;
        this.declaration = declaration;
        this.text = text;
        this.textConfig = textConfig;
        this.parent = parent;
    }

    public int id() {
        return id;
    }

    public ElementDeclaration declaration() {
        return declaration;
    }

    public LayoutElement parent() {
        return parent;
    }

    public List<LayoutElement> children() {
        return children;
    }

    public String text() {
        return text;
    }

    public TextElementConfig textConfig() {
        return textConfig;
    }

    public boolean isText() {
        return text != null;
    }

    public float getPreferredWidth() {
        return preferredWidth;
    }

    public void setPreferredWidth(float preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public float getPreferredHeight() {
        return preferredHeight;
    }

    public void setPreferredHeight(float preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
