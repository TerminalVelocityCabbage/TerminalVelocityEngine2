package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.*;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.LayoutConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.TextElementConfig;
import org.joml.Vector2f;

import java.util.List;

public class UILayoutEngine {

    public interface TextMeasurer {
        Vector2f measureText(String text, TextElementConfig config);
    }

    private final TextMeasurer textMeasurer;

    public UILayoutEngine(TextMeasurer textMeasurer) {
        this.textMeasurer = textMeasurer;
    }

    public void runLayout(LayoutElement root, float availableWidth, float availableHeight) {
        if (root == null) return;
        
        // Pass 1: Sizing
        calculatePreferredSizes(root, availableWidth, availableHeight);
        
        // Pass 2: Positioning
        root.setX(0);
        root.setY(0);
        root.setWidth(root.getPreferredWidth());
        root.setHeight(root.getPreferredHeight());
        calculatePositions(root);
    }

    private void calculatePreferredSizes(LayoutElement element, float parentWidth, float parentHeight) {
        if (element.isText()) {
            Vector2f size = textMeasurer.measureText(element.text(), element.textConfig());
            element.setPreferredWidth(size.x);
            element.setPreferredHeight(size.y);
            return;
        }

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        Sizing sizing = layout.sizing() != null ? layout.sizing() : UIContext.DEFAULT_SIZING;

        float innerAvailableWidth = parentWidth - padding.left() - padding.right();
        float innerAvailableHeight = parentHeight - padding.top() - padding.bottom();

        // Calculate children preferred sizes first
        for (LayoutElement child : element.children()) {
            calculatePreferredSizes(child, innerAvailableWidth, innerAvailableHeight);
        }

        // Calculate this element's preferred width
        element.setPreferredWidth(calculateAxisPreferredSize(sizing.width(), innerAvailableWidth, element.children(), true, layout));
        // Calculate this element's preferred height
        element.setPreferredHeight(calculateAxisPreferredSize(sizing.height(), innerAvailableHeight, element.children(), false, layout));
    }

    private float calculateAxisPreferredSize(SizingAxis axis, float available, List<LayoutElement> children, boolean isWidth, LayoutConfig layout) {
        switch (axis.type()) {
            case FIXED:
                return axis.minMax().min();
            case PERCENT:
                return available * axis.percent();
            case FIT:
                float size = 0;
                boolean isMainAxis = (isWidth && layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) ||
                                     (!isWidth && layout.layoutDirection() == UI.LayoutDirection.TOP_TO_BOTTOM);
                if (isMainAxis) {
                    for (LayoutElement child : children) {
                        size += isWidth ? child.getPreferredWidth() : child.getPreferredHeight();
                    }
                    if (!children.isEmpty()) {
                        size += layout.childGap() * (children.size() - 1);
                    }
                } else {
                    for (LayoutElement child : children) {
                        size = Math.max(size, isWidth ? child.getPreferredWidth() : child.getPreferredHeight());
                    }
                }
                Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
                size += isWidth ? (padding.left() + padding.right()) : (padding.top() + padding.bottom());
                return Math.max(axis.minMax().min(), Math.min(axis.minMax().max(), size));
            case GROW:
                return axis.minMax().min();
            default:
                return 0;
        }
    }

    private void calculatePositions(LayoutElement element) {
        if (element.isText() || element.children().isEmpty()) return;

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        
        float innerWidth = element.getWidth() - padding.left() - padding.right();
        float innerHeight = element.getHeight() - padding.top() - padding.bottom();

        // 1. Distribute GROW space
        distributeSpace(element, innerWidth, innerHeight, layout);

        // 2. Calculate total content size
        float totalContentWidth = 0;
        float totalContentHeight = 0;
        if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
            for (LayoutElement child : element.children()) {
                totalContentWidth += child.getWidth();
            }
            if (!element.children().isEmpty()) {
                totalContentWidth += layout.childGap() * (element.children().size() - 1);
            }
            for (LayoutElement child : element.children()) {
                totalContentHeight = Math.max(totalContentHeight, child.getHeight());
            }
        } else {
            for (LayoutElement child : element.children()) {
                totalContentHeight += child.getHeight();
            }
            if (!element.children().isEmpty()) {
                totalContentHeight += layout.childGap() * (element.children().size() - 1);
            }
            for (LayoutElement child : element.children()) {
                totalContentWidth = Math.max(totalContentWidth, child.getWidth());
            }
        }

        // 3. Align content
        float startX = element.getX() + padding.left();
        float startY = element.getY() + padding.top();
        ChildAlignment alignment = layout.childAlignment() != null ? layout.childAlignment() : UIContext.DEFAULT_ALIGNMENT;

        if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
            startX += switch (alignment.x()) {
                case LEFT -> 0;
                case CENTER -> (innerWidth - totalContentWidth) / 2;
                case RIGHT -> innerWidth - totalContentWidth;
            };
        } else {
            startY += switch (alignment.y()) {
                case TOP -> 0;
                case CENTER -> (innerHeight - totalContentHeight) / 2;
                case BOTTOM -> innerHeight - totalContentHeight;
            };
        }

        // 4. Position children
        float currentX = startX;
        float currentY = startY;

        for (LayoutElement child : element.children()) {
            float childX = currentX;
            float childY = currentY;

            if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
                childY += switch (alignment.y()) {
                    case TOP -> 0;
                    case CENTER -> (innerHeight - child.getHeight()) / 2;
                    case BOTTOM -> innerHeight - child.getHeight();
                };
            } else {
                childX += switch (alignment.x()) {
                    case LEFT -> 0;
                    case CENTER -> (innerWidth - child.getWidth()) / 2;
                    case RIGHT -> innerWidth - child.getWidth();
                };
            }

            child.setX(childX);
            child.setY(childY);

            if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
                currentX += child.getWidth() + layout.childGap();
            } else {
                currentY += child.getHeight() + layout.childGap();
            }

            calculatePositions(child);
        }
    }

    private void distributeSpace(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout) {
        int growCount = 0;
        float usedSpace = 0;
        boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;

        for (LayoutElement child : element.children()) {
            Sizing childSizing = getChildSizing(child);
            SizingAxis mainAxisSizing = isHorizontal ? childSizing.width() : childSizing.height();
            
            if (mainAxisSizing.type() == UI.SizingType.GROW) {
                growCount++;
            }
            usedSpace += isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
        }
        
        if (!element.children().isEmpty()) {
            usedSpace += layout.childGap() * (element.children().size() - 1);
        }

        float extraSpace = (isHorizontal ? innerWidth : innerHeight) - usedSpace;
        float spacePerGrow = growCount > 0 ? Math.max(0, extraSpace / growCount) : 0;

        for (LayoutElement child : element.children()) {
            Sizing childSizing = getChildSizing(child);
            
            if (isHorizontal) {
                float w = child.getPreferredWidth();
                if (childSizing.width().type() == UI.SizingType.GROW) {
                    w += spacePerGrow;
                }
                child.setWidth(w);
                
                float h = child.getPreferredHeight();
                if (childSizing.height().type() == UI.SizingType.GROW) {
                    h = innerHeight;
                }
                child.setHeight(h);
            } else {
                float h = child.getPreferredHeight();
                if (childSizing.height().type() == UI.SizingType.GROW) {
                    h += spacePerGrow;
                }
                child.setHeight(h);
                
                float w = child.getPreferredWidth();
                if (childSizing.width().type() == UI.SizingType.GROW) {
                    w = innerWidth;
                }
                child.setWidth(w);
            }
        }
    }

    private Sizing getChildSizing(LayoutElement child) {
        if (child.isText()) return UIContext.DEFAULT_SIZING;
        ElementDeclaration childDecl = child.declaration();
        if (childDecl == null || childDecl.layout() == null || childDecl.layout().sizing() == null) {
            return UIContext.DEFAULT_SIZING;
        }
        return childDecl.layout().sizing();
    }
}
