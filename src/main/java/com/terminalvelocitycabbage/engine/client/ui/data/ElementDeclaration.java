package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.*;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;
import org.joml.Vector2f;

public record ElementDeclaration(
        LayoutConfig layout,
        Color backgroundColor,
        CornerRadius cornerRadius,
        ImageElementConfig image,
        FloatingElementConfig floating,
        ClipElementConfig clip,
        BorderElementConfig border
) {

    public static ElementDeclaration of(String declaration) {
        if (declaration == null || declaration.isEmpty()) {
            return builder().build();
        }

        var uiContext = ClientBase.getInstance().getUIContext();
        if (uiContext.getCachedElementDeclaration(declaration).isPresent()) {
            return uiContext.getCachedElementDeclaration(declaration).get();
        }

        Builder builder = builder();
        SizingAxis width = SizingAxis.fit();
        SizingAxis height = SizingAxis.fit();
        Padding padding = new Padding();
        Margin margin = new Margin();
        UI.HorizontalAlignment hAlign = UI.HorizontalAlignment.LEFT;
        UI.VerticalAlignment vAlign = UI.VerticalAlignment.TOP;

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
                case "grow" -> { width = SizingAxis.grow(); height = SizingAxis.grow(); }
                case "grow-x" -> width = SizingAxis.grow();
                case "grow-y" -> height = SizingAxis.grow();
                case "min-grow-x" -> width = SizingAxis.grow(parseDim(val).value(), width.max());
                case "max-grow-x" -> width = SizingAxis.grow(width.min(), parseDim(val).value());
                case "min-grow-y" -> height = SizingAxis.grow(parseDim(val).value(), height.max());
                case "max-grow-y" -> height = SizingAxis.grow(height.min(), parseDim(val).value());
                case "fit" -> { width = SizingAxis.fit(); height = SizingAxis.fit(); }
                case "fit-x" -> width = SizingAxis.fit();
                case "fit-y" -> height = SizingAxis.fit();
                case "min-fit-x" -> width = SizingAxis.fit(parseDim(val).value(), width.max());
                case "max-fit-x" -> width = SizingAxis.fit(width.min(), parseDim(val).value());
                case "min-fit-y" -> height = SizingAxis.fit(parseDim(val).value(), height.max());
                case "max-fit-y" -> height = SizingAxis.fit(height.min(), parseDim(val).value());
                case "w" -> {
                    ParsedDim dim = parseDim(val);
                    width = dim.isPercent() ? SizingAxis.percent(dim.value()) : SizingAxis.fixed(dim.value());
                }
                case "h" -> {
                    ParsedDim dim = parseDim(val);
                    height = dim.isPercent() ? SizingAxis.percent(dim.value()) : SizingAxis.fixed(dim.value());
                }
                case "min-w" -> width = width.withMin(parseDim(val).value());
                case "max-w" -> width = width.withMax(parseDim(val).value());
                case "min-h" -> height = height.withMin(parseDim(val).value());
                case "max-h" -> height = height.withMax(parseDim(val).value());

                case "p" -> padding.all((int) parseDim(val).value());
                case "pt" -> padding.top((int) parseDim(val).value());
                case "pb" -> padding.bottom((int) parseDim(val).value());
                case "pl" -> padding.left((int) parseDim(val).value());
                case "pr" -> padding.right((int) parseDim(val).value());
                case "px" -> { int d = (int) parseDim(val).value(); padding.left(d).right(d); }
                case "py" -> { int d = (int) parseDim(val).value(); padding.top(d).bottom(d); }

                case "m" -> margin.all((int) parseDim(val).value());
                case "mt" -> margin.top((int) parseDim(val).value());
                case "mb" -> margin.bottom((int) parseDim(val).value());
                case "ml" -> margin.left((int) parseDim(val).value());
                case "mr" -> margin.right((int) parseDim(val).value());
                case "mx" -> { int d = (int) parseDim(val).value(); margin.left(d).right(d); }
                case "my" -> { int d = (int) parseDim(val).value(); margin.top(d).bottom(d); }

                case "bg" -> builder.backgroundColor(parseColor(val));

                case "rounded" -> builder.cornerRadius().all(parseDim(val).value());
                case "roundedt" -> { float d = parseDim(val).value(); builder.cornerRadius().topLeft(d).topRight(d); }
                case "roundedb" -> { float d = parseDim(val).value(); builder.cornerRadius().bottomLeft(d).bottomRight(d); }
                case "roundedl" -> { float d = parseDim(val).value(); builder.cornerRadius().topLeft(d).bottomLeft(d); }
                case "roundedr" -> { float d = parseDim(val).value(); builder.cornerRadius().topRight(d).bottomRight(d); }
                case "roundedtl" -> builder.cornerRadius().topLeft(parseDim(val).value());
                case "roundedtr" -> builder.cornerRadius().topRight(parseDim(val).value());
                case "roundedbl" -> builder.cornerRadius().bottomLeft(parseDim(val).value());
                case "roundedbr" -> builder.cornerRadius().bottomRight(parseDim(val).value());

                case "border-width" -> builder.borderWidth().all((int) parseDim(val).value());
                case "bordert-width" -> builder.borderWidth().top((int) parseDim(val).value());
                case "borderb-width" -> builder.borderWidth().bottom((int) parseDim(val).value());
                case "borderl-width" -> builder.borderWidth().left((int) parseDim(val).value());
                case "borderr-width" -> builder.borderWidth().right((int) parseDim(val).value());
                case "borderx-width" -> { int d = (int) parseDim(val).value(); builder.borderWidth().left(d).right(d); }
                case "bordery-width" -> { int d = (int) parseDim(val).value(); builder.borderWidth().top(d).bottom(d); }

                case "border-color" -> builder.borderColor(parseColor(val));

                case "aspect" -> builder.layoutBuilder().aspectRatio(parseRatio(val));

                case "gap" -> builder.layoutBuilder().childGap((int) parseDim(val).value());
                case "align-x" -> hAlign = UI.HorizontalAlignment.fromProp(val);
                case "align-y" -> vAlign = UI.VerticalAlignment.fromProp(val);
                case "layout-x", "layout-y" -> builder.layoutBuilder().layoutDirection(UI.LayoutDirection.fromProp(val));
                case "wrap" -> builder.layoutBuilder().wrap(true);

                case "float-parent" -> builder.floatingBuilder().attachTo(UI.FloatingAttachToElement.PARENT);
                case "float-root" -> builder.floatingBuilder().attachTo(UI.FloatingAttachToElement.ROOT);
                case "float-element" -> builder.floatingBuilder().attachTo(UI.FloatingAttachToElement.ELEMENT_WITH_ID, Integer.parseInt(val));
                case "attach" -> builder.floatingBuilder().attachPoints(new FloatingAttachPoints(UI.FloatingAttachPointType.fromProp(val), getFloatingParentAttach(builder.floatingBuilder())));
                case "to" -> builder.floatingBuilder().attachPoints(new FloatingAttachPoints(getFloatingElementAttach(builder.floatingBuilder()), UI.FloatingAttachPointType.fromProp(val)));
                case "float-offset-x" -> builder.floatingBuilder().offset(getFloatingOffset(builder.floatingBuilder()).setComponent(0, parseDim(val).value()));
                case "float-offset-y" -> builder.floatingBuilder().offset(getFloatingOffset(builder.floatingBuilder()).setComponent(1, parseDim(val).value()));
                case "float-expand-x" -> builder.floatingBuilder().expand(getFloatingExpand(builder.floatingBuilder()).setComponent(0, parseDim(val).value()));
                case "float-expand-y" -> builder.floatingBuilder().expand(getFloatingExpand(builder.floatingBuilder()).setComponent(1, parseDim(val).value()));
                case "z" -> builder.floatingBuilder().zIndex(Integer.parseInt(val));
                case "pointer-passthrough" -> builder.floatingBuilder().pointerCaptureMode(UI.PointerCaptureMode.PASSTHROUGH);
                case "pointer-capture" -> builder.floatingBuilder().pointerCaptureMode(UI.PointerCaptureMode.CAPTURE);
                case "clip-to-parent" -> builder.floatingBuilder().clipTo(UI.FloatingClipToElement.ATTACHED_PARENT);
                case "clip-to-none" -> builder.floatingBuilder().clipTo(UI.FloatingClipToElement.NONE);

                case "clip-x" -> builder.clipBuilder().horizontal(true);
                case "clip-y" -> builder.clipBuilder().vertical(true);
                case "clip" -> {
                    builder.clipBuilder().horizontal(true).vertical(true);
                }
                case "clip-offset-x" -> builder.clipBuilder().childOffset(getClipOffset(builder.clipBuilder()).setComponent(0, parseDim(val).value()));
                case "clip-offset-y" -> builder.clipBuilder().childOffset(getClipOffset(builder.clipBuilder()).setComponent(1, parseDim(val).value()));

                case "img" -> builder.imageBuilder().imageIdentifier(Identifier.fromString(val));
                case "atlas" -> builder.imageBuilder().atlasIdentifier(Identifier.fromString(val));
                case "img-rounded" -> builder.imageBuilder().cornerRadius(new CornerRadius(parseDim(val).value()));
                case "img-bg" -> builder.imageBuilder().backgroundColor(parseColor(val));
            }
        }

        builder.layoutBuilder().sizing(new Sizing(width, height)).padding(padding).margin(margin).childAlignment(new ChildAlignment(hAlign, vAlign));

        var elementDeclaration = builder.build();

        uiContext.cacheElementDeclaration(declaration, elementDeclaration);

        return elementDeclaration;
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

    private static float parseRatio(String val) {
        if (val.contains("/")) {
            String[] parts = val.split("/");
            return Float.parseFloat(parts[0]) / Float.parseFloat(parts[1]);
        }
        return Float.parseFloat(val);
    }

    private static Vector2f getFloatingOffset(FloatingElementConfig.Builder builder) {
        Vector2f v = builder.offset();
        return v == null ? new Vector2f() : v;
    }

    private static Vector2f getFloatingExpand(FloatingElementConfig.Builder builder) {
        Vector2f v = builder.expand();
        return v == null ? new Vector2f() : v;
    }

    private static UI.FloatingAttachPointType getFloatingElementAttach(FloatingElementConfig.Builder builder) {
        FloatingAttachPoints p = builder.attachPoints();
        return p == null ? UI.FloatingAttachPointType.TOP_LEFT : p.element();
    }

    private static UI.FloatingAttachPointType getFloatingParentAttach(FloatingElementConfig.Builder builder) {
        FloatingAttachPoints p = builder.attachPoints();
        return p == null ? UI.FloatingAttachPointType.TOP_LEFT : p.parent();
    }

    private static Vector2f getClipOffset(ClipElementConfig.Builder builder) {
        Vector2f v = builder.childOffset();
        return v == null ? new Vector2f() : v;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LayoutConfig.Builder layoutBuilder;
        private Color backgroundColor;
        private CornerRadius cornerRadius;
        private ImageElementConfig.Builder imageBuilder;
        private FloatingElementConfig.Builder floatingBuilder;
        private ClipElementConfig.Builder clipBuilder;
        private BorderWidth borderWidth;
        private Color borderColor;

        public LayoutConfig.Builder layoutBuilder() {
            if (layoutBuilder == null) layoutBuilder = LayoutConfig.builder();
            return layoutBuilder;
        }

        public Builder layout(LayoutConfig.Builder layout) {
            this.layoutBuilder = layout;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public CornerRadius cornerRadius() {
            if (cornerRadius == null) cornerRadius = new CornerRadius();
            return cornerRadius;
        }

        public Builder cornerRadius(CornerRadius cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public ImageElementConfig.Builder imageBuilder() {
            if (imageBuilder == null) imageBuilder = ImageElementConfig.builder();
            return imageBuilder;
        }

        public Builder image(ImageElementConfig.Builder image) {
            this.imageBuilder = image;
            return this;
        }

        public FloatingElementConfig.Builder floatingBuilder() {
            if (floatingBuilder == null) floatingBuilder = FloatingElementConfig.builder();
            return floatingBuilder;
        }

        public Builder floating(FloatingElementConfig.Builder floating) {
            this.floatingBuilder = floating;
            // Note: parentId is handled by build() or set via attachTo
            return this;
        }

        public ClipElementConfig.Builder clipBuilder() {
            if (clipBuilder == null) clipBuilder = ClipElementConfig.builder();
            return clipBuilder;
        }

        public Builder clip(ClipElementConfig.Builder clip) {
            this.clipBuilder = clip;
            return this;
        }

        public BorderWidth borderWidth() {
            if (borderWidth == null) borderWidth = new BorderWidth();
            return borderWidth;
        }

        public Builder borderColor(Color borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder border(BorderElementConfig border) {
            this.borderColor = border.color();
            this.borderWidth = border.width();
            return this;
        }

        public ElementDeclaration build() {
            return new ElementDeclaration(
                    layoutBuilder == null ? null : layoutBuilder.build(),
                    backgroundColor,
                    cornerRadius,
                    imageBuilder == null ? null : imageBuilder.build(),
                    floatingBuilder == null ? null : floatingBuilder.build(),
                    clipBuilder == null ? null : clipBuilder.build(),
                    new BorderElementConfig(borderColor, borderWidth)
            );
        }
    }
}
