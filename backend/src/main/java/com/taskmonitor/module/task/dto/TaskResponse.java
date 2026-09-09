package com.taskmonitor.module.task.dto;

import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.RecurrenceType;
import com.taskmonitor.module.task.domain.TaskStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private CategoryResponse category;
    private Priority priority;
    private TaskStatus status;
    private RecurrenceType recurrenceType;
    private Instant dueDateTime;
    private Instant completedAt;
    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;
    private Integer difficultyLevel;
    private Set<String> tags;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(UUID id, String title, String description, CategoryResponse category, Priority priority, TaskStatus status, RecurrenceType recurrenceType, Instant dueDateTime, Instant completedAt, Integer estimatedDurationMinutes, Integer actualDurationMinutes, Integer difficultyLevel, Set<String> tags, Integer version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.recurrenceType = recurrenceType;
        this.dueDateTime = dueDateTime;
        this.completedAt = completedAt;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.actualDurationMinutes = actualDurationMinutes;
        this.difficultyLevel = difficultyLevel;
        this.tags = tags;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CategoryResponse getCategory() { return category; }
    public void setCategory(CategoryResponse category) { this.category = category; }

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

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private UUID id;
        private String title;
        private String description;
        private CategoryResponse category;
        private Priority priority;
        private TaskStatus status;
        private RecurrenceType recurrenceType;
        private Instant dueDateTime;
        private Instant completedAt;
        private Integer estimatedDurationMinutes;
        private Integer actualDurationMinutes;
        private Integer difficultyLevel;
        private Set<String> tags;
        private Integer version;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(CategoryResponse category) { this.category = category; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder status(TaskStatus status) { this.status = status; return this; }
        public Builder recurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; return this; }
        public Builder dueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public Builder actualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; return this; }
        public Builder difficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public TaskResponse build() {
            return new TaskResponse(id, title, description, category, priority, status, recurrenceType, dueDateTime, completedAt, estimatedDurationMinutes, actualDurationMinutes, difficultyLevel, tags, version, createdAt, updatedAt);
        }
    }
}
