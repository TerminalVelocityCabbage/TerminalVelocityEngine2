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
        Vector2f measureText(String text, TextElementConfig config, float maxWidth);
    }

    private final TextMeasurer textMeasurer;
    private LayoutElement rootRef;

    public UILayoutEngine(TextMeasurer textMeasurer) {
        this.textMeasurer = textMeasurer;
    }

    public void runLayout(LayoutElement root, float availableWidth, float availableHeight) {
        if (root == null) return;
        this.rootRef = root;
        
        // Pass 1: Preferred Sizing
        calculatePreferredSizes(root, availableWidth, availableHeight);
        
        // Initial root sizing
        root.setX(0);
        root.setY(0);
        root.setWidth(availableWidth);
        root.setHeight(availableHeight);

        // Pass 2: Layout (Determines child sizes and relative positions)
        calculatePositions(root);
        
        // Pass 3: Sizing Update (Bottom-up update of FIT elements)
        updateSizing(root);
        
        // Pass 4: Final alignment (Top-down)
        applyAlignment(root);

        // Pass 5: Floating elements
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
        // Initialize size for floating element (it was skipped in standard layout pass)
        if (element.getWidth() == 0 && element.getHeight() == 0) {
            element.setWidth(element.getPreferredWidth());
            element.setHeight(element.getPreferredHeight());
        }

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
            Vector2f size = textMeasurer.measureText(element.text(), element.textConfig(), parentWidth);
            element.setPreferredWidth(size.x);
            element.setPreferredHeight(size.y);
            return;
        }

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        Sizing sizing = layout.sizing() != null ? layout.sizing() : UIContext.DEFAULT_SIZING;

        // Determine constraints for children (Space available for content)
        float contentConstraintW = switch (sizing.width().type()) {
            case FIXED -> sizing.width().min() - padding.left() - padding.right();
            case PERCENT -> parentWidth * sizing.width().percent() - padding.left() - padding.right();
            default -> parentWidth - padding.left() - padding.right();
        };
        float contentConstraintH = switch (sizing.height().type()) {
            case FIXED -> sizing.height().min() - padding.top() - padding.bottom();
            case PERCENT -> parentHeight * sizing.height().percent() - padding.top() - padding.bottom();
            default -> parentHeight - padding.top() - padding.bottom();
        };

        // Calculate children preferred sizes with their respective constraints
        for (LayoutElement child : element.children()) {
            calculatePreferredSizes(child, contentConstraintW, contentConstraintH);
        }

        float preferredWidth, preferredHeight;
        if (layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT) {
            // Main axis (Width)
            preferredWidth = calculateAxisPreferredSize(sizing.width(), parentWidth, parentHeight, element.children(), true, layout);
            
            // Cross axis (Height). The constraint is either the parent's space or the resolved width of this element.
            float crossConstraint = switch (sizing.width().type()) {
                case FIXED -> sizing.width().min();
                case PERCENT -> parentWidth * sizing.width().percent();
                case FIT -> preferredWidth;
                case GROW -> parentWidth;
            };
            preferredHeight = calculateAxisPreferredSize(sizing.height(), parentHeight, crossConstraint, element.children(), false, layout);
        } else {
            // Main axis (Height)
            preferredHeight = calculateAxisPreferredSize(sizing.height(), parentHeight, parentWidth, element.children(), false, layout);
            
            // Cross axis (Width)
            float crossConstraint = switch (sizing.height().type()) {
                case FIXED -> sizing.height().min();
                case PERCENT -> parentHeight * sizing.height().percent();
                case FIT -> preferredHeight;
                case GROW -> parentHeight;
            };
            preferredWidth = calculateAxisPreferredSize(sizing.width(), parentWidth, crossConstraint, element.children(), true, layout);
        }

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
                // Default to width priority
                preferredHeight = preferredWidth / ratio;
            }
        }

        element.setPreferredWidth(preferredWidth);
        element.setPreferredHeight(preferredHeight);
    }

    private float calculateAxisPreferredSize(SizingAxis axis, float availableOnThisAxis, float availableOnOtherAxis, List<LayoutElement> children, boolean isWidth, LayoutConfig layout) {
        return switch (axis.type()) {
            case FIXED -> axis.min();
            case PERCENT -> {
                float size = availableOnThisAxis * axis.percent();
                yield Math.max(axis.min(), Math.min(axis.max(), size));
            }
            case FIT -> {
                float size = 0;
                boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;
                boolean isMainAxis = (isWidth && isHorizontal) || (!isWidth && !isHorizontal);

                if (layout.wrap()) {
                    Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
                    float paddingMain = isHorizontal ? (padding.left() + padding.right()) : (padding.top() + padding.bottom());
                    float mainAxisConstraint = (isMainAxis ? availableOnThisAxis : availableOnOtherAxis) - paddingMain;
                    
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

        if (layout.wrap()) {
            calculateWrappedPositions(element, innerWidth, innerHeight, layout, padding);
        } else {
            calculateNonWrappedPositions(element, innerWidth, innerHeight, layout, padding);
        }
    }

    private void calculateNonWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding) {
        boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;
        
        List<LayoutElement> children = element.children().stream()
                .filter(child -> child.declaration() == null || child.declaration().floating() == null)
                .toList();
        
        if (children.isEmpty()) return;

        // 1. Distribute Space (GROW)
        int growCount = 0;
        float usedMainSpace = 0;
        for (LayoutElement child : children) {
            usedMainSpace += isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
            if (getChildSizing(child).main(isHorizontal).type() == UI.SizingType.GROW) growCount++;
        }
        usedMainSpace += layout.childGap() * (children.size() - 1);
        
        float extraMainSpace = Math.max(0, (isHorizontal ? innerWidth : innerHeight) - usedMainSpace);
        float spacePerGrow = growCount > 0 ? extraMainSpace / growCount : 0;

        // 2. Set child dimensions and recurse
        float currentMainOffset = 0;
        for (LayoutElement child : children) {
            Sizing childSizing = getChildSizing(child);
            float w = child.getPreferredWidth();
            float h = child.getPreferredHeight();
            
            if (childSizing.main(isHorizontal).type() == UI.SizingType.GROW) {
                float growAmount = spacePerGrow;
                float currentMainSize = isHorizontal ? w : h;
                float maxMainSize = childSizing.main(isHorizontal).max();
                if (currentMainSize + growAmount > maxMainSize) {
                    growAmount = Math.max(0, maxMainSize - currentMainSize);
                }
                if (isHorizontal) w += growAmount; else h += growAmount;
            }
            if (childSizing.cross(isHorizontal).type() == UI.SizingType.GROW) {
                if (isHorizontal) h = Math.min(childSizing.height().max(), innerHeight);
                else w = Math.min(childSizing.width().max(), innerWidth);
            }

            // Apply Aspect Ratio
            float ratio = getAspectRatio(child);
            if (ratio > 0) {
                if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() != UI.SizingType.GROW) h = w / ratio;
                else if (childSizing.height().type() == UI.SizingType.GROW && childSizing.width().type() != UI.SizingType.GROW) w = h * ratio;
                else if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() == UI.SizingType.GROW) {
                    if (w / ratio > innerHeight) { h = innerHeight; w = h * ratio; } else h = w / ratio;
                }
            }
            
            child.setWidth(w);
            child.setHeight(h);
            
            // Set relative position (alignment will be handled in Pass 4)
            if (isHorizontal) {
                child.setX(currentMainOffset);
                child.setY(0);
                currentMainOffset += w + layout.childGap();
            } else {
                child.setX(0);
                child.setY(currentMainOffset);
                currentMainOffset += h + layout.childGap();
            }
            
            calculatePositions(child);
        }

        // Handle floating children
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) {
                calculatePositions(child);
            }
        }
    }

    private void calculateWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding) {
        boolean isHorizontal = layout.layoutDirection() == UI.LayoutDirection.LEFT_TO_RIGHT;
        
        List<LayoutElement> children = element.children().stream()
                .filter(child -> child.declaration() == null || child.declaration().floating() == null)
                .toList();

        if (children.isEmpty()) return;

        // 1. Group into lines
        List<List<LayoutElement>> lines = new ArrayList<>();
        List<LayoutElement> currentLine = new ArrayList<>();
        float currentLineMainSize = 0;
        float mainConstraint = isHorizontal ? innerWidth : innerHeight;

        for (LayoutElement child : children) {
            float childMainSize = isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
            float gap = currentLine.isEmpty() ? 0 : layout.childGap();

            if (currentLineMainSize + gap + childMainSize > mainConstraint && !currentLine.isEmpty()) {
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLineMainSize = 0;
                gap = 0;
            }
            currentLine.add(child);
            currentLineMainSize += gap + childMainSize;
        }
        lines.add(currentLine);

        // 2. Process lines (Distribute GROW and calculate cross sizes)
        float currentCrossOffset = 0;
        for (List<LayoutElement> line : lines) {
            float lineMainPreferredSize = 0;
            int growCount = 0;
            for (LayoutElement child : line) {
                lineMainPreferredSize += isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight();
                if (getChildSizing(child).main(isHorizontal).type() == UI.SizingType.GROW) growCount++;
            }
            lineMainPreferredSize += layout.childGap() * (line.size() - 1);
            float extraMainSpace = Math.max(0, mainConstraint - lineMainPreferredSize);
            float spacePerGrow = growCount > 0 ? extraMainSpace / growCount : 0;

            float maxLineCrossSize = 0;
            for (LayoutElement child : line) {
                Sizing childSizing = getChildSizing(child);
                float w = child.getPreferredWidth();
                float h = child.getPreferredHeight();
                if (childSizing.main(isHorizontal).type() == UI.SizingType.GROW) {
                    float growAmount = spacePerGrow;
                    float currentMainSize = isHorizontal ? w : h;
                    float maxMainSize = childSizing.main(isHorizontal).max();
                    if (currentMainSize + growAmount > maxMainSize) {
                        growAmount = Math.max(0, maxMainSize - currentMainSize);
                    }
                    if (isHorizontal) w += growAmount; else h += growAmount;
                }
                float ratio = getAspectRatio(child);
                if (ratio > 0) {
                    if (isHorizontal) h = w / ratio; else w = h * ratio;
                }
                child.setWidth(w);
                child.setHeight(h);
                maxLineCrossSize = Math.max(maxLineCrossSize, isHorizontal ? h : w);
            }

            // Align children in line cross-axis and set relative positions
            float currentMainOffset = 0;
            for (LayoutElement child : line) {
                Sizing childSizing = getChildSizing(child);
                if (childSizing.cross(isHorizontal).type() == UI.SizingType.GROW) {
                    if (isHorizontal) child.setHeight(Math.min(childSizing.height().max(), maxLineCrossSize));
                    else child.setWidth(Math.min(childSizing.width().max(), maxLineCrossSize));
                }
                
                if (isHorizontal) {
                    child.setX(currentMainOffset);
                    child.setY(currentCrossOffset);
                    currentMainOffset += child.getWidth() + layout.childGap();
                } else {
                    child.setX(currentCrossOffset);
                    child.setY(currentMainOffset);
                    currentMainOffset += child.getHeight() + layout.childGap();
                }
                calculatePositions(child);
            }
            currentCrossOffset += maxLineCrossSize + layout.childGap();
        }

        // Handle floating
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) {
                calculatePositions(child);
            }
        }
    }

    private float getAspectRatio(LayoutElement element) {
        ElementDeclaration decl = element.declaration();
        return (decl != null && decl.aspectRatio() != null) ? decl.aspectRatio().aspectRatio() : 0;
    }

    private void updateSizing(LayoutElement element) {
        // Post-order traversal (bottom-up)
        for (LayoutElement child : element.children()) {
            updateSizing(child);
        }

        if (element.isText() || element == rootRef) return;

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Sizing sizing = layout.sizing() != null ? layout.sizing() : UIContext.DEFAULT_SIZING;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;

        if (sizing.width().type() == UI.SizingType.FIT) {
            float minX = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            boolean hasChildren = false;
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                minX = Math.min(minX, child.getX());
                maxX = Math.max(maxX, child.getX() + child.getWidth());
                hasChildren = true;
            }
            if (hasChildren) {
                element.setWidth(Math.max(sizing.width().min(), Math.min(sizing.width().max(), maxX - minX + padding.left() + padding.right())));
            }
        }

        if (sizing.height().type() == UI.SizingType.FIT) {
            float minY = Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            boolean hasChildren = false;
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                minY = Math.min(minY, child.getY());
                maxY = Math.max(maxY, child.getY() + child.getHeight());
                hasChildren = true;
            }
            if (hasChildren) {
                element.setHeight(Math.max(sizing.height().min(), Math.min(sizing.height().max(), maxY - minY + padding.top() + padding.bottom())));
            }
        }

        // Re-apply aspect ratio after FIT updates
        if (decl.aspectRatio() != null && decl.aspectRatio().aspectRatio() > 0) {
            float ratio = decl.aspectRatio().aspectRatio();
            boolean widthFit = sizing.width().type() == UI.SizingType.FIT || sizing.width().type() == UI.SizingType.GROW;
            boolean heightFit = sizing.height().type() == UI.SizingType.FIT || sizing.height().type() == UI.SizingType.GROW;

            if (widthFit && !heightFit) {
                element.setWidth(element.getHeight() * ratio);
            } else if (heightFit && !widthFit) {
                element.setHeight(element.getWidth() / ratio);
            } else if (widthFit && heightFit) {
                // If both are flexible, prioritize width as base for now
                element.setHeight(element.getWidth() / ratio);
            }
        }
    }

    private void applyAlignment(LayoutElement element) {
        if (element.isText() || element.children().isEmpty()) return;

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        ChildAlignment alignment = layout.childAlignment() != null ? layout.childAlignment() : UIContext.DEFAULT_ALIGNMENT;

        float innerWidth = element.getWidth() - padding.left() - padding.right();
        float innerHeight = element.getHeight() - padding.top() - padding.bottom();

        // Calculate bounding box of all non-floating children (they currently have relative positions)
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        boolean hasChildren = false;
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) continue;
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
            hasChildren = true;
        }

        if (hasChildren) {
            float contentWidth = maxX - minX;
            float contentHeight = maxY - minY;

            float offsetX = padding.left();
            offsetX += switch (alignment.x()) {
                case LEFT -> -minX;
                case CENTER -> (innerWidth - contentWidth) / 2f - minX;
                case RIGHT -> (innerWidth - contentWidth) - minX;
            };

            float offsetY = padding.top();
            offsetY += switch (alignment.y()) {
                case TOP -> -minY;
                case CENTER -> (innerHeight - contentHeight) / 2f - minY;
                case BOTTOM -> (innerHeight - contentHeight) - minY;
            };

            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                child.setX(element.getX() + child.getX() + offsetX);
                child.setY(element.getY() + child.getY() + offsetY);
                applyAlignment(child);
            }
        }
        
        // Handle floating children separately
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) {
                applyAlignment(child);
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
