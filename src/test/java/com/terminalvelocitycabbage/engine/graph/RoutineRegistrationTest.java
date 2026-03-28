package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.templates.events.RoutineRegistrationEvent;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoutineRegistrationTest {

    private static final List<String> executionOrder = Collections.synchronizedList(new ArrayList<>());

    public static class TestSystem1 extends System {
        @Override public void update(Manager manager, float deltaTime) {
            executionOrder.add("test1");
        }
    }

    public static class TestSystem2 extends System {
        @Override public void update(Manager manager, float deltaTime) {
            executionOrder.add("test2");
        }
    }

    public static class TestSystem3 extends System {
        @Override public void update(Manager manager, float deltaTime) {
            executionOrder.add("test3");
        }
    }

    @Test
    public void testSystemNameMapping() {
        Manager manager = new Manager();
        manager.createSystem(TestSystem1.class);
        assertEquals(TestSystem1.class, manager.getSystemClass("test1"));

        manager.createSystem(TestSystem2.class);
        assertEquals(TestSystem2.class, manager.getSystemClass("test2"));
    }

    @Test
    public void testRoutineRegistrationFromFile() {
        Manager manager = new Manager();
        manager.createSystem(TestSystem1.class); // This registers "test1" (getSimpleName().toLowerCase().replace("system", ""))
        manager.createSystem(TestSystem2.class); // "test2"
        manager.createSystem(TestSystem3.class); // "test3"

        GameFileSystem fileSystem = new GameFileSystem() {
            @Override
            public Resource getResource(ResourceCategory resourceCategory, Identifier identifier) {
                if (resourceCategory == ResourceCategory.ROUTINE && identifier.name().equals("test")) {
                    return new Resource() {
                        @Override public InputStream openStream() throws IOException { return null; }
                        @Override public DataInputStream asDataStream() { return null; }
                        @Override public ByteBuffer asByteBuffer(boolean keepAlive) { return null; }
                        @Override
                        public String asString() {
                            return "[routine]\n" +
                                    "name = \"test\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step1\"\n" +
                                    "system = \"test1\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step2\"\n" +
                                    "parallel = [\"test2\", \"test3\"]";
                        }
                    };
                }
                return null;
            }
        };

        Registry<Routine> routineRegistry = new Registry<>();
        RoutineRegistrationEvent event = new RoutineRegistrationEvent(routineRegistry, manager, fileSystem);
        Routine routine = event.registerRoutineFromFile("test", "test");

        assertNotNull(routine);
        assertEquals(new Identifier("test", "routine", "test"), routine.getIdentifier());

        Map<Identifier, Routine.Step> steps = routine.getSteps();
        assertEquals(2, steps.size());

        Identifier step1Id = new Identifier("test", "routine_step", "step1");
        Routine.Step step1 = steps.get(step1Id);
        assertTrue(step1 instanceof Routine.SequentialStep, "Step 1 should be SequentialStep");
        assertEquals(TestSystem1.class, ((Routine.SequentialStep) step1).system());

        Identifier step2Id = new Identifier("test", "routine_step", "step2");
        Routine.Step step2 = steps.get(step2Id);
        assertTrue(step2 instanceof Routine.ParallelStep, "Step 2 should be ParallelStep");
        Set<Class<? extends System>> parallelSystems = ((Routine.ParallelStep) step2).systems();
        assertEquals(2, parallelSystems.size());
        assertTrue(parallelSystems.contains(TestSystem2.class));
        assertTrue(parallelSystems.contains(TestSystem3.class));
    }

    @Test
    public void testExecutionOrder() {
        Manager manager = new Manager();
        manager.createSystem(TestSystem1.class);
        manager.createSystem(TestSystem2.class);
        manager.createSystem(TestSystem3.class);

        GameFileSystem fileSystem = new GameFileSystem() {
            @Override
            public Resource getResource(ResourceCategory resourceCategory, Identifier identifier) {
                if (resourceCategory == ResourceCategory.ROUTINE && identifier.name().equals("test_order")) {
                    return new Resource() {
                        @Override public InputStream openStream() throws IOException { return null; }
                        @Override public DataInputStream asDataStream() { return null; }
                        @Override public ByteBuffer asByteBuffer(boolean keepAlive) { return null; }
                        @Override
                        public String asString() {
                            return "[routine]\n" +
                                    "name = \"test_order\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step1\"\n" +
                                    "system = \"test1\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step2\"\n" +
                                    "system = \"test2\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step3\"\n" +
                                    "system = \"test3\"";
                        }
                    };
                }
                return null;
            }
        };

        Registry<Routine> routineRegistry = new Registry<>();
        RoutineRegistrationEvent event = new RoutineRegistrationEvent(routineRegistry, manager, fileSystem);
        Routine routine = event.registerRoutineFromFile("test", "test_order");

        assertNotNull(routine);
        executionOrder.clear();
        routine.update(manager, new EventDispatcher(), 10);

        assertEquals(3, executionOrder.size());
        assertEquals("test1", executionOrder.get(0));
        assertEquals("test2", executionOrder.get(1));
        assertEquals("test3", executionOrder.get(2));
    }

    @Test
    public void testExecutionOrderWithParallel() {
        Manager manager = new Manager();
        manager.createSystem(TestSystem1.class);
        manager.createSystem(TestSystem2.class);
        manager.createSystem(TestSystem3.class);

        GameFileSystem fileSystem = new GameFileSystem() {
            @Override
            public Resource getResource(ResourceCategory resourceCategory, Identifier identifier) {
                if (resourceCategory == ResourceCategory.ROUTINE && identifier.name().equals("test_parallel_order")) {
                    return new Resource() {
                        @Override public InputStream openStream() throws IOException { return null; }
                        @Override public DataInputStream asDataStream() { return null; }
                        @Override public ByteBuffer asByteBuffer(boolean keepAlive) { return null; }
                        @Override
                        public String asString() {
                            return "[routine]\n" +
                                    "name = \"test_parallel_order\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step1\"\n" +
                                    "system = \"test1\"\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step2\"\n" +
                                    "parallel = [\"test2\", \"test3\"]\n" +
                                    "\n" +
                                    "[[steps]]\n" +
                                    "id = \"test:step3\"\n" +
                                    "system = \"test1\"";
                        }
                    };
                }
                return null;
            }
        };

        Registry<Routine> routineRegistry = new Registry<>();
        RoutineRegistrationEvent event = new RoutineRegistrationEvent(routineRegistry, manager, fileSystem);
        Routine routine = event.registerRoutineFromFile("test", "test_parallel_order");

        assertNotNull(routine);
        executionOrder.clear();
        routine.update(manager, new EventDispatcher(), 10);

        assertEquals(4, executionOrder.size());
        assertEquals("test1", executionOrder.get(0));
        // index 1 and 2 can be test2 or test3 in any order
        assertTrue(executionOrder.get(1).equals("test2") || executionOrder.get(1).equals("test3"));
        assertTrue(executionOrder.get(2).equals("test2") || executionOrder.get(2).equals("test3"));
        assertNotEquals(executionOrder.get(1), executionOrder.get(2));
        assertEquals("test1", executionOrder.get(3));
    }
}
