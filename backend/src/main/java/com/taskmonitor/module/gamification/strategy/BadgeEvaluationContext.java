package com.taskmonitor.module.gamification.strategy;

import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.user.domain.User;

public class BadgeEvaluationContext {
    private final User user;
    private final Task task;
    private final int currentStreak;
    private final long totalCompletedTasks;

    public BadgeEvaluationContext(User user, Task task, int currentStreak, long totalCompletedTasks) {
        this.user = user;
        this.task = task;
        this.currentStreak = currentStreak;
        this.totalCompletedTasks = totalCompletedTasks;
    }

    public User getUser() { return user; }
    public Task getTask() { return task; }
    public int getCurrentStreak() { return currentStreak; }
    public long getTotalCompletedTasks() { return totalCompletedTasks; }
}
