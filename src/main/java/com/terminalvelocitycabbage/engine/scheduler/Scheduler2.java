package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scheduler2 {

    private final int timeoutSeconds;
    private final Map<Runnable, Single> taskMap = new HashMap<>();
    private final List<Task> tasks = new ArrayList<>();
    private final ExecutorService executorService;
    private final ForkJoinPool forkJoinPool;
    private final StampedLock scheduleLock = new StampedLock();
    private final ReentrantLock tickLock = new ReentrantLock();

    public Scheduler2(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        var threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                return new SchedulerThread(r);
            }
        };
        executorService = Executors.newSingleThreadExecutor(threadFactory);
        int nThreads = Runtime.getRuntime().availableProcessors();
        forkJoinPool = (ForkJoinPool) Executors.newWorkStealingPool(nThreads);
    }

    public Scheduler2 schedule(Runnable system) {
        long stamp = scheduleLock.writeLock();
        try {
            taskMap.computeIfAbsent(system, sys -> {
                Single single = new Single(sys);
                tasks.add(single);
                return single;
            });
            return this;
        } finally {
            scheduleLock.unlockWrite(stamp);
        }
    }

    public Scheduler2 parallelSchedule(Runnable... systems) {
        long stamp = scheduleLock.writeLock();
        try {
            switch (systems.length) {
                case 0:
                    return this;
                case 1:
                    schedule(systems[0]);
                default: {
                    var cluster = new Cluster(systems);
                    tasks.add(cluster);
                    taskMap.putAll(cluster.taskMap);
                }
            }
            return this;
        } finally {
            scheduleLock.unlockWrite(stamp);
        }
    }

    private void forkAndJoin(Runnable subsystem) {
        Thread currentThread = Thread.currentThread();
        if (!(currentThread instanceof SchedulerThread || currentThread instanceof ForkJoinWorkerThread)) {
            throw new IllegalCallerException("Cannot invoke the forkAndJoin() method from outside other systems.");
        }
        try {
            forkJoinPool.invoke(new RecursiveAction() {
                @Override
                protected void compute() {
                    subsystem.run();
                }
            });
        } catch (RuntimeException ex) {
            Log.error(ex.getMessage());
        }
    }

    public void suspend(Runnable system) {
        Single singleTask = taskMap.get(system);
        if (singleTask == null) {
            return;
        }
        singleTask.setEnabled(false);
    }

    public void resume(Runnable system) {
        Single singleTask = taskMap.get(system);
        if (singleTask == null) {
            return;
        }
        singleTask.setEnabled(true);
    }

    public void tick() {
        tickLock.lock();
        try {
            var futures = executorService.invokeAll(tasks);
            futures.getFirst().get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            Log.error(ex.getMessage());
        } finally {
            tickLock.unlock();
        }
    }

    public boolean shutDown() {
        executorService.shutdown();
        forkJoinPool.shutdown();
        try {
            return executorService.awaitTermination(
                    timeoutSeconds, TimeUnit.SECONDS) &&
                    forkJoinPool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Log.error(ex.getMessage());
        }
        return false;
    }

    private interface Task extends Callable<Void> { }

    private static final class SchedulerThread extends Thread {
        private static final AtomicInteger counter = new AtomicInteger(0);

        public SchedulerThread(Runnable runnable) {
            super(runnable, "tve-scheduler-" + counter.getAndIncrement());
        }
    }

    private final class Single implements Task {
        private final Runnable system;
        private final AtomicBoolean enabled = new AtomicBoolean(true);

        public Single(Runnable system) {
            this.system = system;
        }

        public Runnable getSystem() {
            return system;
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        @Override
        public Void call() {
            if (isEnabled()) {
                forkAndJoin(system);
            }
            return null;
        }

        private void directRun() {
            if (isEnabled()) {
                system.run();
            }
        }
    }

    private final class Cluster implements Task {
        private final List<Single> tasks;
        private final Map<Runnable, Single> taskMap;

        private Cluster(Runnable[] systems) {
            tasks = Arrays.stream(systems).map(Single::new).toList();
            taskMap = tasks.stream().collect(Collectors.toMap(Single::getSystem, Function.identity()));
        }

        @Override
        public Void call() {
            forkAndJoin(() -> ForkJoinTask.invokeAll(tasks.stream().map(single -> new RecursiveAction() {

                @Override
                protected void compute() {
                    single.directRun();
                }
            }).toArray(ForkJoinTask[]::new)));
            return null;
        }
    }

}
