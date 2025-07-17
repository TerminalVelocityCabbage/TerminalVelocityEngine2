package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.ComponentFilter;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ECSTests {

    Manager manager;

    public static class PositionComponent implements Component {

        Vector3f position;

        @Override
        public void setDefaults() {
            position = new Vector3f();
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }
    }

    public static class VelocityComponent implements Component {

        Vector3f position;

        @Override
        public void setDefaults() {
            position = new Vector3f();
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }
    }

    public static class BadComponent implements Component {

        @Override
        public void setDefaults() { }
    }

    public static class MoveEntitySystem extends System {

        @Override
        public void update(Manager manager, float deltaTime) {
            manager.getEntitiesWith(PositionComponent.class).forEach(entity -> {
                entity.getComponent(PositionComponent.class).getPosition().add(1, 0, 0);
            });
        }
    }

    @BeforeEach
    void setup() {
        manager = new Manager();
    }

    @AfterEach
    void reset() {
        manager = null;
    }

    @Test
    void createRemoveEntity() {
        var entity = manager.createEntity();
        manager.freeEntity(entity);
        assertFalse(manager.getEntities().contains(entity));
    }

    @Test
    void addComponent() {
        manager.registerComponent(PositionComponent.class);
        var entity = manager.createEntity();
        entity.addComponent(PositionComponent.class);
        assertNotNull(entity.getComponent(PositionComponent.class));
    }

    @Test
    void removeComponent() {
        manager.registerComponent(PositionComponent.class);
        var entity = manager.createEntity();
        entity.addComponent(PositionComponent.class);
        entity.removeComponent(PositionComponent.class);
        assertNull(entity.getComponent(PositionComponent.class));
    }

    @Test
    void getEntitiesSingle() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);
        entity2.addComponent(PositionComponent.class);
        var entities = manager.getEntitiesWith(PositionComponent.class);
        assertEquals(2, entities.size());
    }

    @Test
    void getEntitiesMulti() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);
        entity2.addComponent(PositionComponent.class);
        var entities = manager.getEntitiesWith(PositionComponent.class, VelocityComponent.class);
        assertEquals(1, entities.size());
    }

    @Test
    void getEntitiesFiltered() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.registerComponent(BadComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        entity1.addComponent(BadComponent.class);
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);
        entity2.addComponent(PositionComponent.class);
        var entities = manager.getEntitiesWith(PositionComponent.class);
        assertEquals(2, entities.size());
        ComponentFilter filter = ComponentFilter.builder().excludes(BadComponent.class).build();
        assertEquals(1, filter.filter(entities).size());
    }

    @Test
    void errorOnAddUnregisteredComponent() {
        var entity = manager.createEntity();
        assertThrows(RuntimeException.class, () -> entity.addComponent(PositionComponent.class));
    }

    @Test
    void getComponentDataAfterEntityQuery() {
        manager.registerComponent(PositionComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        entity1.getComponent(PositionComponent.class).setPosition(new Vector3f(0, 1, 0));
        var entity = manager.getFirstEntityWith(PositionComponent.class);
        assertEquals(new Vector3f(0, 1, 0), entity.getComponent(PositionComponent.class).getPosition());
    }

    @Test
    void systemOperateOnEntity() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.createSystem(MoveEntitySystem.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        //This entity should be ignored without exceptions
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);
        manager.getSystem(MoveEntitySystem.class).update(manager, 0);
        assertEquals(new Vector3f(1, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());
    }

    @Test
    void getEntityByID() {
        var id = manager.createEntity().getID();
        assertNotNull(manager.getEntityWithID(id));
    }

    @Test
    void entitiesIdsUnique() {
        var id1 = manager.createEntity().getID();
        var id2 = manager.createEntity().getID();
        assertNotEquals(id1, id2);
    }

    @Test
    void testFreePersistentEntities() {
        manager.registerComponent(PositionComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        var entity2 = manager.createEntity();
        entity2.addComponent(PositionComponent.class);
        entity2.setPersistent(true);
        assertEquals(2, manager.getEntities().size());
        manager.freeNonPersistentEntities();
        assertEquals(1, manager.getEntities().size());
        manager.freeEntity(entity2);
        assertEquals(0, manager.getEntities().size());
    }

    @Test
    void createEntitiesFromTemplate() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        entity1.getComponent(PositionComponent.class).setPosition(new Vector3f(0, 1, 0));
        entity1.addComponent(VelocityComponent.class);
        var entity2 = manager.createEntity(entity1);
        assertNotEquals(entity1, entity2);
        assertTrue(entity2.containsComponent(PositionComponent.class));
        assertTrue(entity2.containsComponent(VelocityComponent.class));
        assertNotEquals(entity1.getComponent(PositionComponent.class), entity2.getComponent(PositionComponent.class));
    }
}
