package com.kiemhiep.api.platform;

/**
 * Scheduler interface for task scheduling.
 */
public interface Scheduler {

    /**
     * Run a task on the main thread immediately.
     *
     * @param task The task to run
     */
    void runTask(Runnable task);

    /**
     * Run a task on the main thread after a delay.
     *
     * @param delayTicks The delay in ticks
     * @param task       The task to run
     * @return A task ID that can be used to cancel the task
     */
    Object runTaskLater(long delayTicks, Runnable task);

    /**
     * Run a task on the main thread repeatedly.
     *
     * @param delayTicks  The initial delay in ticks
     * @param intervalTicks The interval between runs in ticks
     * @param task        The task to run
     * @return A task ID that can be used to cancel the task
     */
    Object runTaskTimer(long delayTicks, long intervalTicks, Runnable task);

    /**
     * Run a task asynchronously.
     *
     * @param task The task to run
     */
    void runTaskAsync(Runnable task);

    /**
     * Run a task asynchronously after a delay.
     *
     * @param delayTicks The delay in ticks
     * @param task       The task to run
     * @return A task ID that can be used to cancel the task
     */
    Object runTaskLaterAsync(long delayTicks, Runnable task);

    /**
     * Run a task asynchronously repeatedly.
     *
     * @param delayTicks    The initial delay in ticks
     * @param intervalTicks The interval between runs in ticks
     * @param task          The task to run
     * @return A task ID that can be used to cancel the task
     */
    Object runTaskTimerAsync(long delayTicks, long intervalTicks, Runnable task);

    /**
     * Cancel a scheduled task.
     *
     * @param taskId The task ID to cancel
     */
    void cancelTask(Object taskId);

    /**
     * Check if a task is scheduled.
     *
     * @param taskId The task ID to check
     * @return true if the task is still scheduled
     */
    boolean isScheduled(Object taskId);
}
