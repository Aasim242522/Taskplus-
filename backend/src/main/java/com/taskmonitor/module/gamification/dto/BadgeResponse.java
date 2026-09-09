package com.taskmonitor.module.gamification.dto;

import com.taskmonitor.module.gamification.domain.BadgeType;

import java.time.Instant;
import java.util.UUID;

public class BadgeResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private BadgeType badgeType;
    private Integer pointsReward;
    private boolean unlocked;
    private Instant unlockedAt;
    private int progress;
    private int target;

    public BadgeResponse() {
    }

    public BadgeResponse(UUID id, String code, String name, String description, String iconUrl, BadgeType badgeType, Integer pointsReward, boolean unlocked, Instant unlockedAt, int progress, int target) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.badgeType = badgeType;
        this.pointsReward = pointsReward;
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
        this.progress = progress;
        this.target = target;
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

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public Instant getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(Instant unlockedAt) { this.unlockedAt = unlockedAt; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }

    public static class Builder {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private String iconUrl;
        private BadgeType badgeType;
        private Integer pointsReward;
        private boolean unlocked;
        private Instant unlockedAt;
        private int progress;
        private int target;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder iconUrl(String iconUrl) { this.iconUrl = iconUrl; return this; }
        public Builder badgeType(BadgeType badgeType) { this.badgeType = badgeType; return this; }
        public Builder pointsReward(Integer pointsReward) { this.pointsReward = pointsReward; return this; }
        public Builder unlocked(boolean unlocked) { this.unlocked = unlocked; return this; }
        public Builder unlockedAt(Instant unlockedAt) { this.unlockedAt = unlockedAt; return this; }
        public Builder progress(int progress) { this.progress = progress; return this; }
        public Builder target(int target) { this.target = target; return this; }

        public BadgeResponse build() {
            return new BadgeResponse(id, code, name, description, iconUrl, badgeType, pointsReward, unlocked, unlockedAt, progress, target);
        }
    }
}
