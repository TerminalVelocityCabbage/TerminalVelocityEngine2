package com.terminalvelocitycabbage.editor.rendernodes;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.state.State;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.engine.util.StringUtils;
import com.terminalvelocitycabbage.templates.ecs.components.NameComponent;
import com.terminalvelocitycabbage.templates.events.UIClickEvent;

import java.util.Map;

import static com.terminalvelocitycabbage.engine.client.ui.UI.LayoutDirection.TOP_TO_BOTTOM;
import static com.terminalvelocitycabbage.engine.client.ui.UI.UIUnit.PIXELS;

public class DrawEditorUIRenderNode extends EditorUIRenderNode {

    private Scene currentScene;

    public DrawEditorUIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {
        this.currentScene = scene;
        super.execute(scene, properties, renderConfig, deltaTime);
    }

    @Override
    protected void declareUI() {

        State<Entity> selectedEntity = useState("selected_entity", null);
        State<Identifier> selectedAsset = useState("selected_asset", null);

        container(props(UI.pT(10, PIXELS), UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(TOP_TO_BOTTOM)), () -> {
            //optionsBar();
            container(props(UI.gap(5, PIXELS), UI.grow(), UI.backgroundColor(BACKGROUND_COLOR), UI.layout(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
                hierarchy(selectedEntity);
                container(props(UI.gap(5, PIXELS), UI.backgroundColor(BACKGROUND_COLOR), UI.grow(), UI.layout(TOP_TO_BOTTOM)), () -> {
                    scene();
                    browser(selectedEntity, selectedAsset);
                });
                inspector(selectedEntity, selectedAsset);
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

    private void hierarchy(State<Entity> selectedEntity) {
        container(props(
                UI.growY(), UI.width(240, PIXELS), UI.backgroundColor(ELEMENT_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("hierarchyTabs",
                    new Tab("Scene Hierarchy", () -> {
                        if (currentScene != null) {
                            verticalScrollableContainer("scene_tree_container", () -> {
                                for (Entity entity : Editor.getInstance().getManager().getEntities()) {
                                    entitySelector(selectedEntity, entity);
                                }
                            });
                        } else {
                            text("No scene active", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                        }
                    })
            );
        });
    }

    private void entitySelector(State<Entity> selectedEntity, Entity entity) {
        int buttonID = id(entity.getID().toString() + "_button");
        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) {
            selectedEntity.setValue(entity);
            Log.info("Selected entity: " + entity.getID());
        }

        boolean isSelected = selectedEntity.getValue() != null && selectedEntity.getValue().getID().equals(entity.getID());
        String name = entity.getID().toString();
        if (entity.hasComponent(NameComponent.class)) {
            name = entity.getComponent(NameComponent.class).getName();
        }
        text(buttonID, name, props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(isSelected ? TEXT_COLOR : LABEL_COLOR)));
    }

    private void commands() {
        container(props(
                UI.fit(), UI.backgroundColor(BACKGROUND_COLOR), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT),
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
                UI.grow(), UI.backgroundColor(TRANSPARENT)
        ), () -> {
            tabbedMenu("sceneTabs",
                    new Tab("3D", () -> {
                        container(props(UI.grow(), UI.backgroundColor(TRANSPARENT)), () -> {
                            commands();
                        });
                    }),
                    new Tab("2D", () -> {
                        container(props(UI.grow(), UI.backgroundColor(TRANSPARENT)), () -> {
                            commands();
                        });
                    })
            );
        });
    }

    private void browser(State<Entity> selectedEntity, State<Identifier> selectedAsset) {
        container(props(
                UI.growX(), UI.height(240, PIXELS), UI.backgroundColor(BACKGROUND_COLOR)
        ), () -> {
            tabbedMenu("browserTabs",
                    new Tab("Filesystem", () -> assetBrowser(selectedEntity, selectedAsset))
            );
        });
    }

    private void assetBrowser(State<Entity> selectedEntity, State<Identifier> selectedAsset) {

        State<ResourceCategory> selectedCategory = useState(null);

        Editor editor = (Editor) Editor.getInstance();
        GameFileSystem fileSystem = editor.getFileSystem();

        container(props(UI.backgroundColor(BORDER_COLOR), UI.grow(), UI.gap(5, PIXELS), UI.direction(UI.LayoutDirection.LEFT_TO_RIGHT)), () -> {
            container(props(UI.width(200, PIXELS), UI.growY(),UI.backgroundColor(ELEMENT_COLOR), UI.layout(TOP_TO_BOTTOM)), () -> {
                verticalScrollableContainer("asset_category_container", () -> {
                    for (ResourceCategory category : fileSystem.getResourceCategoryRegistry().getRegistryContents().values()) {
                        resourceCategorySelector(selectedCategory, category);
                    }
                });
            });
            container(props(UI.grow(), UI.backgroundColor(ELEMENT_COLOR)), () -> {
                verticalScrollableContainer("asset_container_by_category", () -> {
                    if (selectedCategory.getValue() != null) {
                        Map<Identifier, Resource> resourcesOfType = fileSystem.getResourcesOfType(selectedCategory.getValue());
                        for (Identifier resourceIdentifier : resourcesOfType.keySet()) {
                            assetSelector(selectedAsset, resourceIdentifier);
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

    private void assetSelector(State<Identifier> selectedAsset, Identifier identifier) {
        int buttonID = id(identifier.toString() + "_button");
        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) selectedAsset.setValue(identifier);

        boolean isSelected = selectedAsset.getValue() != null && selectedAsset.getValue().equals(identifier);
        text(buttonID, identifier.toString(), props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(isSelected ? TEXT_COLOR : LABEL_COLOR)));
    }

    private void resourceCategorySelector(State<ResourceCategory> selectedCategory, ResourceCategory category) {
        int buttonID = id(category.name() + "_button");
        if (heardEvent(buttonID, UIClickEvent.EVENT) instanceof UIClickEvent) selectedCategory.setValue(category);

        text(buttonID, StringUtils.convertSnakeCaseToCapitalized(category.plural()), props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(TEXT_COLOR)));
    }

    private void inspector(State<Entity> selectedEntity, State<Identifier> selectedAsset) {
        container(props(
                UI.growY(), UI.width(340, PIXELS), UI.backgroundColor(ELEMENT_COLOR),
                UI.gap(5, PIXELS)
        ), () -> {
            tabbedMenu("inspectorTabs",
                    new Tab("Element Inspector", () -> elementInspector(selectedEntity)),
                    new Tab("State Inspector", () -> {
                        text("State Inspector TODO", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
                    })
            );
        });
    }

    private void elementInspector(State<Entity> selectedEntity) {
        container(props(UI.grow(), UI.direction(TOP_TO_BOTTOM)), () -> {
            if (selectedEntity.getValue() != null) {
                for (Class<?> componentClass : selectedEntity.getValue().getComponents().keySet()) {
                    component(componentClass);
                    horizontalDivider();
                }
            } else {
                text("Select an entity to view its components", props(UI.font(REGULAR_FONT), UI.textSize(15, PIXELS), UI.textColor(LABEL_COLOR)));
            }
        });
    }
}
