package com.taskmonitor.module.analytics.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_task_metrics", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_metric_date", columnNames = {"user_id", "metric_date"})
}, indexes = {
        @Index(name = "idx_daily_metrics_lookup", columnList = "user_id, metric_date")
})
public class DailyTaskMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "tasks_created", nullable = false)
    private Integer tasksCreated = 0;

    @Column(name = "tasks_completed", nullable = false)
    private Integer tasksCompleted = 0;

    @Column(name = "tasks_overdue", nullable = false)
    private Integer tasksOverdue = 0;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "total_duration_minutes", nullable = false)
    private Integer totalDurationMinutes = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public DailyTaskMetric() {
    }

    public DailyTaskMetric(UUID id, User user, LocalDate metricDate, Integer tasksCreated, Integer tasksCompleted, Integer tasksOverdue, Integer pointsEarned, Integer totalDurationMinutes) {
        this.id = id;
        this.user = user;
        this.metricDate = metricDate;
        this.tasksCreated = tasksCreated != null ? tasksCreated : 0;
        this.tasksCompleted = tasksCompleted != null ? tasksCompleted : 0;
        this.tasksOverdue = tasksOverdue != null ? tasksOverdue : 0;
        this.pointsEarned = pointsEarned != null ? pointsEarned : 0;
        this.totalDurationMinutes = totalDurationMinutes != null ? totalDurationMinutes : 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getMetricDate() { return metricDate; }
    public void setMetricDate(LocalDate metricDate) { this.metricDate = metricDate; }

    public Integer getTasksCreated() { return tasksCreated; }
    public void setTasksCreated(Integer tasksCreated) { this.tasksCreated = tasksCreated; }

    public Integer getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public Integer getTasksOverdue() { return tasksOverdue; }
    public void setTasksOverdue(Integer tasksOverdue) { this.tasksOverdue = tasksOverdue; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private LocalDate metricDate;
        private Integer tasksCreated = 0;
        private Integer tasksCompleted = 0;
        private Integer tasksOverdue = 0;
        private Integer pointsEarned = 0;
        private Integer totalDurationMinutes = 0;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder metricDate(LocalDate metricDate) { this.metricDate = metricDate; return this; }
        public Builder tasksCreated(Integer tasksCreated) { this.tasksCreated = tasksCreated; return this; }
        public Builder tasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; return this; }
        public Builder tasksOverdue(Integer tasksOverdue) { this.tasksOverdue = tasksOverdue; return this; }
        public Builder pointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; return this; }
        public Builder totalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; return this; }

        public DailyTaskMetric build() {
            return new DailyTaskMetric(id, user, metricDate, tasksCreated, tasksCompleted, tasksOverdue, pointsEarned, totalDurationMinutes);
        }
    }
}
