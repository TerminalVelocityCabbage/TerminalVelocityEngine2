package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Scheduler {

    private final ExecutorService executor;
    private final Queue<Runnable> mainThreadTasks;
    private final PriorityQueue<ScheduledTask> scheduledTasks;

    public Scheduler(int workerThreads) {
        this.executor = Executors.newFixedThreadPool(workerThreads);
        this.mainThreadTasks = new ConcurrentLinkedQueue<>();
        this.scheduledTasks = new PriorityQueue<>(Comparator.comparingLong(t -> t.nextRun));
    }

    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork) {
        return scheduleAsyncTask(asyncWork, null, -1, null);
    }

    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete) {
        return scheduleAsyncTask(asyncWork, onComplete, -1, null);
    }

    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis) {
        return scheduleAsyncTask(asyncWork, onComplete, timeoutMillis, null);
    }

    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis, Runnable onTimeout) {

        AsyncTaskHandle<T> handle = new AsyncTaskHandle<>(null);

        Future<?> future = executor.submit(() -> {
            try {
                if (handle.isCancelled()) return;
                T result = asyncWork.get();
                if (onComplete != null && !handle.isCancelled()) {
                    mainThreadTasks.add(() -> {
                        if (!handle.isCancelled()) {
                            onComplete.accept(result);
                            handle.markDone();
                        }
                    });
                }
            } catch (Exception e) {
                Log.crash("Exception occurred while running async task", e);
            }
        });

        handle.setFuture(future);

        // Schedule watchdog timeout if one is needed
        if (timeoutMillis > 0) {
            scheduleDelayedTask(() -> {
                if (handle.isDone() || handle.isCancelled()) return;
                handle.cancel();
                if (onTimeout != null) onTimeout.run();
            }, timeoutMillis);
        }

        return handle;
    }


    public TaskHandle scheduleDelayedTask(Runnable task, long delayMillis) {
        return scheduleTask(task, delayMillis, -1);
    }

    public TaskHandle scheduleRepeatingTask(Runnable task, long intervalMillis) {
        return scheduleTask(task, intervalMillis, intervalMillis);
    }

    private TaskHandle scheduleTask(Runnable task, long delayMillis, long intervalMillis) {
        ScheduledTask st = new ScheduledTask(task, System.currentTimeMillis() + delayMillis, intervalMillis);
        ScheduledTaskHandle handle = new ScheduledTaskHandle(st);
        st.handle = handle;
        scheduledTasks.add(st);
        return handle;
    }

    public void update() {
        long now = System.currentTimeMillis();

        // Run due scheduled tasks
        while (!scheduledTasks.isEmpty() && scheduledTasks.peek().nextRun <= now) {
            ScheduledTask task = scheduledTasks.poll();
            if (task.handle.isCancelled()) scheduledTasks.remove(task);

            task.runnable.run();
            if (task.interval > 0) {
                task.nextRun = task.nextRun + task.interval;
                scheduledTasks.add(task);
            } else {
                task.handle().setDone(); // mark completed
            }
        }

        // Run any async task continuations
        Runnable continuation;
        while ((continuation = mainThreadTasks.poll()) != null) {
            continuation.run();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}

