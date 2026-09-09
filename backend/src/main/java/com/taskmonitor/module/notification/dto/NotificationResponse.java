package com.taskmonitor.module.notification.dto;

import com.taskmonitor.module.notification.domain.NotificationStatus;
import com.taskmonitor.module.notification.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {

    private UUID id;
    private String title;
    private String message;
    private NotificationType notificationType;
    private NotificationStatus status;
    private Instant readAt;
    private Instant createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(UUID id, String title, String message, NotificationType notificationType, NotificationStatus status, Instant readAt, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.status = status;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private UUID id;
        private String title;
        private String message;
        private NotificationType notificationType;
        private NotificationStatus status;
        private Instant readAt;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder notificationType(NotificationType notificationType) { this.notificationType = notificationType; return this; }
        public Builder status(NotificationStatus status) { this.status = status; return this; }
        public Builder readAt(Instant readAt) { this.readAt = readAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponse build() {
            return new NotificationResponse(id, title, message, notificationType, status, readAt, createdAt);
        }
    }
}
