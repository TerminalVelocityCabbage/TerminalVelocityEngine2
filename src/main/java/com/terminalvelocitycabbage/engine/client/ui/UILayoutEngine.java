package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.*;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.FloatingElementConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.LayoutConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.TextElementConfig;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UILayoutEngine {

    public interface TextMeasurer {
        Vector2f measureText(String text, TextElementConfig config);
    }

    private final TextMeasurer textMeasurer;
    private LayoutElement rootRef;

    public UILayoutEngine(TextMeasurer textMeasurer) {
        this.textMeasurer = textMeasurer;
    }

    public void runLayout(LayoutElement root, float availableWidth, float availableHeight) {
        if (root == null) return;
        this.rootRef = root;
        
        // Pass 1: Sizing
        calculatePreferredSizes(root, availableWidth, availableHeight);
        
        // Pass 2: Positioning
        root.setX(0);
        root.setY(0);
        root.setWidth(root.getPreferredWidth());
        root.setHeight(root.getPreferredHeight());
        calculatePositions(root);

        // Pass 3: Floating elements
        calculateFloatingPositions(root);
    }

    private void calculateFloatingPositions(LayoutElement element) {
        for (LayoutElement child : element.children()) {
            ElementDeclaration decl = child.declaration();
            if (decl != null && decl.floating() != null) {
                applyFloatingPosition(child, decl.floating());
            }
            calculateFloatingPositions(child);
        }
    }

    private void applyFloatingPosition(LayoutElement element, FloatingElementConfig config) {
        LayoutElement target = null;
        switch (config.attachTo()) {
            case PARENT -> target = element.parent();
            case ROOT -> {
                target = element;
                while (target.parent() != null) target = target.parent();
            }
            case ELEMENT_WITH_ID -> {
                target = findElementById(rootRef, config.parentId());
                if (target == null) target = element.parent();
            }
        }

        if (target == null) return;

        Vector2f targetPos = new Vector2f(target.getX(), target.getY());
        Vector2f targetSize = new Vector2f(target.getWidth(), target.getHeight());
        Vector2f elementSize = new Vector2f(element.getWidth(), element.getHeight());

        // Calculate attachment point on target
        Vector2f attachPointPos = calculateAttachPoint(targetPos, targetSize, config.attachPoints().parent());
        // Calculate attachment point on element
        Vector2f elementAttachPointOffset = calculateAttachPoint(new Vector2f(), elementSize, config.attachPoints().element());

        float x = attachPointPos.x - elementAttachPointOffset.x + (config.offset() != null ? config.offset().x : 0);
        float y = attachPointPos.y - elementAttachPointOffset.y + (config.offset() != null ? config.offset().y : 0);

        element.setX(x);
        element.setY(y);

        // Apply expand if present
        if (config.expand() != null) {
            element.setWidth(element.getWidth() + config.expand().x);
            element.setHeight(element.getHeight() + config.expand().y);
        }
    }

    private Vector2f calculateAttachPoint(Vector2f pos, Vector2f size, UI.FloatingAttachPointType type) {
        float x = pos.x;
        float y = pos.y;

        switch (type) {
            case TOP_LEFT -> {}
            case LEFT -> y += size.y / 2f;
            case BOTTOM_LEFT -> y += size.y;
            case TOP -> x += size.x / 2f;
            case CENTER -> {
                x += size.x / 2f;
                y += size.y / 2f;
            }
            case BOTTOM -> {
                x += size.x / 2f;
                y += size.y;
            }
            case TOP_RIGHT -> x += size.x;
            case RIGHT -> {
                x += size.x;
                y += size.y / 2f;
            }
            case BOTTOM_RIGHT -> {
                x += size.x;
                y += size.y;
            }
        }

        return new Vector2f(x, y);
    }

    //TODO determine if this is super slow and if so we can just maintain a map of element ids to elements
    private LayoutElement findElementById(LayoutElement root, int id) {
        if (root.id() == id) return root;
        for (LayoutElement child : root.children()) {
            LayoutElement found = findElementById(child, id);
            if (found != null) return found;
        }
        return null;
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
        float preferredWidth = calculateAxisPreferredSize(sizing.width(), innerAvailableWidth, innerAvailableHeight, element.children(), true, layout);
        // Calculate this element's preferred height
        float preferredHeight = calculateAxisPreferredSize(sizing.height(), innerAvailableHeight, innerAvailableWidth, element.children(), false, layout);

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

    private float calculateAxisPreferredSize(SizingAxis axis, float availableOnThisAxis, float availableOnOtherAxis, List<LayoutElement> children, boolean isWidth, LayoutConfig layout) {
        return switch (axis.type()) {
            case FIXED -> axis.min();
            case PERCENT -> availableOnThisAxis * axis.percent();
            case FIT -> {
                float size = 0;
                boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;
                boolean isMainAxis = (isWidth && isHorizontal) || (!isWidth && !isHorizontal);

                if (layout.wrap()) {
                    float mainAxisConstraint = isMainAxis ? availableOnThisAxis : availableOnOtherAxis;
                    float currentLineMainSize = 0;
                    float currentLineCrossSize = 0;
                    float maxLineMainSize = 0;
                    float totalCrossSize = 0;

                    for (LayoutElement child : children) {
                        if (child.declaration() != null && child.declaration().floating() != null) continue;

                        float childMainSize = isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
                        float childCrossSize = isHorizontal ? child.getPreferredHeight() : child.getPreferredWidth();
                        float gap = currentLineMainSize == 0 ? 0 : layout.childGap();

                        if (currentLineMainSize + gap + childMainSize > mainAxisConstraint && currentLineMainSize > 0) {
                            maxLineMainSize = Math.max(maxLineMainSize, currentLineMainSize);
                            totalCrossSize += currentLineCrossSize + layout.childGap();
                            currentLineMainSize = childMainSize;
                            currentLineCrossSize = childCrossSize;
                        } else {
                            currentLineMainSize += gap + childMainSize;
                            currentLineCrossSize = Math.max(currentLineCrossSize, childCrossSize);
                        }
                    }
                    if (currentLineMainSize > 0) {
                        maxLineMainSize = Math.max(maxLineMainSize, currentLineMainSize);
                        totalCrossSize += currentLineCrossSize;
                    }
                    size = isMainAxis ? maxLineMainSize : totalCrossSize;
                } else {
                    if (isMainAxis) {
                        for (LayoutElement child : children) {
                            if (child.declaration() != null && child.declaration().floating() != null) continue;
                            size += isWidth ? child.getPreferredWidth() : child.getPreferredHeight();
                        }
                        int nonFloatingChildren = 0;
                        for (LayoutElement child : children) {
                            if (child.declaration() == null || child.declaration().floating() == null) nonFloatingChildren++;
                        }
                        if (nonFloatingChildren > 0) {
                            size += layout.childGap() * (nonFloatingChildren - 1);
                        }
                    } else {
                        for (LayoutElement child : children) {
                            if (child.declaration() != null && child.declaration().floating() != null) continue;
                            size = Math.max(size, isWidth ? child.getPreferredWidth() : child.getPreferredHeight());
                        }
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

        if (layout.wrap()) {
            calculateWrappedPositions(element, innerWidth, innerHeight, layout, padding);
        } else {
            calculateNonWrappedPositions(element, innerWidth, innerHeight, layout, padding);
        }
    }

    private void calculateNonWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding) {
        // 2. Calculate total content size
        float totalContentWidth = 0;
        float totalContentHeight = 0;
        if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                totalContentWidth += child.getWidth();
            }
            int nonFloatingChildrenCount = 0;
            for (LayoutElement child : element.children()) {
                if (child.declaration() == null || child.declaration().floating() == null) nonFloatingChildrenCount++;
            }
            if (nonFloatingChildrenCount > 0) {
                totalContentWidth += layout.childGap() * (nonFloatingChildrenCount - 1);
            }
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                totalContentHeight = Math.max(totalContentHeight, child.getHeight());
            }
        } else {
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                totalContentHeight += child.getHeight();
            }
            int nonFloatingChildrenCount = 0;
            for (LayoutElement child : element.children()) {
                if (child.declaration() == null || child.declaration().floating() == null) nonFloatingChildrenCount++;
            }
            if (nonFloatingChildrenCount > 0) {
                totalContentHeight += layout.childGap() * (nonFloatingChildrenCount - 1);
            }
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
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
            if (child.declaration() != null && child.declaration().floating() != null) {
                calculatePositions(child); // Still need to recurse for children of floating elements
                continue;
            }

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

    private void calculateWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding) {
        // Implement wrapping logic
        boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;
        ChildAlignment alignment = layout.childAlignment() != null ? layout.childAlignment() : UIContext.DEFAULT_ALIGNMENT;

        List<LayoutElement> children = element.children().stream()
                .filter(child -> child.declaration() == null || child.declaration().floating() == null)
                .toList();

        if (children.isEmpty()) return;

        // Group children into lines
        List<List<LayoutElement>> lines = new ArrayList<>();
        List<LayoutElement> currentLine = new ArrayList<>();
        float currentLineSize = 0;
        float maxLineCrossSize = 0;
        List<Float> lineCrossSizes = new ArrayList<>();

        for (LayoutElement child : children) {
            float childMainSize = isHorizontal ? child.getWidth() : child.getHeight();
            float gap = currentLine.isEmpty() ? 0 : layout.childGap();

            if (currentLineSize + gap + childMainSize > (isHorizontal ? innerWidth : innerHeight) && !currentLine.isEmpty()) {
                lines.add(currentLine);
                lineCrossSizes.add(maxLineCrossSize);
                currentLine = new ArrayList<>();
                currentLineSize = 0;
                maxLineCrossSize = 0;
                gap = 0;
            }

            currentLine.add(child);
            currentLineSize += gap + childMainSize;
            maxLineCrossSize = Math.max(maxLineCrossSize, isHorizontal ? child.getHeight() : child.getWidth());
        }
        lines.add(currentLine);
        lineCrossSizes.add(maxLineCrossSize);

        float totalCrossSize = 0;
        for (float size : lineCrossSizes) {
            totalCrossSize += size;
        }
        totalCrossSize += layout.childGap() * (lines.size() - 1);

        // Position lines
        float startX = element.getX() + padding.left();
        float startY = element.getY() + padding.top();

        if (isHorizontal) {
            startY += switch (alignment.y()) {
                case TOP -> 0;
                case CENTER -> (innerHeight - totalCrossSize) / 2;
                case BOTTOM -> innerHeight - totalCrossSize;
            };
        } else {
            startX += switch (alignment.x()) {
                case LEFT -> 0;
                case CENTER -> (innerWidth - totalCrossSize) / 2;
                case RIGHT -> innerWidth - totalCrossSize;
            };
        }

        float currentCrossOffset = isHorizontal ? startY : startX;

        for (int i = 0; i < lines.size(); i++) {
            List<LayoutElement> line = lines.get(i);
            float lineCrossSize = lineCrossSizes.get(i);
            float lineMainSize = 0;
            for (int j = 0; j < line.size(); j++) {
                lineMainSize += (isHorizontal ? line.get(j).getWidth() : line.get(j).getHeight());
                if (j > 0) lineMainSize += layout.childGap();
            }

            float currentMainOffset;
            if (isHorizontal) {
                currentMainOffset = startX + switch (alignment.x()) {
                    case LEFT -> 0;
                    case CENTER -> (innerWidth - lineMainSize) / 2;
                    case RIGHT -> innerWidth - lineMainSize;
                };
            } else {
                currentMainOffset = startY + switch (alignment.y()) {
                    case TOP -> 0;
                    case CENTER -> (innerHeight - lineMainSize) / 2;
                    case BOTTOM -> innerHeight - lineMainSize;
                };
            }

            for (LayoutElement child : line) {
                if (isHorizontal) {
                    float childY = currentCrossOffset + switch (alignment.y()) {
                        case TOP -> 0;
                        case CENTER -> (lineCrossSize - child.getHeight()) / 2;
                        case BOTTOM -> lineCrossSize - child.getHeight();
                    };
                    child.setX(currentMainOffset);
                    child.setY(childY);
                    currentMainOffset += child.getWidth() + layout.childGap();
                } else {
                    float childX = currentCrossOffset + switch (alignment.x()) {
                        case LEFT -> 0;
                        case CENTER -> (lineCrossSize - child.getWidth()) / 2;
                        case RIGHT -> lineCrossSize - child.getWidth();
                    };
                    child.setX(childX);
                    child.setY(currentMainOffset);
                    currentMainOffset += child.getHeight() + layout.childGap();
                }
                calculatePositions(child);
            }
            currentCrossOffset += lineCrossSize + layout.childGap();
        }

        // Handle floating elements separately
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) {
                calculatePositions(child);
            }
        }
    }

    private void distributeSpace(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout) {
        int growCount = 0;
        float usedSpace = 0;
        boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;

        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) continue;
            Sizing childSizing = getChildSizing(child);
            SizingAxis mainAxisSizing = isHorizontal ? childSizing.width() : childSizing.height();
            
            if (mainAxisSizing.type() == UI.SizingType.GROW) {
                growCount++;
            }
            usedSpace += isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
        }
        
        int nonFloatingChildrenCount = 0;
        for (LayoutElement child : element.children()) {
            if (child.declaration() == null || child.declaration().floating() == null) nonFloatingChildrenCount++;
        }
        if (nonFloatingChildrenCount > 0) {
            usedSpace += layout.childGap() * (nonFloatingChildrenCount - 1);
        }

        float extraSpace = (isHorizontal ? innerWidth : innerHeight) - usedSpace;
        float spacePerGrow = growCount > 0 ? Math.max(0, extraSpace / growCount) : 0;

        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) {
                // For floating elements, we still need to set their dimensions if they use GROW
                // GROW for floating elements usually means filling the target?
                // Actually Clay handles GROW for floating elements differently.
                // For now let's just use preferred size for floating elements here
                child.setWidth(child.getPreferredWidth());
                child.setHeight(child.getPreferredHeight());
                distributeSpace(child, child.getWidth(), child.getHeight(), 
                    (child.declaration() != null && child.declaration().layout() != null) ? child.declaration().layout() : UIContext.DEFAULT_LAYOUT);
                continue;
            }
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
