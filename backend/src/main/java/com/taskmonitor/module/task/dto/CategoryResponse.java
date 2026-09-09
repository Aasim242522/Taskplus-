package com.taskmonitor.module.task.dto;

import java.time.Instant;
import java.util.UUID;

public class CategoryResponse {
    private UUID id;
    private String name;
    private String colorHex;
    private String icon;
    private boolean isSystem;
    private Instant createdAt;

    public CategoryResponse() {
    }

    public CategoryResponse(UUID id, String name, String colorHex, String icon, boolean isSystem, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
        this.icon = icon;
        this.isSystem = isSystem;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isSystem() { return isSystem; }
    public void setSystem(boolean system) { isSystem = system; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private UUID id;
        private String name;
        private String colorHex;
        private String icon;
        private boolean isSystem;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder colorHex(String colorHex) { this.colorHex = colorHex; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder isSystem(boolean isSystem) { this.isSystem = isSystem; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public CategoryResponse build() {
            return new CategoryResponse(id, name, colorHex, icon, isSystem, createdAt);
        }
    }
}
