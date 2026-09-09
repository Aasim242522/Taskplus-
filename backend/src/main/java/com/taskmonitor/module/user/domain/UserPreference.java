package com.taskmonitor.module.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private boolean darkMode = false;

    @Column(nullable = false)
    private boolean emailNotifications = true;

    @Column(nullable = false)
    private boolean pushNotifications = true;

    @Column(nullable = false)
    private int dailyGoalTasks = 5;

    @Column(nullable = false, length = 10)
    private String weekStartDay = "MONDAY";

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public UserPreference() {
    }

    public UserPreference(UUID id, User user, boolean darkMode, boolean emailNotifications, boolean pushNotifications, int dailyGoalTasks, String weekStartDay) {
        this.id = id;
        this.user = user;
        this.darkMode = darkMode;
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.dailyGoalTasks = dailyGoalTasks;
        this.weekStartDay = weekStartDay != null ? weekStartDay : "MONDAY";
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public boolean isPushNotifications() { return pushNotifications; }
    public void setPushNotifications(boolean pushNotifications) { this.pushNotifications = pushNotifications; }

    public int getDailyGoalTasks() { return dailyGoalTasks; }
    public void setDailyGoalTasks(int dailyGoalTasks) { this.dailyGoalTasks = dailyGoalTasks; }

    public String getWeekStartDay() { return weekStartDay; }
    public void setWeekStartDay(String weekStartDay) { this.weekStartDay = weekStartDay; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private boolean darkMode = false;
        private boolean emailNotifications = true;
        private boolean pushNotifications = true;
        private int dailyGoalTasks = 5;
        private String weekStartDay = "MONDAY";

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder darkMode(boolean darkMode) { this.darkMode = darkMode; return this; }
        public Builder emailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; return this; }
        public Builder pushNotifications(boolean pushNotifications) { this.pushNotifications = pushNotifications; return this; }
        public Builder dailyGoalTasks(int dailyGoalTasks) { this.dailyGoalTasks = dailyGoalTasks; return this; }
        public Builder weekStartDay(String weekStartDay) { this.weekStartDay = weekStartDay; return this; }
        public UserPreference build() {
            return new UserPreference(id, user, darkMode, emailNotifications, pushNotifications, dailyGoalTasks, weekStartDay);
        }
    }
}
