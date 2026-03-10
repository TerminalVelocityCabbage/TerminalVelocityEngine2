package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.ecs.components.TransformationComponent;
import com.terminalvelocitycabbage.templates.events.EntityTemplateRegistrationEvent;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntityTemplateRegistrationTest {

    public static class PositionComponent implements Component {
        Vector3f position;
        @Override public void setDefaults() { position = new Vector3f(); }
        public void setPosition(float x, float y, float z) { position.set(x, y, z); }
        public Vector3f getPosition() { return position; }

        @Override
        public void parseComponentField(String field, String value) {
            String cleanedValue = value.replace("[", "").replace("]", "");
            String[] split = cleanedValue.split(",");
            for (int i = 0; i < split.length; i++) split[i] = split[i].trim();
            switch (field) {
                case "position" -> {
                    if (split.length == 3) {
                        setPosition(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                    }
                }
            }
        }
    }

    @Test
    public void testCreateEntityTemplateFromFile() {
        Manager manager = new Manager();
        manager.registerComponent(TransformationComponent.class);

        GameFileSystem fileSystem = new GameFileSystem() {
            @Override
            public Resource getResource(ResourceCategory resourceCategory, Identifier identifier) {
                return new Resource() {
                    @Override
                    public InputStream openStream() throws IOException {
                        return null;
                    }

                    @Override
                    public DataInputStream asDataStream() {
                        return null;
                    }

                    @Override
                    public ByteBuffer asByteBuffer(boolean keepAlive) {
                        return null;
                    }

                    @Override
                    public String asString() {
                        return "name = \"test_entity\"\n" +
                                "[component.transformation]\n" +
                                "position = [1.0, 2.0, -3.0]";
                    }
                };
            }
        };

        EntityTemplateRegistrationEvent event = new EntityTemplateRegistrationEvent(manager, fileSystem);
        Identifier templateId = event.createEntityTemplateFromFile("test", "test_file");

        assertNotNull(templateId);
        Entity entity = manager.createEntityFromTemplate(templateId);
        assertNotNull(entity);

        TransformationComponent transform = entity.getComponent(TransformationComponent.class);
        assertNotNull(transform);
        assertEquals(1.0f, transform.getPosition().x);
        assertEquals(2.0f, transform.getPosition().y);
        assertEquals(-3.0f, transform.getPosition().z);
    }

}
