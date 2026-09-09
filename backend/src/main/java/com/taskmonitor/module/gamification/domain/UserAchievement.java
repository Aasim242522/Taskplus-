package com.taskmonitor.module.gamification.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_badges", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_badge", columnNames = {"user_id", "badge_id"})
}, indexes = {
        @Index(name = "idx_user_badges_user", columnList = "user_id")
})
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false)
    private Achievement achievement;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant unlockedAt;

    public UserAchievement() {
    }

    public UserAchievement(UUID id, User user, Achievement achievement) {
        this.id = id;
        this.user = user;
        this.achievement = achievement;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }

    public Instant getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(Instant unlockedAt) { this.unlockedAt = unlockedAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private Achievement achievement;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder achievement(Achievement achievement) { this.achievement = achievement; return this; }
        public UserAchievement build() {
            return new UserAchievement(id, user, achievement);
        }
    }
}
