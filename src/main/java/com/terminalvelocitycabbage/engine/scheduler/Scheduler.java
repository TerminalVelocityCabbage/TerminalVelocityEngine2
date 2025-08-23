package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.touples.Triplet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Scheduler {

    public static Identifier DEFAULT_ASYNC_POOL;

    private final Map<Identifier, Triplet<Integer, Integer, ExecutorService>> executor; //Current, Max, Service
    private final Queue<Runnable> mainThreadTasks;
    private final PriorityQueue<ScheduledTask> scheduledTasks;

    public Scheduler(Identifier defaultAsyncPoolIdentifier, int defaultPoolThreads) {
        DEFAULT_ASYNC_POOL = defaultAsyncPoolIdentifier;
        this.executor = new HashMap<>();
        executor.put(DEFAULT_ASYNC_POOL, new Triplet<>(0, defaultPoolThreads, Executors.newFixedThreadPool(defaultPoolThreads)));
        this.mainThreadTasks = new ConcurrentLinkedQueue<>();
        this.scheduledTasks = new PriorityQueue<>(Comparator.comparingLong(t -> t.nextRun));
    }

    public Identifier registerPool(Identifier identifier, int threads) {
        executor.put(identifier, new Triplet<>(0, threads, Executors.newFixedThreadPool(threads)));
        return identifier;
    }

    private int getRemainingThreadsForPool(Identifier identifier) {
        var pool = executor.get(identifier);
        return pool.getValue1() - pool.getValue0();
    }

    private ExecutorService getExecutorForPool(Identifier identifier) {
        return executor.get(identifier).getValue2();
    }

    private void incrementCurrentThreadsForPool(Identifier identifier) {
        executor.get(identifier).setValue0(executor.get(identifier).getValue0() + 1);
    }

    private void decrementCurrentThreadsForPool(Identifier identifier) {
        executor.get(identifier).setValue0(executor.get(identifier).getValue0() - 1);
    }

    /**
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Runnable asyncWork) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork);
    }

    /**
     * @param pool The executor Service Pool to submit this task to
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Identifier pool, Runnable asyncWork) {
        return scheduleAsyncTask(pool, asyncWork, -1, null);
    }

    /**
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Runnable asyncWork, long timeoutMillis) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork, timeoutMillis);
    }

    /**
     * @param pool The executor Service Pool to submit this task to
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Identifier pool, Runnable asyncWork, long timeoutMillis) {
        return scheduleAsyncTask(pool, asyncWork, timeoutMillis, null);
    }

    /**
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param onTimeout What happens if this task is timed out before it completes
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Runnable asyncWork, long timeoutMillis, Runnable onTimeout) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork, timeoutMillis, onTimeout);
    }

    /**
     * @param asyncWork The Runnable, which should be executed off the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param onTimeout What happens if this task is timed out before it completes
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleAsyncTask(Identifier pool, Runnable asyncWork, long timeoutMillis, Runnable onTimeout) {
        AsyncTaskHandle<Void> handle = new AsyncTaskHandle<>(null);

        if (!(getRemainingThreadsForPool(pool) > 0)) {
            return null;
        } else {
            incrementCurrentThreadsForPool(pool);
        }

        Future<?> future = getExecutorForPool(pool).submit(() -> {
            try {
                if (!handle.isCancelled()) {
                    asyncWork.run();
                    handle.markDone();
                }
            } catch (Exception e) {
                Log.crash("Exception occurred while running async task", e);
            }
        });

        handle.setFuture(future);

        if (timeoutMillis > 0) {
            scheduleDelayedTask(() -> {
                if (!handle.isDone() && !handle.isCancelled()) {
                    handle.cancel();
                    decrementCurrentThreadsForPool(pool);
                    if (onTimeout != null) {
                        onTimeout.run();
                    }
                }
            }, timeoutMillis);
        }

        return handle;
    }

    /**
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork, onComplete, -1, null);
    }

    /**
     * @param pool The executor Service Pool to submit this task to
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Identifier pool, Supplier<T> asyncWork, Consumer<T> onComplete) {
        return scheduleAsyncTask(pool, asyncWork, onComplete, -1, null);
    }

    /**
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork, onComplete, timeoutMillis, null);
    }

    /**
     * @param pool The executor Service Pool to submit this task to
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Identifier pool, Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis) {
        return scheduleAsyncTask(pool, asyncWork, onComplete, timeoutMillis, null);
    }

    /**
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param onTimeout What happens if this task is timed out before it completes
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis, Runnable onTimeout) {
        return scheduleAsyncTask(DEFAULT_ASYNC_POOL, asyncWork, onComplete, timeoutMillis, onTimeout);
    }

    /**
     * @param pool The executor Service Pool to submit this task to
     * @param asyncWork The Supplier, which should be executed off the main thread
     * @param onComplete A Consumer which accepts the result of the async work and does something with it on the main thread
     * @param timeoutMillis The maximum time in milliseconds that this task can be working before we kill it
     * @param onTimeout What happens if this task is timed out before it completes
     * @param <T> The type of data that is generated and consumed by the asyncWork and onComplete runnable
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public <T> TaskHandle scheduleAsyncTask(Identifier pool, Supplier<T> asyncWork, Consumer<T> onComplete, long timeoutMillis, Runnable onTimeout) {

        AsyncTaskHandle<T> handle = new AsyncTaskHandle<>(null);

        if (!(getRemainingThreadsForPool(pool) > 0)) {
            return null;
        } else {
            incrementCurrentThreadsForPool(pool);
        }

        Future<?> future = getExecutorForPool(pool).submit(() -> {
            try {
                if (handle.isCancelled()) return;
                T result = asyncWork.get();
                if (onComplete != null && !handle.isCancelled()) {
                    decrementCurrentThreadsForPool(pool);
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
                decrementCurrentThreadsForPool(pool);
                if (onTimeout != null) onTimeout.run();
            }, timeoutMillis);
        }

        return handle;
    }

    /**
     * @param task A runnable that is executed after the specified delay
     * @param delayMillis the time in milliseconds before this task is executed
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
    public TaskHandle scheduleDelayedTask(Runnable task, long delayMillis) {
        return scheduleTask(task, delayMillis, -1);
    }

    /**
     * @param task A runnable that is executed once every 'intervalMillis' milliseconds
     * @param intervalMillis the time in milliseconds between each execution of this task
     * @return An {@link TaskHandle} representing this task in case the user needs to cancel it
     */
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
        executor.forEach((identifier, pool) -> {
            pool.getValue2().shutdown();
        });
    }
}

