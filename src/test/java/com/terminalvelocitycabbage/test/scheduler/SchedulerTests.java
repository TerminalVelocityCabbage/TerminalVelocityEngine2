package com.terminalvelocitycabbage.test.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler2;
import org.junit.jupiter.api.Test;

public class SchedulerTests {

    @Test
    void test() {
        Scheduler2 scheduler = new Scheduler2(60);

        var startTime = System.currentTimeMillis();

        scheduler.schedule(() -> System.out.println("Hello World"))
                .parallelSchedule(
                () -> {
                    System.out.println("1");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    System.out.println("2");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .schedule(() -> System.out.println("4"));

        for (int i = 0; i < 10; i++) {
            scheduler.tick();
        }

        Log.info(System.currentTimeMillis() - startTime);

        scheduler.shutDown();
    }
}
