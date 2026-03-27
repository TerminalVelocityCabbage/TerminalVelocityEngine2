package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.ecs.Entity;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimationControllerManagerTest {

    static class TestEntity extends Entity {
        private final UUID id;
        public TestEntity(UUID id) {
            super();
            this.id = id;
        }
        @Override
        public UUID getID() { return id; }
    }

    @Test
    public void testVector3fNPE() {
        AnimationControllerManager manager = new AnimationControllerManager();
        // This should not throw NPE during registration
        assertDoesNotThrow(() -> {
            manager.registerVariable("test", Vector3f.class, entity -> {
                if (entity == null) throw new NullPointerException("Entity is null!");
                return new Vector3f(1, 2, 3);
            });
        });
    }

    @Test
    public void testVector3fEvaluation() {
        AnimationControllerManager manager = new AnimationControllerManager();
        Vector3f vec1 = new Vector3f(1, 2, 3);
        Vector3f vec2 = new Vector3f(4, 5, 6);
        
        manager.registerVariable("pos", Vector3f.class, entity -> {
            if (entity == null) return new Vector3f(0, 0, 0); 
            return entity.getID().getMostSignificantBits() == 1 ? vec1 : vec2;
        });

        TestEntity entity1 = new TestEntity(new UUID(1, 0));
        TestEntity entity2 = new TestEntity(new UUID(2, 0));

        manager.setCurrentEntity(entity1);
        assertEquals(1.0, manager.compileExpression("pos.x").evaluate());
        assertEquals(2.0, manager.compileExpression("pos.y").evaluate());
        assertEquals(3.0, manager.compileExpression("pos.z").evaluate());

        manager.setCurrentEntity(entity2);
        assertEquals(4.0, manager.compileExpression("pos.x").evaluate());
        assertEquals(5.0, manager.compileExpression("pos.y").evaluate());
        assertEquals(6.0, manager.compileExpression("pos.z").evaluate());
    }
}
