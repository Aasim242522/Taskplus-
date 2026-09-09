package com.taskmonitor.module.task.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_tasks_user_status", columnList = "user_id, status"),
        @Index(name = "idx_tasks_user_due_date", columnList = "user_id, due_date_time"),
        @Index(name = "idx_tasks_user_completed_at", columnList = "user_id, completed_at"),
        @Index(name = "idx_tasks_status_due_date", columnList = "status, due_date_time"),
        @Index(name = "idx_tasks_user_created_at", columnList = "user_id, created_at")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    @Column(name = "due_date_time")
    private Instant dueDateTime;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes = 0;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes = 0;

    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel = 2;

    @Column(name = "parent_recurring_task_id")
    private UUID parentRecurringTaskId;

    @org.hibernate.annotations.BatchSize(size = 25)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Task() {
    }

    public Task(UUID id, User user, Category category, String title, String description, Priority priority,
                TaskStatus status, RecurrenceType recurrenceType, Instant dueDateTime, Instant completedAt,
                Integer estimatedDurationMinutes, Integer actualDurationMinutes, Integer difficultyLevel,
                UUID parentRecurringTaskId, Set<Tag> tags, Integer version) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.recurrenceType = recurrenceType != null ? recurrenceType : RecurrenceType.NONE;
        this.dueDateTime = dueDateTime;
        this.completedAt = completedAt;
        this.estimatedDurationMinutes = estimatedDurationMinutes != null ? estimatedDurationMinutes : 0;
        this.actualDurationMinutes = actualDurationMinutes != null ? actualDurationMinutes : 0;
        this.difficultyLevel = difficultyLevel != null ? difficultyLevel : 2;
        this.parentRecurringTaskId = parentRecurringTaskId;
        this.tags = tags != null ? tags : new HashSet<>();
        this.version = version != null ? version : 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

    public Instant getDueDateTime() { return dueDateTime; }
    public void setDueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public Integer getActualDurationMinutes() { return actualDurationMinutes; }
    public void setActualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; }

    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public UUID getParentRecurringTaskId() { return parentRecurringTaskId; }
    public void setParentRecurringTaskId(UUID parentRecurringTaskId) { this.parentRecurringTaskId = parentRecurringTaskId; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private Category category;
        private String title;
        private String description;
        private Priority priority = Priority.MEDIUM;
        private TaskStatus status = TaskStatus.PENDING;
        private RecurrenceType recurrenceType = RecurrenceType.NONE;
        private Instant dueDateTime;
        private Instant completedAt;
        private Integer estimatedDurationMinutes = 0;
        private Integer actualDurationMinutes = 0;
        private Integer difficultyLevel = 2;
        private UUID parentRecurringTaskId;
        private Set<Tag> tags = new HashSet<>();
        private Integer version = 0;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder status(TaskStatus status) { this.status = status; return this; }
        public Builder recurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; return this; }
        public Builder dueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public Builder actualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; return this; }
        public Builder difficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public Builder parentRecurringTaskId(UUID parentRecurringTaskId) { this.parentRecurringTaskId = parentRecurringTaskId; return this; }
        public Builder tags(Set<Tag> tags) { this.tags = tags; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public Task build() {
            return new Task(id, user, category, title, description, priority, status, recurrenceType, dueDateTime,
                    completedAt, estimatedDurationMinutes, actualDurationMinutes, difficultyLevel, parentRecurringTaskId,
                    tags, version);
        }
    }
}
