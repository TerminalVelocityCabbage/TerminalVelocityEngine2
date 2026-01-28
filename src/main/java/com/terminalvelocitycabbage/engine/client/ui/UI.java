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
        BOTTOM_RIGHT;

        public static FloatingAttachPointType fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "top" -> TOP;
                case "bottom" -> BOTTOM;
                case "left" -> LEFT;
                case "right" -> RIGHT;
                case "center" -> CENTER;
                case "top-left" -> TOP_LEFT;
                case "top-right" -> TOP_RIGHT;
                case "bottom-left" -> BOTTOM_LEFT;
                case "bottom-right" -> BOTTOM_RIGHT;
                default -> TOP_LEFT;
            };
        }
    }

    public enum FloatingAttachToElement {
        PARENT,
        ELEMENT_WITH_ID,
        ROOT
    }

    public enum FloatingClipToElement {
        NONE,
        ATTACHED_PARENT;

        public static FloatingClipToElement fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "clip-to-parent" -> ATTACHED_PARENT;
                default -> NONE;
            };
        }
    }

    public enum HorizontalAlignment {
        LEFT,
        RIGHT,
        CENTER;

        public static HorizontalAlignment fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "right" -> RIGHT;
                case "center" -> CENTER;
                default -> LEFT;
            };
        }
    }

    public enum VerticalAlignment {
        TOP,
        BOTTOM,
        CENTER;

        public static VerticalAlignment fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "bottom" -> BOTTOM;
                case "center" -> CENTER;
                default -> TOP;
            };
        }
    }

    public enum LayoutDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP;

        public static LayoutDirection fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "rtl", "right-to-left" -> RIGHT_TO_LEFT;
                case "ttb", "top-to-bottom" -> TOP_TO_BOTTOM;
                case "btt", "bottom-to-top" -> BOTTOM_TO_TOP;
                default -> LEFT_TO_RIGHT;
            };
        }
    }

    public enum PointerCaptureMode {
        CAPTURE,
        PASSTHROUGH;

        public static PointerCaptureMode fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "pointer-passthrough" -> PASSTHROUGH;
                default -> CAPTURE;
            };
        }
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
        RIGHT;

        public static TextAlignment fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "text-center" -> CENTER;
                case "text-right" -> RIGHT;
                default -> LEFT;
            };
        }
    }

    public enum TextWrapMode {
        WORDS,
        NEWLINES,
        NONE;

        public static TextWrapMode fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "text-wrap-words" -> WORDS;
                case "text-wrap-newlines" -> NEWLINES;
                default -> NONE;
            };
        }
    }
}
