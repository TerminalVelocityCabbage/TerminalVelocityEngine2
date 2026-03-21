package com.terminalvelocitycabbage.editor.rendernodes;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.State;
import com.terminalvelocitycabbage.engine.util.StringUtils;
import com.terminalvelocitycabbage.templates.events.UIClickEvent;

import java.util.Map;

import static com.terminalvelocitycabbage.engine.client.ui.UI.LayoutDirection.TOP_TO_BOTTOM;
import static com.terminalvelocitycabbage.engine.client.ui.UI.UIUnit.PIXELS;

public class DrawEditorUIRenderNode extends EditorUIRenderNode {

    public DrawEditorUIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    protected void declareUI() {

        container(props(UI.pT(10, PIXELS), UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(TOP_TO_BOTTOM)), () -> {
            //optionsBar();
            container(props(UI.gap(5, PIXELS), UI.grow(), UI.backgroundColor(BACKGROUND_COLOR), UI.layout(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
                hierarchy();
                container(props(UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(TOP_TO_BOTTOM)), () -> {
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

    private void hierarchy() {
        container(props(
                UI.growY(), UI.width(240, PIXELS), UI.backgroundColor(ELEMENT_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("hierarchyTabs",
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
            tabbedMenu("sceneTabs",
                    new Tab("3D", () -> {
                        container(props(UI.grow(), UI.backgroundColor(BORDER_COLOR)), () -> {
                            commands();
                        });
                    }),
                    new Tab("2D", () -> {
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
                    new Tab("Filesystem", this::assetBrowser)
            );
        });
    }

    private void assetBrowser() {

        State<ResourceCategory> selectedCategory = useState(null);

        Editor editor = (Editor) Editor.getInstance();
        GameFileSystem fileSystem = editor.getFileSystem();

        container(props(UI.backgroundColor(BORDER_COLOR), UI.grow(), UI.gap(5, PIXELS), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
            container(props(UI.width(200, PIXELS), UI.growY(),UI.backgroundColor(ELEMENT_COLOR), UI.layout(TOP_TO_BOTTOM)), () -> {
                verticalScrollableContainer("asset_category_container", () -> {
                    //TODO make this a button to update the state above
                    for (ResourceCategory category : fileSystem.getResourceCategoryRegistry().getRegistryContents().values()) {
                        resourceCategorySelector(selectedCategory, category);
                    }
                });
            });
            container(props(UI.grow(), UI.backgroundColor(ELEMENT_COLOR)), () -> {
                verticalScrollableContainer("asset_container_by_category", () -> {
                    if (selectedCategory.getValue() != null) {
                        Map<Identifier, Resource> resourcesOfType = fileSystem.getResourcesOfType(selectedCategory.getValue());
                        //TODO make this update a state for the currently previewed element in the viewport
                        for (Identifier resourceIdentifier : resourcesOfType.keySet()) {
                            text(resourceIdentifier.toString(), props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(TEXT_COLOR)));
                        }
                        if (resourcesOfType.isEmpty()) {
                            text("No " + selectedCategory.getValue().plural() + " registered to this filesystem", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                        }
                    } else {
                        text("Select a resource category to view resources", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                    }
                });
            });
        });
    }

    private void resourceCategorySelector(State<ResourceCategory> selectedCategory, ResourceCategory category) {
        int buttonID = id(category.name() + "_button");
        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) selectedCategory.setValue(category);

        text(buttonID, StringUtils.convertSnakeCaseToCapitalized(category.plural()), props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(TEXT_COLOR)));
    }

    private void inspector() {
        container(props(
                UI.growY(), UI.width(340, PIXELS), UI.backgroundColor(ELEMENT_COLOR),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("inspectorTabs",
                    new Tab("Element Inspector", this::elementInspector),
                    new Tab("State Inspector", () -> {
                        text("State Inspector TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                    })
            );
        });
    }

    private void elementInspector() {
        container(props(UI.grow(), UI.direction(TOP_TO_BOTTOM)), () -> {
            //TODO change this to show the components of the selected entity
            for (Class<?> componentC : Editor.getInstance().getManager().getComponents()) {
                component(componentC);
                horizontalDivider();
            }
        });
    }
}
