package com.taskmonitor.module.gamification.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "streaks", indexes = {
        @Index(name = "idx_streaks_user_id", columnList = "user_id")
})
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Column(nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(nullable = false)
    private Integer freezeCredits = 0;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Streak() {
    }

    public Streak(UUID id, User user, Integer currentStreak, Integer longestStreak, LocalDate lastActivityDate, Integer freezeCredits) {
        this.id = id;
        this.user = user;
        this.currentStreak = currentStreak != null ? currentStreak : 0;
        this.longestStreak = longestStreak != null ? longestStreak : 0;
        this.lastActivityDate = lastActivityDate;
        this.freezeCredits = freezeCredits != null ? freezeCredits : 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getLongestStreak() { return longestStreak; }
    public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public Integer getFreezeCredits() { return freezeCredits; }
    public void setFreezeCredits(Integer freezeCredits) { this.freezeCredits = freezeCredits; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private Integer currentStreak = 0;
        private Integer longestStreak = 0;
        private LocalDate lastActivityDate;
        private Integer freezeCredits = 0;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder currentStreak(Integer currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(Integer longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder lastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; return this; }
        public Builder freezeCredits(Integer freezeCredits) { this.freezeCredits = freezeCredits; return this; }

        public Streak build() {
            return new Streak(id, user, currentStreak, longestStreak, lastActivityDate, freezeCredits);
        }
    }
}
