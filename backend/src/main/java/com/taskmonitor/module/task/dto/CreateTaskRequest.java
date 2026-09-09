package com.taskmonitor.module.task.dto;

import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.RecurrenceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;
    private UUID categoryId;
    private Priority priority = Priority.MEDIUM;
    private RecurrenceType recurrenceType = RecurrenceType.NONE;
    private Instant dueDateTime;

    @Min(value = 0, message = "Estimated duration cannot be negative")
    private Integer estimatedDurationMinutes = 0;

    @Min(value = 1, message = "Difficulty level must be between 1 and 5")
    @Max(value = 5, message = "Difficulty level must be between 1 and 5")
    private Integer difficultyLevel = 2;

    private Set<String> tags;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String description, UUID categoryId, Priority priority, RecurrenceType recurrenceType, Instant dueDateTime, Integer estimatedDurationMinutes, Integer difficultyLevel, Set<String> tags) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.recurrenceType = recurrenceType != null ? recurrenceType : RecurrenceType.NONE;
        this.dueDateTime = dueDateTime;
        this.estimatedDurationMinutes = estimatedDurationMinutes != null ? estimatedDurationMinutes : 0;
        this.difficultyLevel = difficultyLevel != null ? difficultyLevel : 2;
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

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; }

    public Instant getDueDateTime() { return dueDateTime; }
    public void setDueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public static class Builder {
        private String title;
        private String description;
        private UUID categoryId;
        private Priority priority = Priority.MEDIUM;
        private RecurrenceType recurrenceType = RecurrenceType.NONE;
        private Instant dueDateTime;
        private Integer estimatedDurationMinutes = 0;
        private Integer difficultyLevel = 2;
        private Set<String> tags;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder categoryId(UUID categoryId) { this.categoryId = categoryId; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder recurrenceType(RecurrenceType recurrenceType) { this.recurrenceType = recurrenceType; return this; }
        public Builder dueDateTime(Instant dueDateTime) { this.dueDateTime = dueDateTime; return this; }
        public Builder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public Builder difficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }

        public CreateTaskRequest build() {
            return new CreateTaskRequest(title, description, categoryId, priority, recurrenceType, dueDateTime, estimatedDurationMinutes, difficultyLevel, tags);
        }
    }
}
