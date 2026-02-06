package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.ecs.ComponentFilter;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.RoutineSystemExecutionEvent;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ECSTests {

    Manager manager;
    
    private static final String TEST_NAMESPACE = "test";
    
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

        public Vector3f getVelocity() {
            return position;
        }

        public void setVelocity(Vector3f position) {
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
                entity.getComponent(PositionComponent.class).getPosition().add(1 * deltaTime, 0 * deltaTime, 0 * deltaTime);
            });
        }
    }

    public static class MoveEntitySystem2 extends System {

        @Override
        public void update(Manager manager, float deltaTime) {
            for (int i = 0; i < 10000; i++) {
                manager.getEntitiesWith(PositionComponent.class).forEach(entity -> {
                    entity.getComponent(PositionComponent.class).getPosition().add(2 * deltaTime, 0 * deltaTime, 0 * deltaTime);
                });
            }
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
        manager.getSystem(MoveEntitySystem.class).update(manager, 1);
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
    void createEntityFromTemplate() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        var templateIdentifier = new Identifier(TEST_NAMESPACE, "template", "template");
        var entityTemplate = manager.createEntityTemplate(templateIdentifier, entity -> {
            entity.addComponent(PositionComponent.class);
            entity.getComponent(PositionComponent.class).setPosition(new Vector3f(0, 1, 0));
            entity.addComponent(VelocityComponent.class);
        });
        var entity1 = manager.createEntityFromTemplate(templateIdentifier);
        var entity2 = manager.createEntityFromTemplate(templateIdentifier);
        assertNotEquals(entity1, entity2);
        assertTrue(entity1.containsComponent(PositionComponent.class));
        assertTrue(entity1.containsComponent(VelocityComponent.class));
        assertTrue(entity2.containsComponent(PositionComponent.class));
        assertTrue(entity2.containsComponent(VelocityComponent.class));
        assertNotEquals(entity1.getComponent(PositionComponent.class), entity2.getComponent(PositionComponent.class));
        assertNotEquals(entity1.getComponent(PositionComponent.class), entity2.getComponent(PositionComponent.class));
        assertEquals(entity1.getComponent(PositionComponent.class).getPosition(), entity2.getComponent(PositionComponent.class).getPosition());
        assertEquals(entity1.getComponent(VelocityComponent.class).getVelocity(), entity2.getComponent(VelocityComponent.class).getVelocity());
    }

    @Test
    void createEntityFromAnotherEntity() {
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        entity1.getComponent(PositionComponent.class).setPosition(new Vector3f(0, 1, 0));
        entity1.addComponent(VelocityComponent.class);
        var entity2 = manager.duplicateEntity(entity1);
        assertNotEquals(entity1, entity2);
        assertTrue(entity2.containsComponent(PositionComponent.class));
        assertTrue(entity2.containsComponent(VelocityComponent.class));
        assertNotEquals(entity1.getComponent(PositionComponent.class), entity2.getComponent(PositionComponent.class));
    }

    @Test
    void relationshipAddQuery() {
        manager.registerRelationshipType("hates");
        var entity1 = manager.createEntity();
        var entity2 = manager.createEntity();
        manager.addRelationship(entity1, "hates", entity2);
        var entitiesThatHateEntity2 = manager.getAllEntitiesThat("hates", entity2);
        assertEquals(entitiesThatHateEntity2.stream().findFirst().get(), entity1);
        var entitiesThatEntity1Hates = manager.getAllEntitiesThat(entity1, "hates");
        assertEquals(entitiesThatEntity1Hates.stream().findFirst().get(), entity2);
    }

    @Test
    void freeEntityClearsRelationships() {
        manager.registerRelationshipType("hates");
        var entity1 = manager.createEntity();
        var entity2 = manager.createEntity();
        manager.addRelationship(entity1, "hates", entity2);
        assertEquals(1, manager.getEntityRelationships().get("hates").size());
        assertEquals(1, manager.getInverseEntityRelationships().get("hates").size());
        manager.freeEntity(entity2);
        assertEquals(0, manager.getEntityRelationships().get("hates").get(entity1).size());
        assertNull(manager.getInverseEntityRelationships().get("hates").get(entity1));
    }

    @Test
    void removeRelationship() {
        manager.registerRelationshipType("hates");
        var entity1 = manager.createEntity();
        var entity2 = manager.createEntity();
        manager.addRelationship(entity1, "hates", entity2);
        assertEquals(1, manager.getAllEntitiesThat(entity1, "hates").size());
        manager.removeRelationship(entity1, "hates", entity2);
        assertEquals(0, manager.getAllEntitiesThat(entity1, "hates").size());
    }

    @Test
    void emptyRelationshipQuery() {
        manager.registerRelationshipType("hates");
        var entity1 = manager.createEntity();
        assertEquals(0, manager.getAllEntitiesThat(entity1, "hates").size());
    }

    @Test
    void hasRelationship() {
        manager.registerRelationshipType("hates");
        var entity1 = manager.createEntity();
        var entity2 = manager.createEntity();
        manager.addRelationship(entity1, "hates", entity2);
        assertTrue(manager.entityHasRelationship(entity1, "hates", entity2));
        assertFalse(manager.entityHasRelationship(entity2, "hates", entity1));
    }

    @Test
    void testBasicSystemRoutineOperateOnEntity() {

        var dummyEventDispatcher = new EventDispatcher();
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.createSystem(MoveEntitySystem.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        //This entity should be ignored without exceptions
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);

        //Create Routine
        var routine = Routine.builder(TEST_NAMESPACE, "testRoutine").addStep(new Identifier(TEST_NAMESPACE, "system", "testSystem1"), MoveEntitySystem.class).build();

        routine.update(manager, dummyEventDispatcher, 10);

        assertEquals(new Vector3f(10, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());

    }

    @Test
    void testMultiStepSystemRoutineOperateOnEntity() {

        var dummyEventDispatcher = new EventDispatcher();
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.createSystem(MoveEntitySystem.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        //This entity should be ignored without exceptions
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);

        //Create Routine
        var routine = Routine.builder(TEST_NAMESPACE, "testRoutine")
                .addStep(new Identifier(TEST_NAMESPACE, "system", "testSystem1"), MoveEntitySystem.class)
                .addStep(new Identifier(TEST_NAMESPACE, "system", "testSystem2"), MoveEntitySystem.class)
                .build();

        routine.update(manager, dummyEventDispatcher, 10);

        assertEquals(new Vector3f(20, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());

    }

    @Test
    void testParallelStepSystemRoutineOperateOnEntity() {

        var dummyEventDispatcher = new EventDispatcher();
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.createSystem(MoveEntitySystem.class);
        manager.createSystem(MoveEntitySystem2.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        //This entity should be ignored without exceptions
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);

        //Create Routine
        var routine = Routine.builder(TEST_NAMESPACE, "testRoutine")
                .addParallelStep(new Identifier(TEST_NAMESPACE, "system", "testSystem1"), MoveEntitySystem.class, MoveEntitySystem2.class)
                .build();

        routine.update(manager, dummyEventDispatcher, 10);

        assertEquals(new Vector3f(200010, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());

    }

    @Test
    void testParallelStepSystemRoutineOperateOnEntityOrdered() {

        var dummyEventDispatcher = new EventDispatcher();
        manager.registerComponent(PositionComponent.class);
        manager.registerComponent(VelocityComponent.class);
        manager.createSystem(MoveEntitySystem.class);
        manager.createSystem(MoveEntitySystem2.class);
        var entity1 = manager.createEntity();
        entity1.addComponent(PositionComponent.class);
        //This entity should be ignored without exceptions
        var entity2 = manager.createEntity();
        entity2.addComponent(VelocityComponent.class);

        List<String> executedSteps = new ArrayList<>();

        var id1 = dummyEventDispatcher.listenToEvent(RoutineSystemExecutionEvent.post(new Identifier(TEST_NAMESPACE, "system", "testSystem0")), (event) -> {
            executedSteps.add("testSystem0");
            Log.info("testSystem0 post");
            assertEquals(new Vector3f(10, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());
        });
        var id2 = dummyEventDispatcher.listenToEvent(RoutineSystemExecutionEvent.post(new Identifier(TEST_NAMESPACE, "system", "testSystem1")), (event) -> {
            executedSteps.add("testSystem1");
            Log.info("testSystem1 post");
            assertEquals(new Vector3f(200020, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());
        });
        var id3 = dummyEventDispatcher.listenToEvent(RoutineSystemExecutionEvent.post(new Identifier(TEST_NAMESPACE, "system", "testSystem2")), (event) -> {
            executedSteps.add("testSystem2");
            Log.info("testSystem2 post");
            assertEquals(new Vector3f(200030, 0, 0), entity1.getComponent(PositionComponent.class).getPosition());
        });

        //Create Routine
        var routine = Routine.builder(TEST_NAMESPACE, "testRoutine")
                .addStep(new Identifier(TEST_NAMESPACE, "system", "testSystem0"), MoveEntitySystem.class)
                .addParallelStep(new Identifier(TEST_NAMESPACE, "system", "testSystem1"), MoveEntitySystem.class, MoveEntitySystem2.class)
                .addStep(new Identifier(TEST_NAMESPACE, "system", "testSystem2"), MoveEntitySystem.class)
                .build();

        routine.update(manager, dummyEventDispatcher, 10);

        assertEquals(executedSteps.get(0), "testSystem0");
        assertEquals(executedSteps.get(1), "testSystem1");
        assertEquals(executedSteps.get(2), "testSystem2");

        assertEquals(executedSteps.size(), 3);

        dummyEventDispatcher.removeEventListener(id1);
        dummyEventDispatcher.removeEventListener(id2);
        dummyEventDispatcher.removeEventListener(id3);

    }
}
