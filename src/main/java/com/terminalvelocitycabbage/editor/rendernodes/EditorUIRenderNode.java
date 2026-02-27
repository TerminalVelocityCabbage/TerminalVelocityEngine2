package com.terminalvelocitycabbage.editor.rendernodes;

import com.terminalvelocitycabbage.editor.registry.EditorFonts;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.UIRenderNode;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.State;
import com.terminalvelocitycabbage.engine.util.Color;
import com.terminalvelocitycabbage.templates.events.UICharInputEvent;
import com.terminalvelocitycabbage.templates.events.UIClickEvent;
import com.terminalvelocitycabbage.templates.events.UIScrollEvent;

import static com.terminalvelocitycabbage.engine.client.ui.UI.UIUnit.PIXELS;

public abstract class EditorUIRenderNode extends UIRenderNode {

    public static final Color BLACK = Color.ofHex("#000000");
    public static final Color ULTRA_DARK = Color.ofHex("#191919");
    public static final Color DARK = Color.ofHex("#262626");
    public static final Color MEDIUM = Color.ofHex("#303030");
    public static final Color MEDIUM_LIGHT = Color.ofHex("#383838");
    public static final Color LIGHT = Color.ofHex("#474747");
    public static final Color ULTRALIGHT = Color.ofHex("#fafafa");
    public static final Color WHITE = Color.ofHex("#ffffff");

    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    public static final Color LIGHT_RED = Color.ofHex("#ef4444");
    public static final Color RED = Color.ofHex("#dc2626");
    public static final Color DARK_RED = Color.ofHex("#b91c1c");

    public static final Color LIGHT_ORANGE = Color.ofHex("#f97316");
    public static final Color ORANGE = Color.ofHex("#ea580c");
    public static final Color DARK_ORANGE = Color.ofHex("#c2410c");

    public static final Color LIGHT_YELLOW = Color.ofHex("#fbbf24");
    public static final Color YELLOW = Color.ofHex("#f59e0b");
    public static final Color DARK_YELLOW = Color.ofHex("#d97706");

    public static final Color LIGHT_GREEN = Color.ofHex("#84cc16");
    public static final Color GREEN = Color.ofHex("#65a30d");
    public static final Color DARK_GREEN = Color.ofHex("#4d7c0f");

    public static final Color LIGHT_BLUE = Color.ofHex("#0ea5e9");
    public static final Color BLUE = Color.ofHex("#0284c7");
    public static final Color DARK_BLUE = Color.ofHex("#0369a1");

    public static final Color LIGHT_PURPLE = Color.ofHex("#8b5cf6");
    public static final Color PURPLE = Color.ofHex("#7c3aed");
    public static final Color DARK_PURPLE = Color.ofHex("#6d28d9");

    public static final Color BACKGROUND_COLOR = BLACK;
    public static final Color BORDER_COLOR = DARK;
    public static final Color ELEMENT_COLOR = MEDIUM;
    public static final Color FIELD_COLOR = DARK;
    public static final Color TEXT_COLOR = WHITE;
    public static final Color LABEL_COLOR = ULTRALIGHT;

    public static final Identifier REGULAR_FONT = EditorFonts.REGULAR;

    public EditorUIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    protected Identifier[] getInterestedEvents() {
        return new Identifier[]{
                UIClickEvent.EVENT,
                UIScrollEvent.EVENT,
                UICharInputEvent.EVENT
        };
    }

    public record Tab(String name, Runnable action) {}

    public void tabbedMenu(String menuName, Tab... tabs) {

        int tabbedMenuID = id(menuName);

        State<Integer> selectedTab = useState(0);

        // Overall container for tabs
        container(tabbedMenuID, props(
                UI.grow(), UI.backgroundColor(BACKGROUND_COLOR), UI.direction(UI.LayoutDirection.TOP_TO_BOTTOM)
        ), () -> {
            //Tabs
            container(props(
                    UI.growX(), UI.fitY(), UI.backgroundColor(BACKGROUND_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                    UI.gap(5, PIXELS)
            ), () -> {
                for (int i = 0; i < tabs.length; i++) {
                    if (tab(tabs[i].name(), i == selectedTab.getValue())) selectedTab.setValue(i);
                }
            });
            //Selected tab content
            container(props(
                    UI.grow(), UI.backgroundColor(ELEMENT_COLOR)
            ), () -> tabs[selectedTab.getValue()].action().run());
        });
    }

    private boolean tab(String tabName, boolean selected) {

        int tabID = id("tab_" + tabName);
        boolean hovered = isHovered(tabID);

        container(tabID, props(
                UI.height(24, PIXELS), UI.backgroundColor((selected || hovered) ? ELEMENT_COLOR : BORDER_COLOR), UI.pY(4, PIXELS), UI.pX(10, PIXELS),
                UI.roundedT(5)
        ), () -> {
            text(tabName, props(
                UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(selected ? TEXT_COLOR : LABEL_COLOR)
            ));
        });

        return heardEvent(tabID, UIClickEvent.EVENT) instanceof UIClickEvent;
    }

    public void iconButton(Identifier icon, Identifier atlas, Runnable action) {

        int buttonID = id("icon_button_" + icon.toString());

        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) action.run();

        container(buttonID, props(
            UI.height(24, PIXELS), UI.width(24, PIXELS), UI.backgroundColor(ELEMENT_COLOR), UI.rounded(5),
                UI.border(2), UI.borderColor(MEDIUM_LIGHT), UI.p(3, PIXELS)
        ), () -> {
            image(props(
                UI.width(16, PIXELS), UI.height(16, PIXELS), UI.backgroundColor(TRANSPARENT),
                    UI.bgImage(icon, atlas), UI.imageBackground(TRANSPARENT)
            ));
        });
    }

    public void button(int buttonID, String label, Runnable action) {

        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) action.run();

        container(buttonID, props(
                UI.height(24, PIXELS), UI.fitX(), UI.backgroundColor(ELEMENT_COLOR), UI.rounded(5),
                UI.border(2), UI.borderColor(MEDIUM_LIGHT), UI.p(3, PIXELS)
        ), () -> {
            text(label, props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(TEXT_COLOR)));
        });
    }
}
