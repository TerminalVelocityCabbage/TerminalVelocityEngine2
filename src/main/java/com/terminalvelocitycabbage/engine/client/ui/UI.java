package com.terminalvelocitycabbage.engine.client.ui;

public class UI {


    public enum FloatingAttachPointType {
        TOP_LEFT,
        LEFT,
        BOTTOM_LEFT,
        TOP,
        CENTER,
        BOTTOM,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT
    }

    public enum FloatingAttachToElement {
        PARENT,
        ELEMENT_WITH_ID,
        ROOT
    }

    public enum FloatingClipToElement {
        NONE,
        ATTACHED_PARENT
    }

    public enum HorizontalAlignment {
        LEFT,
        RIGHT,
        CENTER
    }

    public enum VerticalAlignment {
        TOP,
        BOTTOM,
        CENTER
    }

    //TODO add right to left and bottom to top
    public enum LayoutDirection {
        LEFT_TO_RIGHT,
        TOP_TO_BOTTOM
    }

    public enum PointerCaptureMode {
        CAPTURE,
        PASSTHROUGH
    }

    public enum RenderCommandType {
        NONE,
        RECTANGLE,
        BORDER,
        TEXT,
        IMAGE,
        SCISSOR_START,
        SCISSOR_END,
        CUSTOM
    }

    public enum SizingType {
        FIT,
        GROW,
        PERCENT,
        FIXED
    }

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum TextWrapMode {
        WORDS,
        NEWLINES,
        NONE
    }
}
