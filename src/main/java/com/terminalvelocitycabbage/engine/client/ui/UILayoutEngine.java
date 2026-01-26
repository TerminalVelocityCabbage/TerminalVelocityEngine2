package com.terminalvelocitycabbage.engine.client.ui;

import com.terminalvelocitycabbage.engine.client.ui.data.*;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.FloatingElementConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.LayoutConfig;
import com.terminalvelocitycabbage.engine.client.ui.data.configs.TextElementConfig;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UILayoutEngine {

    public interface LayoutDataSource {
        Vector2f measureText(String text, TextElementConfig config, float maxWidth);
        Vector2f getImageDimensions(Identifier imageId, Identifier atlasId);
    }

    private final LayoutDataSource dataSource;
    private LayoutElement rootRef;

    public UILayoutEngine(LayoutDataSource textMeasurer) {
        this.dataSource = textMeasurer;
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
                // After positioning the floating element, we MUST recalculate its children's absolute positions
                applyAlignment(child);
            }
            calculateFloatingPositions(child);
        }
    }

    private void applyFloatingPosition(LayoutElement element, FloatingElementConfig config) {
        // Initialize size for floating element (it was skipped in standard layout pass)
        // Note: we check both width and height to be 0, but fit/fixed might have set them already
        // However, standard layout pass skips floating elements completely in calculatePositions,
        // so we must ensure they have their final sizes here before positioning.
        if (element.getWidth() == 0 && element.getHeight() == 0) {
            element.setWidth(element.getPreferredWidth());
            element.setHeight(element.getPreferredHeight());
        }

        // Before positioning children, we must ensure they have their sizes and relative positions calculated
        // calculatePositions(element) was skipped for floating elements in Pass 2
        calculatePositions(element);

        LayoutElement target = switch (config.attachTo()) {
            case PARENT -> element.parent();
            case ROOT -> rootRef;
            case ELEMENT_WITH_ID -> findElementById(rootRef, config.parentId());
        };

        if (target == null) Log.crash("UI Target not found for element: " + element.id());

        Vector2f targetPos = new Vector2f(target.getX(), target.getY());
        Vector2f targetSize = new Vector2f(target.getWidth(), target.getHeight());
        Vector2f elementSize = new Vector2f(element.getWidth(), element.getHeight());

        // Calculate attachment point on target
        Vector2f attachPointPos = calculateAttachPoint(targetPos, targetSize, config.attachPoints().parent());
        // Calculate attachment point on element
        Vector2f elementAttachPointOffset = calculateAttachPoint(new Vector2f(), elementSize, config.attachPoints().element());

        element.setX(attachPointPos.x - elementAttachPointOffset.x + (config.offset() != null ? config.offset().x : 0));
        element.setY(attachPointPos.y - elementAttachPointOffset.y + (config.offset() != null ? config.offset().y : 0));

        // Apply expand if present
        if (config.expand() != null) {
            element.setWidth(element.getWidth() + config.expand().x);
            element.setHeight(element.getHeight() + config.expand().y);
        }
    }

    private Vector2f calculateAttachPoint(Vector2f pos, Vector2f size, UI.FloatingAttachPointType type) {

        return switch (type) {
            case TOP_LEFT ->        new Vector2f(pos.x, pos.y);
            case LEFT ->            new Vector2f(pos.x, pos.y + size.y / 2f);
            case BOTTOM_LEFT ->     new Vector2f(pos.x, pos.y + size.y);
            case TOP ->             new Vector2f(pos.x + size.x / 2f, pos.y);
            case CENTER ->          new Vector2f(pos.x + size.x / 2f, pos.y + size.y / 2f);
            case BOTTOM ->          new Vector2f(pos.x + size.x / 2f, pos.y + size.y);
            case TOP_RIGHT ->       new Vector2f(pos.x + size.x, pos.y);
            case RIGHT ->           new Vector2f(pos.x + size.x, pos.y + size.y / 2f);
            case BOTTOM_RIGHT ->    new Vector2f(pos.x + size.x, pos.y + size.y);
        };
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

        Margin margin = getMargin(element);
        float availableW = parentWidth - margin.left() - margin.right();
        float availableH = parentHeight - margin.top() - margin.bottom();

        if (element.isText()) {
            element.setPreferredSize(dataSource.measureText(element.text(), element.textConfig(), availableW));
            return;
        }

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        BorderWidth border = (decl.border() != null) ? decl.border().width() : UIContext.DEFAULT_BORDER_WIDTH;
        Sizing sizing = layout.sizing() != null ? layout.sizing() : UIContext.DEFAULT_SIZING;

        // Determine constraints for children (Space available for content)
        float contentConstraintW = switch (sizing.width().type()) {
            case FIXED -> sizing.width().min() - padding.left() - padding.right() - border.left() - border.right();
            case PERCENT -> availableW * sizing.width().percent() - padding.left() - padding.right() - border.left() - border.right();
            default -> availableW - padding.left() - padding.right() - border.left() - border.right();
        };
        float contentConstraintH = switch (sizing.height().type()) {
            case FIXED -> sizing.height().min() - padding.top() - padding.bottom() - border.top() - border.bottom();
            case PERCENT -> availableH * sizing.height().percent() - padding.top() - padding.bottom() - border.top() - border.bottom();
            default -> availableH - padding.top() - padding.bottom() - border.top() - border.bottom();
        };

        // Calculate children preferred sizes with their respective constraints
        for (LayoutElement child : element.children()) {
            calculatePreferredSizes(child, contentConstraintW, contentConstraintH);
        }

        float preferredWidth, preferredHeight;
        UI.LayoutDirection direction = layout.layoutDirection();
        if (direction == UI.LayoutDirection.LEFT_TO_RIGHT || direction == UI.LayoutDirection.RIGHT_TO_LEFT) {
            // Main axis (Width)
            preferredWidth = calculateAxisPreferredSize(element, sizing.width(), availableW, availableH, true);
            
            // Cross axis (Height). The constraint is either the parent's space or the resolved width of this element.
            float crossConstraint = switch (sizing.width().type()) {
                case FIXED -> sizing.width().min();
                case PERCENT -> availableW * sizing.width().percent();
                case FIT -> preferredWidth;
                case GROW -> availableW;
            };
            preferredHeight = calculateAxisPreferredSize(element, sizing.height(), availableH, crossConstraint, false);
        } else {
            // Main axis (Height)
            preferredHeight = calculateAxisPreferredSize(element, sizing.height(), availableH, availableW, false);
            
            // Cross axis (Width)
            float crossConstraint = switch (sizing.height().type()) {
                case FIXED -> sizing.height().min();
                case PERCENT -> availableH * sizing.height().percent();
                case FIT -> preferredHeight;
                case GROW -> availableH;
            };
            preferredWidth = calculateAxisPreferredSize(element, sizing.width(), availableW, crossConstraint, true);
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

    private Margin getMargin(LayoutElement element) {
        if (element.isText()) return UIContext.DEFAULT_MARGIN;
        ElementDeclaration decl = element.declaration();
        if (decl == null) return UIContext.DEFAULT_MARGIN;
        LayoutConfig layout = decl.layout();
        if (layout == null) return UIContext.DEFAULT_MARGIN;
        Margin margin = layout.margin();
        return margin != null ? margin : UIContext.DEFAULT_MARGIN;
    }

    private float calculateAxisPreferredSize(LayoutElement element, SizingAxis axis, float availableOnThisAxis, float availableOnOtherAxis, boolean isWidth) {
        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl != null && decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        BorderWidth border = (decl != null && decl.border() != null) ? decl.border().width() : UIContext.DEFAULT_BORDER_WIDTH;
        List<LayoutElement> children = element.children();

        return switch (axis.type()) {
            case FIXED -> axis.min();
            case PERCENT -> {
                float size = availableOnThisAxis * axis.percent();
                yield Math.max(axis.min(), Math.min(axis.max(), size));
            }
            case FIT -> {
                float size = 0;

                // Handle Image FIT
                if (decl != null && decl.image() != null) {
                    Vector2f dims = dataSource.getImageDimensions(decl.image().imageIdentifier(), decl.image().atlasIdentifier());
                    size = isWidth ? dims.x : dims.y;
                } else {
                    UI.LayoutDirection direction = layout.layoutDirection();
                    boolean isHorizontal = direction == UI.LayoutDirection.LEFT_TO_RIGHT || direction == UI.LayoutDirection.RIGHT_TO_LEFT;
                    boolean isMainAxis = (isWidth && isHorizontal) || (!isWidth && !isHorizontal);

                    if (layout.wrap()) {
                        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
                        float paddingMain = isHorizontal ? (padding.left() + padding.right()) : (padding.top() + padding.bottom());
                        float borderMain = isHorizontal ? (border.left() + border.right()) : (border.top() + border.bottom());
                        float mainAxisConstraint = (isMainAxis ? availableOnThisAxis : availableOnOtherAxis) - paddingMain - borderMain;

                        float currentLineMainSize = 0;
                        float currentLineCrossSize = 0;
                        float maxLineMainSize = 0;
                        float totalCrossSize = 0;

                        for (LayoutElement child : children) {
                            if (child.declaration() != null && child.declaration().floating() != null) continue;

                            Margin childMargin = getMargin(child);
                            float childMarginMain = isHorizontal ? (childMargin.left() + childMargin.right()) : (childMargin.top() + childMargin.bottom());
                            float childMarginCross = isHorizontal ? (childMargin.top() + childMargin.bottom()) : (childMargin.left() + childMargin.right());

                            float childMainSize = (isHorizontal ? child.getPreferredWidth() : child.getPreferredHeight()) + childMarginMain;
                            float childCrossSize = (isHorizontal ? child.getPreferredHeight() : child.getPreferredWidth()) + childMarginCross;
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
                                Margin childMargin = getMargin(child);
                                float childMarginSize = isWidth ? (childMargin.left() + childMargin.right()) : (childMargin.top() + childMargin.bottom());
                                size += (isWidth ? child.getPreferredWidth() : child.getPreferredHeight()) + childMarginSize;
                            }
                            int nonFloatingChildren = 0;
                            for (LayoutElement child : children) {
                                if (child.declaration() == null || child.declaration().floating() == null)
                                    nonFloatingChildren++;
                            }
                            if (nonFloatingChildren > 0) {
                                size += layout.childGap() * (nonFloatingChildren - 1);
                            }
                        } else {
                            for (LayoutElement child : children) {
                                if (child.declaration() != null && child.declaration().floating() != null) continue;
                                Margin childMargin = getMargin(child);
                                float childMarginSize = isWidth ? (childMargin.left() + childMargin.right()) : (childMargin.top() + childMargin.bottom());
                                size = Math.max(size, (isWidth ? child.getPreferredWidth() : child.getPreferredHeight()) + childMarginSize);
                            }
                        }
                    }
                }

                Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
                size += isWidth ? (padding.left() + padding.right() + border.left() + border.right()) : (padding.top() + padding.bottom() + border.top() + border.bottom());
                yield Math.max(axis.min(), Math.min(axis.max(), size));
            }
            case GROW -> axis.min();
        };
    }

    private void calculatePositions(LayoutElement element) {
        if (element.isText()) return;

        // Ensure we calculate positions for floating children too if they were skipped
        // Standard layout pass skips floating elements in children list within calculate(Non)WrappedPositions
        // but we need to recurse into them.
        
        // Note: we don't return early if children is empty because updateSizing might still need to run
        // but here we are calculating child positions, so if there are no children, there's nothing to do
        if (element.children().isEmpty()) return;

        ElementDeclaration decl = element.declaration();
        LayoutConfig layout = decl.layout() != null ? decl.layout() : UIContext.DEFAULT_LAYOUT;
        Padding padding = layout.padding() != null ? layout.padding() : UIContext.DEFAULT_PADDING;
        BorderWidth border = (decl.border() != null) ? decl.border().width() : UIContext.DEFAULT_BORDER_WIDTH;

        float innerWidth = element.getWidth() - padding.left() - padding.right() - border.left() - border.right();
        float innerHeight = element.getHeight() - padding.top() - padding.bottom() - border.top() - border.bottom();

        if (layout.wrap()) {
            calculateWrappedPositions(element, innerWidth, innerHeight, layout, padding, border);
        } else {
            calculateNonWrappedPositions(element, innerWidth, innerHeight, layout, padding, border);
        }
    }

    private void calculateNonWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding, BorderWidth border) {
        UI.LayoutDirection direction = layout.layoutDirection();
        boolean isHorizontal = direction == UI.LayoutDirection.LEFT_TO_RIGHT || direction == UI.LayoutDirection.RIGHT_TO_LEFT;
        boolean isReversed = direction == UI.LayoutDirection.RIGHT_TO_LEFT || direction == UI.LayoutDirection.BOTTOM_TO_TOP;
        
        List<LayoutElement> children = element.children().stream()
                .filter(child -> child.declaration() == null || child.declaration().floating() == null)
                .toList();
        
        if (children.isEmpty()) return;

        // 1. Distribute Space (GROW)
        int growCount = 0;
        float usedMainSpace = 0;
        for (LayoutElement child : children) {
            Margin m = getMargin(child);
            usedMainSpace += (isHorizontal ? child.getPreferredWidth() + m.left() + m.right() : child.getPreferredHeight() + m.top() + m.bottom());
            if (getChildSizing(child).main(isHorizontal).type() == UI.SizingType.GROW) growCount++;
        }
        usedMainSpace += layout.childGap() * (children.size() - 1);
        
        float extraMainSpace = Math.max(0, (isHorizontal ? innerWidth : innerHeight) - usedMainSpace);
        float spacePerGrow = growCount > 0 ? extraMainSpace / growCount : 0;

        // 2. Set child dimensions and recurse
        float currentMainOffset = 0;
        for (LayoutElement child : children) {
            Sizing childSizing = getChildSizing(child);
            Margin m = getMargin(child);
            float w = child.getPreferredWidth();
            float h = child.getPreferredHeight();
            
            if (childSizing.main(isHorizontal).type() == UI.SizingType.GROW) {
                float growAmount = spacePerGrow;
                float currentMainSize = isHorizontal ? w + m.left() + m.right() : h + m.top() + m.bottom();
                float maxMainSize = childSizing.main(isHorizontal).max();
                if (currentMainSize + growAmount > maxMainSize) {
                    growAmount = Math.max(0, maxMainSize - currentMainSize);
                }
                if (isHorizontal) w += growAmount; else h += growAmount;
            }
            if (childSizing.cross(isHorizontal).type() == UI.SizingType.GROW) {
                if (isHorizontal) h = Math.min(childSizing.height().max(), innerHeight - m.top() - m.bottom());
                else w = Math.min(childSizing.width().max(), innerWidth - m.left() - m.right());
            }

            // Apply Aspect Ratio
            float ratio = getAspectRatio(child);
            if (ratio > 0) {
                if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() != UI.SizingType.GROW) h = w / ratio;
                else if (childSizing.height().type() == UI.SizingType.GROW && childSizing.width().type() != UI.SizingType.GROW) w = h * ratio;
                else if (childSizing.width().type() == UI.SizingType.GROW && childSizing.height().type() == UI.SizingType.GROW) {
                    if (w / ratio > innerHeight - m.top() - m.bottom()) { h = innerHeight - m.top() - m.bottom(); w = h * ratio; } else h = w / ratio;
                }
            }
            
            child.setWidth(w);
            child.setHeight(h);
            
            // Set relative position (alignment will be handled in Pass 4)
            if (isHorizontal) {
                child.setX(isReversed ? -currentMainOffset - m.right() - w : currentMainOffset + m.left());
                child.setY(m.top());
                currentMainOffset += m.left() + w + m.right() + layout.childGap();
            } else {
                child.setX(m.left());
                child.setY(isReversed ? -currentMainOffset - m.bottom() - h : currentMainOffset + m.top());
                currentMainOffset += m.top() + h + m.bottom() + layout.childGap();
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

    private void calculateWrappedPositions(LayoutElement element, float innerWidth, float innerHeight, LayoutConfig layout, Padding padding, BorderWidth border) {
        UI.LayoutDirection direction = layout.layoutDirection();
        boolean isHorizontal = direction == UI.LayoutDirection.LEFT_TO_RIGHT || direction == UI.LayoutDirection.RIGHT_TO_LEFT;
        boolean isReversed = direction == UI.LayoutDirection.RIGHT_TO_LEFT || direction == UI.LayoutDirection.BOTTOM_TO_TOP;
        
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
            Margin m = getMargin(child);
            float childMainSize = isHorizontal ? child.getPreferredWidth() + m.left() + m.right() : child.getPreferredHeight() + m.top() + m.bottom();
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

        // 2. Initial line cross-size calculation
        List<Float> lineCrossSizes = new ArrayList<>();
        float totalLinesCrossSize = 0;
        for (List<LayoutElement> line : lines) {
            float maxLineCrossSize = 0;
            for (LayoutElement child : line) {
                Margin m = getMargin(child);
                maxLineCrossSize = Math.max(maxLineCrossSize, isHorizontal ? child.getPreferredHeight() + m.top() + m.bottom() : child.getPreferredWidth() + m.left() + m.right());
            }
            lineCrossSizes.add(maxLineCrossSize);
            totalLinesCrossSize += maxLineCrossSize;
        }
        if (lines.size() > 1) {
            totalLinesCrossSize += layout.childGap() * (lines.size() - 1);
        }

        // 3. Distribute extra cross space among lines (Align-content: stretch)
        float extraCrossSpace = Math.max(0, (isHorizontal ? innerHeight : innerWidth) - totalLinesCrossSize);
        float extraCrossPerLine = extraCrossSpace / lines.size();

        // 4. Process lines
        float currentCrossOffset = 0;
        for (int i = 0; i < lines.size(); i++) {
            List<LayoutElement> line = lines.get(i);
            float lineCrossSize = lineCrossSizes.get(i) + extraCrossPerLine;

            float lineMainPreferredSize = 0;
            int growCount = 0;
            for (LayoutElement child : line) {
                Margin m = getMargin(child);
                lineMainPreferredSize += isHorizontal ? child.getPreferredWidth() + m.left() + m.right() : child.getPreferredHeight() + m.top() + m.bottom();
                if (getChildSizing(child).main(isHorizontal).type() == UI.SizingType.GROW) growCount++;
            }
            lineMainPreferredSize += layout.childGap() * (line.size() - 1);
            float extraMainSpace = Math.max(0, mainConstraint - lineMainPreferredSize);
            float spacePerGrow = growCount > 0 ? extraMainSpace / growCount : 0;

            for (LayoutElement child : line) {
                Sizing childSizing = getChildSizing(child);
                Margin m = getMargin(child);
                float w = child.getPreferredWidth();
                float h = child.getPreferredHeight();
                if (childSizing.main(isHorizontal).type() == UI.SizingType.GROW) {
                    float growAmount = spacePerGrow;
                    float currentMainSize = isHorizontal ? w + m.left() + m.right() : h + m.top() + m.bottom();
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
            }

            // Align children in line cross-axis and set relative positions
            float currentMainOffset = 0;
            for (LayoutElement child : line) {
                Sizing childSizing = getChildSizing(child);
                Margin m = getMargin(child);
                if (childSizing.cross(isHorizontal).type() == UI.SizingType.GROW) {
                    if (isHorizontal) child.setHeight(Math.min(childSizing.height().max(), lineCrossSize - m.top() - m.bottom()));
                    else child.setWidth(Math.min(childSizing.width().max(), lineCrossSize - m.left() - m.right()));
                }
                
                if (isHorizontal) {
                    child.setX(isReversed ? -currentMainOffset - m.right() - child.getWidth() : currentMainOffset + m.left());
                    child.setY(currentCrossOffset + m.top());
                    currentMainOffset += m.left() + child.getWidth() + m.right() + layout.childGap();
                } else {
                    child.setX(currentCrossOffset + m.left());
                    child.setY(isReversed ? -currentMainOffset - m.bottom() - child.getHeight() : currentMainOffset + m.top());
                    currentMainOffset += m.top() + child.getHeight() + m.bottom() + layout.childGap();
                }
                calculatePositions(child);
            }
            currentCrossOffset += lineCrossSize + layout.childGap();
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
        if (decl == null) return 0;
        if (decl.aspectRatio() != null) return decl.aspectRatio().aspectRatio();
        if (decl.image() != null) {
            Vector2f dims = dataSource.getImageDimensions(decl.image().imageIdentifier(), decl.image().atlasIdentifier());
            if (dims.x > 0 && dims.y > 0) return dims.x / dims.y;
        }
        return 0;
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
        BorderWidth border = (decl.border() != null) ? decl.border().width() : UIContext.DEFAULT_BORDER_WIDTH;

        if (sizing.width().type() == UI.SizingType.FIT) {
            float minX = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            boolean hasChildren = false;
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                Margin m = getMargin(child);
                minX = Math.min(minX, child.getX() - m.left());
                maxX = Math.max(maxX, child.getX() + child.getWidth() + m.right());
                hasChildren = true;
            }
            if (hasChildren) {
                element.setWidth(Math.max(sizing.width().min(), Math.min(sizing.width().max(), maxX - minX + padding.left() + padding.right() + border.left() + border.right())));
            }
        }

        if (sizing.height().type() == UI.SizingType.FIT) {
            float minY = Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            boolean hasChildren = false;
            for (LayoutElement child : element.children()) {
                if (child.declaration() != null && child.declaration().floating() != null) continue;
                Margin m = getMargin(child);
                minY = Math.min(minY, child.getY() - m.top());
                maxY = Math.max(maxY, child.getY() + child.getHeight() + m.bottom());
                hasChildren = true;
            }
            if (hasChildren) {
                element.setHeight(Math.max(sizing.height().min(), Math.min(sizing.height().max(), maxY - minY + padding.top() + padding.bottom() + border.top() + border.bottom())));
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
        BorderWidth border = (decl.border() != null) ? decl.border().width() : UIContext.DEFAULT_BORDER_WIDTH;
        ChildAlignment alignment = layout.childAlignment() != null ? layout.childAlignment() : UIContext.DEFAULT_ALIGNMENT;

        float innerWidth = element.getWidth() - padding.left() - padding.right() - border.left() - border.right();
        float innerHeight = element.getHeight() - padding.top() - padding.bottom() - border.top() - border.bottom();

        // Calculate bounding box of all non-floating children (they currently have relative positions)
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        boolean hasChildren = false;
        for (LayoutElement child : element.children()) {
            if (child.declaration() != null && child.declaration().floating() != null) continue;
            Margin m = getMargin(child);
            minX = Math.min(minX, child.getX() - m.left());
            minY = Math.min(minY, child.getY() - m.top());
            maxX = Math.max(maxX, child.getX() + child.getWidth() + m.right());
            maxY = Math.max(maxY, child.getY() + child.getHeight() + m.bottom());
            hasChildren = true;
        }

        if (hasChildren) {
            float contentWidth = maxX - minX;
            float contentHeight = maxY - minY;

            float offsetX = padding.left() + border.left();
            float offsetY = padding.top() + border.top();

            if (decl.clip() != null && decl.clip().childOffset() != null) {
                offsetX -= decl.clip().childOffset().x;
                offsetY -= decl.clip().childOffset().y;
            }

            offsetX += switch (alignment.x()) {
                case LEFT -> -minX;
                case CENTER -> (innerWidth - contentWidth) / 2f - minX;
                case RIGHT -> (innerWidth - contentWidth) - minX;
            };

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
