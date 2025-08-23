package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulerTest {

    Scheduler scheduler = null;

    @BeforeEach
    void setup() {
        scheduler = new Scheduler(4);
    }

    @AfterEach
    void reset() {
        scheduler.shutdown();
        scheduler = null;
    }

    @Test
    void testDelayedTaskExecution() throws InterruptedException {

        List<String> returnValues = new ArrayList<>();

        returnValues.add("delayed task 0");
        Log.info( "delayed task 0" );
        var task1 = scheduler.scheduleDelayedTask(() -> {
            returnValues.add("delayed task 1");
            Log.info( "delayed task 1" );
        }, 10);
        returnValues.add("delayed task 2");
        Log.info( "delayed task 2" );

        int iterations = 0;
        while (iterations < 15) {
            scheduler.update();
            Thread.sleep(1);
            iterations++;
        }

        assertEquals("delayed task 0", returnValues.get(0));
        assertEquals("delayed task 2", returnValues.get(1));
        assertEquals("delayed task 1", returnValues.get(2));

    }

    @Test
    void testRepeatingTaskExecution() throws InterruptedException {

        long now = System.currentTimeMillis();
        List<String> returnValues = new ArrayList<>();

        var task1 = scheduler.scheduleRepeatingTask(() -> {
            returnValues.add("repeated task 1");
            Log.info( "repeated task 1: " + System.currentTimeMillis() );
        }, 500);

        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            Thread.sleep(1);
        }

        assertEquals(3, returnValues.size());

    }

    @Test
    void testTaskCancellation() throws InterruptedException {

        long now = System.currentTimeMillis();
        List<String> returnValues = new ArrayList<>();

        var task1 = scheduler.scheduleRepeatingTask(() -> {
            returnValues.add("canceled task 1");
            Log.info( "canceled task 1: " + System.currentTimeMillis() );
        }, 500);

        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            Thread.sleep(1);
        }

        task1.cancel();

        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            Thread.sleep(1);
        }

        assertEquals(3, returnValues.size());

    }

    @Test
    void testAsyncTask() throws InterruptedException {

        long now = System.currentTimeMillis();
        List<Integer> returnValues = new ArrayList<>();

        var task1 = scheduler.scheduleRepeatingTask(() -> {
            returnValues.add(0);
            Log.info( "repeated task 2: " + System.currentTimeMillis() );
        }, 500);

        var task2 = scheduler.scheduleAsyncTask(
                () -> {
                    try {
                        Log.info("started async task 1");
                        Thread.sleep(700);
                    } catch (InterruptedException ignored) { }
                    return new int[]{1, 2, 3, 4, 5};
                },
                ( something ) -> {
                    returnValues.add(something[2]);
                    Log.info("async task 1: " + System.currentTimeMillis() );
                }
        );

        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            Thread.sleep(1);
        }

        assertEquals(4, returnValues.size());
        assertEquals(3, returnValues.get(1));

    }

    @Test
    void testCancelAsyncTask() throws InterruptedException {

        long now = System.currentTimeMillis();
        List<Integer> returnValues = new ArrayList<>();

        var task1 = scheduler.scheduleRepeatingTask(() -> {
            returnValues.add(0);
            Log.info( "repeated task 2: " + System.currentTimeMillis() );
        }, 500);

        var task2 = scheduler.scheduleAsyncTask(
                () -> {
                    try {
                        Log.info("started async task 1");
                        Thread.sleep(700);
                    } catch (InterruptedException ignored) { }
                    return new int[]{1, 2, 3, 4, 5};
                },
                ( something ) -> {
                    returnValues.add(something[2]);
                    Log.info("async task 1: " + System.currentTimeMillis() );
                }
        );

        boolean canceled = false;
        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            if (!canceled && System.currentTimeMillis() > now + 600) {
                canceled = true;
                task2.cancel();
                Log.info("canceled async task 1");
            }
            Thread.sleep(1);
        }

        assertEquals(3, returnValues.size());

    }

    @Test
    void testTimeoutAsyncTask() throws InterruptedException {

        long now = System.currentTimeMillis();
        List<Integer> returnValues = new ArrayList<>();

        var task2 = scheduler.scheduleAsyncTask(
                () -> {
                    try {
                        Log.info("started async task 1");
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) { }
                    return new int[]{1, 2, 3, 4, 5};
                },
                ( something ) -> {
                    returnValues.add(something[2]);
                    returnValues.add(something[3]);
                    Log.info("async task 1: " + System.currentTimeMillis() );
                },
                1200,
                () -> {
                    Log.info("async task 1 timed out");
                    returnValues.add(69420);
                }
        );

        while (System.currentTimeMillis() < now + (500 * 3.1)) {
            scheduler.update();
            Thread.sleep(1);
        }

        assertEquals(1, returnValues.size());
        assertTrue(returnValues.contains(69420));

    }

}
