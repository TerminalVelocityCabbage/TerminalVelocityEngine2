package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.ElementDeclaration;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.TextElementConfig;
import org.joml.Vector2f;

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
    private final Vector2f preferredSize = new Vector2f();
    private final Vector2f position = new Vector2f();
    private final Vector2f size = new Vector2f();

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

    public void setPreferredSize(Vector2f preferredSize) {
        this.preferredSize.set(preferredSize);
    }

    public float getPreferredWidth() {
        return preferredSize.x;
    }

    public void setPreferredWidth(float preferredWidth) {
        preferredSize.x = preferredWidth;
    }

    public float getPreferredHeight() {
        return preferredSize.y;
    }

    public void setPreferredHeight(float preferredHeight) {
        preferredSize.y = preferredHeight;
    }

    public float getX() {
        return position.x;
    }

    public void setX(float x) {
        position.x = x;
    }

    public float getY() {
        return position.y;
    }

    public void setY(float y) {
        position.y = y;
    }

    public float getWidth() {
        return size.x;
    }

    public void setWidth(float width) {
        size.x = width;
    }

    public float getHeight() {
        return size.y;
    }

    public void setHeight(float height) {
        size.y = height;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }
}
