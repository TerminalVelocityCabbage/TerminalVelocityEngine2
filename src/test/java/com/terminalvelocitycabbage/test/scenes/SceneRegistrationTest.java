package com.terminalvelocitycabbage.test.scenes;

import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.templates.events.SceneRegistrationEvent;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SceneRegistrationTest {

    public static class TestComponent implements Component {
        public String name;
        @Override
        public void parseComponentField(String field, String value) {
            if (field.equals("name")) this.name = value;
        }
        @Override public void setDefaults() { this.name = "default"; }
    }

    @Test
    public void testSceneRegistrationFromFile() {
        Registry<Scene> sceneRegistry = new Registry<>();
        Registry<Routine> routineRegistry = new Registry<>();
        
        // Register a dummy routine
        Identifier routineId = new Identifier("test", "routine", "test_routine");
        Routine dummyRoutine = Routine.builder("test", "test_routine").build();
        routineRegistry.register(routineId, dummyRoutine);

        Manager manager = new Manager();
        manager.registerComponent(TestComponent.class);

        GameFileSystem fileSystem = new GameFileSystem() {
            @Override
            public Resource getResource(ResourceCategory resourceCategory, Identifier identifier) {
                if (resourceCategory == ResourceCategory.SCENE && identifier.name().equals("test.scene.toml")) {
                    return new Resource() {
                        @Override public InputStream openStream() throws IOException { return null; }
                        @Override public DataInputStream asDataStream() { return null; }
                        @Override public ByteBuffer asByteBuffer(boolean keepAlive) { return null; }
                        @Override
                        public String asString() {
                            return "[scene]\n" +
                                    "name = \"test_scene\"\n" +
                                    "render_graph = \"test:render_graph\"\n" +
                                    "routines = [\"test:test_routine\"]\n" +
                                    "input_controllers = [\"test:controller1\"]\n" +
                                    "texture_atlases = [\"test:atlas1\"]\n" +
                                    "\n" +
                                    "[[entities]]\n" +
                                    "[entities.component.test]\n" +
                                    "name = \"entity1\"";
                        }
                    };
                }
                return null;
            }
        };

        SceneRegistrationEvent event = new SceneRegistrationEvent(sceneRegistry, fileSystem, routineRegistry);
        Identifier sceneId = event.registerSceneFromFile("test", "test");

        assertNotNull(sceneId);
        assertEquals(new Identifier("test", "scene", "test_scene"), sceneId);

        Scene scene = sceneRegistry.get(sceneId);
        assertNotNull(scene);
        assertEquals(new Identifier("test", "render_graph", "render_graph"), scene.getRenderGraph());

        assertEquals(1, scene.getRoutines().size());
        assertEquals(dummyRoutine, scene.getRoutines().get(0));

        assertEquals(1, scene.getInputControllers().size());
        assertEquals(new Identifier("test", "controller", "controller1"), scene.getInputControllers().get(0));

        assertEquals(1, scene.getTextureAtlases().size());
        assertEquals(new Identifier("test", "atlas", "atlas1"), scene.getTextureAtlases().get(0));

        // Test entities
        assertEquals(1, scene.getEntityInitializers().size());
        scene.getEntityInitializers().get(0).accept(manager);

        Entity entity = manager.getEntities().iterator().next();
        assertTrue(entity.hasComponent(TestComponent.class));
        assertEquals("entity1", entity.getComponent(TestComponent.class).name);
    }
}
