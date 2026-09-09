package com.taskmonitor.module.task.dto;

import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.RecurrenceType;
import com.taskmonitor.module.task.domain.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UpdateTaskRequest {

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;
    private UUID categoryId;
    private Priority priority;
    private TaskStatus status;
    private RecurrenceType recurrenceType;
    private Instant dueDateTime;

    @Min(value = 0, message = "Estimated duration cannot be negative")
    private Integer estimatedDurationMinutes;

    @Min(value = 0, message = "Actual duration cannot be negative")
    private Integer actualDurationMinutes;

    @Min(value = 1, message = "Difficulty level must be between 1 and 5")
    @Max(value = 5, message = "Difficulty level must be between 1 and 5")
    private Integer difficultyLevel;

    private Set<String> tags;

    public UpdateTaskRequest() {
    }

    public UpdateTaskRequest(String title, String description, UUID categoryId, Priority priority, TaskStatus status, RecurrenceType recurrenceType, Instant dueDateTime, Integer estimatedDurationMinutes, Integer actualDurationMinutes, Integer difficultyLevel, Set<String> tags) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.priority = priority;
        this.status = status;
        this.recurrenceType = recurrenceType;
        this.dueDateTime = dueDateTime;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.actualDurationMinutes = actualDurationMinutes;
        this.difficultyLevel = difficultyLevel;
        this.tags = tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

    public Instant getDueDateTime() { return dueDateTime; }
    public void setDueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public Integer getActualDurationMinutes() { return actualDurationMinutes; }
    public void setActualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; }

    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public static class Builder {
        private String title;
        private String description;
        private UUID categoryId;
        private Priority priority;
        private TaskStatus status;
        private RecurrenceType recurrenceType;
        private Instant dueDateTime;
        private Integer estimatedDurationMinutes;
        private Integer actualDurationMinutes;
        private Integer difficultyLevel;
        private Set<String> tags;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder categoryId(UUID categoryId) { this.categoryId = categoryId; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder status(TaskStatus status) { this.status = status; return this; }
        public Builder recurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; return this; }
        public Builder dueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; return this; }
        public Builder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public Builder actualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; return this; }
        public Builder difficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }

        public UpdateTaskRequest build() {
            return new UpdateTaskRequest(title, description, categoryId, priority, status, recurrenceType, dueDateTime, estimatedDurationMinutes, actualDurationMinutes, difficultyLevel, tags);
        }
    }
}
