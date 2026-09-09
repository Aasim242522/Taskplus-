package com.taskmonitor.module.task.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_user_id", columnList = "user_id")
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7)
    private String colorHex = "#6366F1";

    @Column(length = 50)
    private String icon = "folder";

    @Column(nullable = false)
    private boolean isSystem = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Category() {
    }

    public Category(UUID id, User user, String name, String colorHex, String icon, boolean isSystem) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.colorHex = colorHex != null ? colorHex : "#6366F1";
        this.icon = icon != null ? icon : "folder";
        this.isSystem = isSystem;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
        private User user;
        private String name;
        private String colorHex = "#6366F1";
        private String icon = "folder";
        private boolean isSystem = false;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder colorHex(String colorHex) { this.colorHex = colorHex; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder isSystem(boolean isSystem) { this.isSystem = isSystem; return this; }
        public Category build() {
            return new Category(id, user, name, colorHex, icon, isSystem);
        }
    }
}
