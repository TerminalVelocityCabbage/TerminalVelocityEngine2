package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public class UIContext {

    Element previousContainer;
    Element currentContainer;
    Element previousElement;
    Element currentElement;

    WindowProperties windowProperties;

    public UIContext(WindowProperties windowProperties) {
        previousContainer = null;
        currentContainer = null;
        previousElement = null;
        this.windowProperties = windowProperties;
    }

    public Element getPreviousContainer() {
        return previousContainer;
    }

    public void setPreviousContainer(Element previousContainer) {
        this.previousContainer = previousContainer;
    }

    public Element getCurrentContainer() {
        return currentContainer;
    }

    public void setCurrentContainer(Element currentContainer) {
        this.currentContainer = currentContainer;
    }

    public Element getPreviousElement() {
        return previousElement;
    }

    public void setPreviousElement(Element previousSibling) {
        this.previousElement = previousSibling;
    }

    public void setCurrentElement(Element currentSibling) {
        this.currentElement = currentSibling;
    }

    public Element getCurrentElement() {
        return currentElement;
    }

    public int getWindowWidth() {
        return windowProperties.getWidth();
    }

    public int getWindowHeight() {
        return windowProperties.getHeight();
    }
}
