package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.Scheduler;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Fabric implementation of Scheduler.
 */
public class FabricScheduler implements Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(FabricScheduler.class);

    private final MinecraftServer server;
    private final ExecutorService asyncExecutor;
    private final ConcurrentHashMap<Object, Boolean> scheduledTasks;
    private long taskIdCounter = 0;

    public FabricScheduler(MinecraftServer server) {
        this.server = server;
        this.asyncExecutor = Executors.newFixedThreadPool(10);
        this.scheduledTasks = new ConcurrentHashMap<>();
    }

    @Override
    public void runTask(Runnable task) {
        if (server.isOnThread()) {
            task.run();
        } else {
            server.send(() -> task.run());
        }
    }

    @Override
    public Object runTaskLater(long delayTicks, Runnable task) {
        long taskId = nextTaskId();
        scheduledTasks.put(taskId, true);

        long delayMillis = delayTicks * 50; // 50ms per tick
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (scheduledTasks.getOrDefault(taskId, false)) {
                runTask(task);
                scheduledTasks.remove(taskId);
                scheduler.shutdown();
            }
        }, delayMillis, TimeUnit.MILLISECONDS);

        return taskId;
    }

    @Override
    public Object runTaskTimer(long delayTicks, long intervalTicks, Runnable task) {
        long taskId = nextTaskId();
        scheduledTasks.put(taskId, true);

        long delayMillis = delayTicks * 50;
        long intervalMillis = intervalTicks * 50;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (scheduledTasks.getOrDefault(taskId, false)) {
                runTask(task);
            } else {
                scheduler.shutdown();
            }
        }, delayMillis, intervalMillis, TimeUnit.MILLISECONDS);

        return taskId;
    }

    @Override
    public void runTaskAsync(Runnable task) {
        asyncExecutor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Error in async task", e);
            }
        });
    }

    @Override
    public Object runTaskLaterAsync(long delayTicks, Runnable task) {
        long taskId = nextTaskId();
        scheduledTasks.put(taskId, true);

        long delayMillis = delayTicks * 50;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (scheduledTasks.getOrDefault(taskId, false)) {
                asyncExecutor.submit(task);
                scheduledTasks.remove(taskId);
                scheduler.shutdown();
            }
        }, delayMillis, TimeUnit.MILLISECONDS);

        return taskId;
    }

    @Override
    public Object runTaskTimerAsync(long delayTicks, long intervalTicks, Runnable task) {
        long taskId = nextTaskId();
        scheduledTasks.put(taskId, true);

        long delayMillis = delayTicks * 50;
        long intervalMillis = intervalTicks * 50;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (scheduledTasks.getOrDefault(taskId, false)) {
                asyncExecutor.submit(task);
            } else {
                scheduler.shutdown();
            }
        }, delayMillis, intervalMillis, TimeUnit.MILLISECONDS);

        return taskId;
    }

    @Override
    public void cancelTask(Object taskId) {
        scheduledTasks.remove(taskId);
    }

    @Override
    public boolean isScheduled(Object taskId) {
        return scheduledTasks.getOrDefault(taskId, false);
    }

    private long nextTaskId() {
        return ++taskIdCounter;
    }

    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
