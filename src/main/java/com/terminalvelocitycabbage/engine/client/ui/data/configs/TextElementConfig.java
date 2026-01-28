package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;

public record TextElementConfig(Color textColor, Identifier fontIdentifier, int fontSize, int letterSpacing, int lineHeight, UI.TextWrapMode wrapMode, UI.TextAlignment textAlignment) {

    public static TextElementConfig of(String declaration) {
        if (declaration == null || declaration.isEmpty()) {
            return builder().build();
        }

        var uiContext = ClientBase.getInstance().getUIContext();
        if (uiContext.getCachedTextConfig(declaration).isPresent()) {
            return uiContext.getCachedTextConfig(declaration).get();
        }

        Builder builder = builder();
        String[] props = declaration.split("\\s+");
        for (String prop : props) {
            if (prop.isEmpty()) continue;

            String key = prop;
            String val = "";
            if (prop.contains("-[")) {
                key = prop.substring(0, prop.indexOf("-["));
                val = prop.substring(prop.indexOf("[") + 1, prop.lastIndexOf("]"));
            }

            switch (key) {
                case "text-color" -> builder.textColor(parseColor(val));
                case "font" -> builder.fontIdentifier(Identifier.of(val));
                case "text-size" -> builder.fontSize((int) parseDim(val).value());
                case "letter-spacing" -> builder.letterSpacing((int) parseDim(val).value());
                case "line-height" -> builder.lineHeight((int) parseDim(val).value());
                case "text-wrap-none", "text-wrap-words", "text-wrap-newlines" -> builder.wrapMode(UI.TextWrapMode.fromProp(prop));
                case "text-left", "text-center", "text-right" -> builder.textAlignment(UI.TextAlignment.fromProp(prop));
            }
        }

        var textConfig = builder.build();

        uiContext.cacheTextConfig(declaration, textConfig);

        return textConfig;
    }

    private record ParsedDim(float value, boolean isPercent) {}

    private static ParsedDim parseDim(String val) {
        if (val.endsWith("px")) return new ParsedDim(Float.parseFloat(val.substring(0, val.length() - 2)), false);
        if (val.endsWith("%")) return new ParsedDim(Float.parseFloat(val.substring(0, val.length() - 1)) / 100f, true);
        try {
            return new ParsedDim(Float.parseFloat(val), false);
        } catch (NumberFormatException e) {
            return new ParsedDim(0, false);
        }
    }

    private static Color parseColor(String val) {
        String[] parts = val.split(",");
        if (parts.length == 3) {
            return new Color(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), 1.0f);
        } else if (parts.length == 4) {
            return new Color(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
        }
        return new Color(0, 0, 0, 1);
    }

    public TextElementConfig {
        if (fontSize == 0) fontSize = 16;
        if (wrapMode == null) wrapMode = UI.TextWrapMode.NONE;
        if (textAlignment == null) textAlignment = UI.TextAlignment.LEFT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Color textColor;
        private Identifier fontIdentifier;
        private int fontSize;
        private int letterSpacing;
        private int lineHeight;
        private UI.TextWrapMode wrapMode;
        private UI.TextAlignment textAlignment;

        public Builder textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder fontIdentifier(Identifier fontIdentifier) {
            this.fontIdentifier = fontIdentifier;
            return this;
        }

        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder letterSpacing(int letterSpacing) {
            this.letterSpacing = letterSpacing;
            return this;
        }

        public Builder lineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public Builder wrapMode(UI.TextWrapMode wrapMode) {
            this.wrapMode = wrapMode;
            return this;
        }

        public Builder textAlignment(UI.TextAlignment textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public TextElementConfig build() {
            return new TextElementConfig(textColor, fontIdentifier, fontSize, letterSpacing, lineHeight, wrapMode, textAlignment);
        }
    }
}
