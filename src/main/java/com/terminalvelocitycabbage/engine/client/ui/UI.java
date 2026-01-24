package com.terminalvelocitycabbage.engine.client.ui;

public class UI {


    public enum FloatingAttachPointType {
        LEFT_TOP,
        LEFT_CENTER,
        LEFT_BOTTOM,
        CENTER_TOP,
        CENTER_CENTER,
        CENTER_BOTTOM,
        RIGHT_TOP,
        RIGHT_CENTER,
        RIGHT_BOTTOM
    }

    public enum FloatingAttachToElement {
        NONE,
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
