package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class UIContext {

    Identifier previousContainer;
    Identifier currentContainer;
    Identifier previousElement;
    Identifier currentElement;

    WindowProperties windowProperties;

    public UIContext(WindowProperties windowProperties) {
        previousContainer = null;
        currentContainer = null;
        previousElement = null;
        this.windowProperties = windowProperties;
    }

    public Identifier getPreviousContainer() {
        return previousContainer;
    }

    public void setPreviousContainer(Identifier previousContainer) {
        this.previousContainer = previousContainer;
    }

    public Identifier getCurrentContainer() {
        return currentContainer;
    }

    public void setCurrentContainer(Identifier currentContainer) {
        this.currentContainer = currentContainer;
    }

    public Identifier getPreviousElement() {
        return previousElement;
    }

    public void setPreviousElement(Identifier previousSibling) {
        this.previousElement = previousSibling;
    }

    public void setCurrentElement(Identifier currentSibling) {
        this.currentElement = currentSibling;
    }

    public Identifier getCurrentElement() {
        return currentElement;
    }

    public int getWindowWidth() {
        return windowProperties.getWidth();
    }

    public int getWindowHeight() {
        return windowProperties.getHeight();
    }
}
