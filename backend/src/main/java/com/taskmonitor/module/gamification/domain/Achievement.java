package com.taskmonitor.module.gamification.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "badges", indexes = {
        @Index(name = "idx_badges_code", columnList = "code")
})
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false, length = 30)
    private BadgeType badgeType;

    @Column(name = "criteria_metadata", columnDefinition = "TEXT", nullable = false)
    private String criteriaMetadata;

    @Column(name = "points_reward", nullable = false)
    private Integer pointsReward = 50;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Achievement() {
    }

    public Achievement(UUID id, String code, String name, String description, String iconUrl, BadgeType badgeType, String criteriaMetadata, Integer pointsReward) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.badgeType = badgeType;
        this.criteriaMetadata = criteriaMetadata;
        this.pointsReward = pointsReward != null ? pointsReward : 50;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public BadgeType getBadgeType() { return badgeType; }
    public void setBadgeType(BadgeType badgeType) { this.badgeType = badgeType; }

    public String getCriteriaMetadata() { return criteriaMetadata; }
    public void setCriteriaMetadata(String criteriaMetadata) { this.criteriaMetadata = criteriaMetadata; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private String iconUrl;
        private BadgeType badgeType;
        private String criteriaMetadata;
        private Integer pointsReward = 50;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder iconUrl(String iconUrl) { this.iconUrl = iconUrl; return this; }
        public Builder badgeType(BadgeType badgeType) { this.badgeType = badgeType; return this; }
        public Builder criteriaMetadata(String criteriaMetadata) { this.criteriaMetadata = criteriaMetadata; return this; }
        public Builder pointsReward(Integer pointsReward) { this.pointsReward = pointsReward; return this; }

        public Achievement build() {
            return new Achievement(id, code, name, description, iconUrl, badgeType, criteriaMetadata, pointsReward);
        }
    }
}
