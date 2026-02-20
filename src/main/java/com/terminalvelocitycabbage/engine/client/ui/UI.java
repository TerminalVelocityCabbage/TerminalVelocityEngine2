package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;

public class UI {

    public static String grow() {
        return "grow";
    }
    
    public static String growX() {
        return "grow-x";
    }

    public static String growY() {
        return "grow-y";
    }
    
    public static String minGrowX(int value, UIUnit unit) {
        return "min-grow-x-[" + value + unit.suffix() + "]";
    }
    
    public static String minGrowY(int value, UIUnit unit) {
        return "min-grow-y-[" + value + unit.suffix() + "]";
    }
    
    public static String maxGrowX(int value, UIUnit unit) {
        return "max-grow-x-[" + value + unit.suffix() + "]";
    }
    
    public static String maxGrowY(int value, UIUnit unit) {
        return "max-grow-y-[" + value + unit.suffix() + "]";
    }
    
    public static String fit() {
        return "fit";
    }

    public static String fitX() {
        return "fit-x";
    }
    
    public static String fitY() {
        return "fit-y";
    }
    
    public static String minFitX(int value, UIUnit unit) {
        return "min-fit-x-[" + value + unit.suffix() + "]";
    }
    
    public static String minFitY(int value, UIUnit unit) {
        return "min-fit-y-[" + value + unit.suffix() + "]";
    }
    
    public static String maxFitX(int value, UIUnit unit) {
        return "max-fit-x-[" + value + unit.suffix() + "]";
    }
    
    public static String maxFitY(int value, UIUnit unit) {
        return "max-fit-y-[" + value + unit.suffix() + "]";
    }
    
    public static String width(int value, UIUnit unit) {
        return "w-[" + value + unit.suffix() + "]";
    }
    
    public static String height(int value, UIUnit unit) {
        return "h-[" + value + unit.suffix() + "]";
    }
    
    public static String minWidth(int value, UIUnit unit) {
        return "min-w-[" + value + unit.suffix() + "]";
    }
    
    public static String minHeight(int value, UIUnit unit) {
        return "min-h-[" + value + unit.suffix() + "]";
    }
    
    public static String maxWidth(int value, UIUnit unit) {
        return "max-w-[" + value + unit.suffix() + "]";
    }
    
    public static String maxHeight(int value, UIUnit unit) {
        return "max-h-[" + value + unit.suffix() + "]";
    }
    
    public static String p(int value, UIUnit unit) {
        return "p-[" + value + unit.suffix() + "]";
    }
    
    public static String pX(int value, UIUnit unit) {
        return "px-[" + value + unit.suffix() + "]";
    }
    
    public static String pY(int value, UIUnit unit) {
        return "py-[" + value + unit.suffix() + "]";
    }
    
    public static String pT(int value, UIUnit unit) {
        return "pt-[" + value + unit.suffix() + "]";
    }
    
    public static String pB(int value, UIUnit unit) {
        return "pb-[" + value + unit.suffix() + "]";
    }
    
    public static String pL(int value, UIUnit unit) {
        return "pl-[" + value + unit.suffix() + "]";
    }
    
    public static String pR(int value, UIUnit unit) {
        return "pr-[" + value + unit.suffix() + "]";
    }
    
    public static String m(int value, UIUnit unit) {
        return "m-[" + value + unit.suffix() + "]";
    }
    
    public static String mX(int value, UIUnit unit) {
        return "mx-[" + value + unit.suffix() + "]";
    }
    
    public static String mY(int value, UIUnit unit) {
        return "my-[" + value + unit.suffix() + "]";
    }
    
    public static String mT(int value, UIUnit unit) {
        return "mt-[" + value + unit.suffix() + "]";
    }
    
    public static String mB(int value, UIUnit unit) {
        return "mb-[" + value + unit.suffix() + "]";
    }
    
    public static String mL(int value, UIUnit unit) {
        return "ml-[" + value + unit.suffix() + "]";
    }
    
    public static String mR(int value, UIUnit unit) {
        return "mr-[" + value + unit.suffix() + "]";
    }
    
    public static String backgroundColor(Color color) {
        return "bg-[" + color.toPropString() + "]";
    }
    
    public static String rounded(int radius) {
        return "rounded-[" + radius + "]";
    }
    
    public static String roundedT(int radius) {
        return "roundedt-[" + radius + "]";
    }
    
    public static String roundedB(int radius) {
        return "roundedb-[" + radius + "]";
    }
    
    public static String roundedL(int radius) {
        return "roundedl-[" + radius + "]";
    }
    
    public static String roundedR(int radius) {
        return "roundedr-[" + radius + "]";
    }
    
    public static String roundedTL(int radius) {
        return "roundedtl-[" + radius + "]";
    }
    
    public static String roundedTR(int radius) {
        return "roundedtr-[" + radius + "]";
    }
    
    public static String roundedBL(int radius) {
        return "roundedbl-[" + radius + "]";
    }
    
    public static String roundedBR(int radius) {
        return "roundedbr-[" + radius + "]";
    }
    
    public static String rounded(int TL, int TR, int BL, int BR) {
        return roundedTL(TL) + " " + roundedTR(TR) + " " + roundedBL(BL) + " " + roundedBR(BR);
    }
    
    public static String border(int width) {
        return "border-width-[" + width + "]";
    }

    public static String borderT(int width) {
        return "bordert-width-[" + width + "]";
    }

    public static String borderB(int width) {
        return "borderb-width-[" + width + "]";
    }

    public static String borderL(int width) {
        return "borderl-width-[" + width + "]";
    }
    
    public static String borderR(int width) {
        return "borderr-width-[" + width + "]";
    }

    public static String borderX(int width) {
        return "borderx-width-[" + width + "]";
    }

    public static String borderY(int width) {
        return "bordery-width-[" + width + "]";
    }

    public static String borderColor(Color color) {
        return "border-color-[" + color.toPropString() + "]";
    }

    public static String aspect(int width, int height) {
        return "aspect-[" + width + "/" + height + "]";
    }

    public static String gap(int value, UIUnit unit) {
        return "gap-[" + value + unit.suffix() + "]";
    }

    public static String alignX(HorizontalAlignment alignment) {
        return "alignx-[" + alignment.name().toLowerCase() + "]";
    }

    public static String alignY(VerticalAlignment alignment) {
        return "aligny-[" + alignment.name().toLowerCase() + "]";
    }
    
    public static String layout(LayoutDirection direction) {
        return "layout-[" + direction.toProp() + "]";
    }
    
    public static String wrap() {
        return "wrap";
    }
    
    public static String floatParent() {
        return "float-parent";
    }
    
    public static String floatRoot() {
        return "float-root";
    }
    
    public static String floatAttached(int elementId) {
        return "float-element-[" + elementId + "]";
    }
    
    public static String attachTo(FloatingAttachPointType element, FloatingAttachToElement to) {
        return "attach-[" + element.name().toLowerCase() + "] to-[" + to.name().toLowerCase() + "]";
    }
    
    public static String floatOffsetX(int value, UIUnit unit) {
        return "float-offset-x-[" + value + unit.suffix() + "]";
    }
    
    public static String floatOffsetY(int value, UIUnit unit) {
        return "float-offset-y-[" + value + unit.suffix() + "]";
    }
    
    public static String floatExpandX(int value, UIUnit unit) {
        return "float-expand-x-[" + value + unit.suffix() + "]";
    }
    
    public static String floatExpandY(int value, UIUnit unit) {
        return "float-expand-y-[" + value + unit.suffix() + "]";
    }
    
    public static String zIndex(int value) {
        return "z-index-[" + value + "]";
    }
    
    public static String pointerCapture(boolean enabled) {
        return enabled ? "pointer-capture" : "pointer-passthrough";
    }
    
    public static String clipToParent() {
        return "clip-to-parent";
    }
    
    public static String clipToNone() {
        return "clip-to-none";
    }
    
    public static String clip() {
        return "clip";
    }
    
    public static String clipX() {
        return "clip-x";
    }
    
    public static String clipY() {
        return "clip-y";
    }
    
    public static String clipOffsetX(int value, UIUnit unit) {
        return "clip-offset-x-[" + value + unit.suffix() + "]";
    }
    
    public static String clipOffsetY(int value, UIUnit unit) {
        return "clip-offset-y-[" + value + unit.suffix() + "]";
    }
    
    public static String bgImage(Identifier image, Identifier atlas) {
        return "img-[" + image + "] atlas-[" + atlas + "]";
    }
    
    public static String imageRounded(int value, UIUnit unit) {
        return "img-rounded-[" + value + unit.suffix() + "]";
    }
    
    public static String imageBackground(Color color) {
        return "img-bg-[" + color.toPropString() + "]";
    }
    
    public static String textColor(Color color) {
        return "text-color-[" + color.toPropString() + "]";
    }

    public static String font(Identifier font) {
        return "font-[" + font + "]";
    }
    
    public static String textSize(int value, UIUnit unit) {
        return "text-size-[" + value + unit.suffix() + "]";
    }
    
    public static String letterSpacing(int value, UIUnit unit) {
        return "letter-spacing-[" + value + unit.suffix() + "]";
    }
    
    public static String lineHeight(int value, UIUnit unit) {
        return "line-height-[" + value + unit.suffix() + "]";
    }
    
    public static String textWrap(TextWrapMode mode) {
        return "text-wrap-[" + mode.name().toLowerCase() + "]";
    }
    
    public static String textAlignment(TextAlignment alignment) {
        return "text-" + alignment.name().toLowerCase();
    }
    
    public enum UIUnit {
        PIXELS("px"),
        PERCENT("%");
        private final String suffix;
        UIUnit(String suffix) {
            this.suffix = suffix;
        }
        public String suffix() {
            return suffix;
        }
    }
    
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
                case "top_left" -> TOP_LEFT;
                case "top_right" -> TOP_RIGHT;
                case "bottom_left" -> BOTTOM_LEFT;
                case "bottom_right" -> BOTTOM_RIGHT;
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
        LEFT_TO_RIGHT("ltr"),
        RIGHT_TO_LEFT("trl"),
        TOP_TO_BOTTOM("ttb"),
        BOTTOM_TO_TOP("btt");

        private final String propValue;

        LayoutDirection(String propValue) {
            this.propValue = propValue;
        }

        public static LayoutDirection fromProp(String prop) {
            return switch (prop.toLowerCase()) {
                case "rtl", "right-to-left" -> RIGHT_TO_LEFT;
                case "ttb", "top-to-bottom" -> TOP_TO_BOTTOM;
                case "btt", "bottom-to-top" -> BOTTOM_TO_TOP;
                default -> LEFT_TO_RIGHT;
            };
        }

        public String toProp() {
            return propValue;
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
