package com.terminalvelocitycabbage.editor.rendernodes;

import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.debug.Log;

import static com.terminalvelocitycabbage.engine.client.ui.UI.UIUnit.PIXELS;

public class DrawEditorUIRenderNode extends EditorUIRenderNode {

    public DrawEditorUIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    protected void declareUI() {

        container(props(UI.pT(10, PIXELS), UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(UI.LayoutDirection.TOP_TO_BOTTOM)), () -> {
            //optionsBar();
            container(props(UI.gap(5, PIXELS), UI.grow(), UI.backgroundColor(BACKGROUND_COLOR), UI.layout(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
                sceneTree();
                container(props(UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(UI.LayoutDirection.TOP_TO_BOTTOM)), () -> {
                    scene();
                    browser();
                });
                inspector();
            });
        });
    }

    //TODO this might be useful in the future, but for now it's really not needed
    private void optionsBar() {
        container(props(
                UI.growX(), UI.fitY(), UI.backgroundColor(BACKGROUND_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                UI.p(5, PIXELS), UI.gap(5, PIXELS)
        ), () -> {
            text("Options Bar", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
        });
    }

    private void sceneTree() {
        container(props(
                UI.growY(), UI.width(240, PIXELS), UI.backgroundColor(ELEMENT_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("sceneTabs",
                    new Tab("Scene Hierarchy", () -> {
                        text("Scene tree TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                    })
            );
        });
    }

    private void commands() {
        container(props(
                UI.fit(), UI.backgroundColor(TRANSPARENT), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                UI.gap(5, PIXELS), UI.floatParent(), UI.attachTo(UI.FloatingAttachPointType.TOP_LEFT, UI.FloatingAttachPointType.TOP_LEFT),
                UI.floatOffsetX(5, PIXELS), UI.floatOffsetY(5, PIXELS)
        ), () -> {
            iconButton(EditorTextures.TRANSLATE_ICON, EditorTextures.UI_ATLAS, () -> Log.info("Translate button pressed"));
            iconButton(EditorTextures.ROTATE_ICON, EditorTextures.UI_ATLAS, () -> Log.info("Rotate button pressed"));
            iconButton(EditorTextures.SCALE_ICON, EditorTextures.UI_ATLAS, () -> Log.info("Scale button pressed"));
        });
    }

    private void scene() {
        container(props(
                UI.grow(), UI.backgroundColor(BACKGROUND_COLOR)
        ), () -> {
            tabbedMenu("browserTabs",
                    new Tab("Asset Browser", () -> {
                        container(props(UI.grow(), UI.backgroundColor(BORDER_COLOR)), () -> {
                            commands();
                        });
                    })
            );
        });
    }

    private void browser() {
        container(props(
                UI.growX(), UI.height(240, PIXELS), UI.backgroundColor(BACKGROUND_COLOR)
        ), () -> {
            tabbedMenu("browserTabs",
                    new Tab("Asset Browser", this::assetBrowser)
            );
        });
    }

    private void assetBrowser() {
        container(props(UI.backgroundColor(ELEMENT_COLOR), UI.grow(), UI.gap(5, PIXELS), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
            container(props(UI.width(240, PIXELS), UI.growY(),UI.backgroundColor(ELEMENT_COLOR)), () -> {
                text("Asset Filesystem TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
            });
            container(props(UI.grow(), UI.backgroundColor(ELEMENT_COLOR)), () -> {
                text("Asset List TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
            });
        });
    }

    private void inspector() {
        container(props(
                UI.growY(), UI.width(240, PIXELS), UI.backgroundColor(ELEMENT_COLOR),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("inspectorTabs",
                    new Tab("Inspector", () -> {
                        text("Inspector TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                    })
            );
        });
    }
}
