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
        float preferredWidth = calculateAxisPreferredSize(sizing.width(), innerAvailableWidth, element.children(), true, layout);
        // Calculate this element's preferred height
        float preferredHeight = calculateAxisPreferredSize(sizing.height(), innerAvailableHeight, element.children(), false, layout);

        // Apply aspect ratio if present
        if (decl.aspectRatio() != null && decl.aspectRatio().aspectRatio() > 0) {
            float ratio = decl.aspectRatio().aspectRatio();
            boolean widthFixed = sizing.width().type() == UI.SizingType.FIXED || sizing.width().type() == UI.SizingType.PERCENT;
            boolean heightFixed = sizing.height().type() == UI.SizingType.FIXED || sizing.height().type() == UI.SizingType.PERCENT;

            if (widthFixed && !heightFixed) {
                preferredHeight = preferredWidth / ratio;
            } else if (heightFixed && !widthFixed) {
                preferredWidth = preferredHeight * ratio;
            } else if (!widthFixed && !heightFixed) {
                // If neither is strictly fixed, we use the larger one to determine the other or follow some priority
                // In Clay, aspect ratio usually works by calculating one from the other
                // Let's assume width has priority if both are FIT/GROW for now
                preferredHeight = preferredWidth / ratio;
            }
        }

        element.setPreferredWidth(preferredWidth);
        element.setPreferredHeight(preferredHeight);
    }

    private float calculateAxisPreferredSize(SizingAxis axis, float available, List<LayoutElement> children, boolean isWidth, LayoutConfig layout) {
        return switch (axis.type()) {
            case FIXED -> axis.min();
            case PERCENT -> available * axis.percent();
            case FIT -> {
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
                yield Math.max(axis.min(), Math.min(axis.max(), size));
            }
            case GROW -> axis.min();
        };
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
            
            float w = child.getPreferredWidth();
            float h = child.getPreferredHeight();
            ElementDeclaration childDecl = child.declaration();
            float aspectRatio = (childDecl != null && childDecl.aspectRatio() != null) ? childDecl.aspectRatio().aspectRatio() : 0;

            if (isHorizontal) {
                if (childSizing.width().type() == UI.SizingType.GROW) {
                    w += spacePerGrow;
                }
                
                if (childSizing.height().type() == UI.SizingType.GROW) {
                    h = innerHeight;
                }

                // Apply aspect ratio if constrained
                if (aspectRatio > 0) {
                    if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() != UI.SizingType.GROW) {
                        h = w / aspectRatio;
                    } else if (childSizing.height().type() == UI.SizingType.GROW && childSizing.width().type() != UI.SizingType.GROW) {
                        w = h * aspectRatio;
                    } else if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() == UI.SizingType.GROW) {
                        // Both GROW, adjust to fit within innerWidth/innerHeight while maintaining ratio
                        if (w / aspectRatio > innerHeight) {
                            h = innerHeight;
                            w = h * aspectRatio;
                        } else {
                            h = w / aspectRatio;
                        }
                    }
                }

                child.setWidth(w);
                child.setHeight(h);
            } else {
                if (childSizing.height().type() == UI.SizingType.GROW) {
                    h += spacePerGrow;
                }
                
                if (childSizing.width().type() == UI.SizingType.GROW) {
                    w = innerWidth;
                }

                // Apply aspect ratio if constrained
                if (aspectRatio > 0) {
                    if (childSizing.height().type() == UI.SizingType.GROW && childSizing.width().type() != UI.SizingType.GROW) {
                        w = h * aspectRatio;
                    } else if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() != UI.SizingType.GROW) {
                        h = w / aspectRatio;
                    } else if (childSizing.height().type() == UI.SizingType.GROW && childSizing.width().type() == UI.SizingType.GROW) {
                        // Both GROW
                        if (h * aspectRatio > innerWidth) {
                            w = innerWidth;
                            h = w / aspectRatio;
                        } else {
                            w = h * aspectRatio;
                        }
                    }
                }

                child.setHeight(h);
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
