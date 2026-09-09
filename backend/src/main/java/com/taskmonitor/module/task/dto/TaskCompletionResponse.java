package com.taskmonitor.module.task.dto;

import com.taskmonitor.module.gamification.dto.BadgeResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TaskCompletionResponse {

    private UUID taskId;
    private String status;
    private Instant completedAt;
    private int pointsAwarded;
    private PointsBreakdown breakdown;
    private int newTotalPoints;
    private int currentStreak;
    private List<BadgeResponse> badgesUnlocked;

    public TaskCompletionResponse() {
    }

    public TaskCompletionResponse(UUID taskId, String status, Instant completedAt, int pointsAwarded, PointsBreakdown breakdown, int newTotalPoints, int currentStreak, List<BadgeResponse> badgesUnlocked) {
        this.taskId = taskId;
        this.status = status;
        this.completedAt = completedAt;
        this.pointsAwarded = pointsAwarded;
        this.breakdown = breakdown;
        this.newTotalPoints = newTotalPoints;
        this.currentStreak = currentStreak;
        this.badgesUnlocked = badgesUnlocked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }

    public PointsBreakdown getBreakdown() { return breakdown; }
    public void setBreakdown(PointsBreakdown breakdown) { this.breakdown = breakdown; }

    public int getNewTotalPoints() { return newTotalPoints; }
    public void setNewTotalPoints(int newTotalPoints) { this.newTotalPoints = newTotalPoints; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public List<BadgeResponse> getBadgesUnlocked() { return badgesUnlocked; }
    public void setBadgesUnlocked(List<BadgeResponse> badgesUnlocked) { this.badgesUnlocked = badgesUnlocked; }

    public static class Builder {
        private UUID taskId;
        private String status;
        private Instant completedAt;
        private int pointsAwarded;
        private PointsBreakdown breakdown;
        private int newTotalPoints;
        private int currentStreak;
        private List<BadgeResponse> badgesUnlocked;

        public Builder taskId(UUID taskId) { this.taskId = taskId; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder pointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; return this; }
        public Builder breakdown(PointsBreakdown breakdown) { this.breakdown = breakdown; return this; }
        public Builder newTotalPoints(int newTotalPoints) { this.newTotalPoints = newTotalPoints; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder badgesUnlocked(List<BadgeResponse> badgesUnlocked) { this.badgesUnlocked = badgesUnlocked; return this; }

        public TaskCompletionResponse build() {
            return new TaskCompletionResponse(taskId, status, completedAt, pointsAwarded, breakdown, newTotalPoints, currentStreak, badgesUnlocked);
        }
    }

    public static class PointsBreakdown {
        private int basePoints;
        private double difficultyMultiplier;
        private int onTimeBonus;
        private int streakBonus;
        private int durationEfficiencyBonus;
        private int overduePenalty;
        private int totalPointsAwarded;

        public PointsBreakdown() {
        }

        public PointsBreakdown(int basePoints, double difficultyMultiplier, int onTimeBonus, int streakBonus, int durationEfficiencyBonus, int overduePenalty, int totalPointsAwarded) {
            this.basePoints = basePoints;
            this.difficultyMultiplier = difficultyMultiplier;
            this.onTimeBonus = onTimeBonus;
            this.streakBonus = streakBonus;
            this.durationEfficiencyBonus = durationEfficiencyBonus;
            this.overduePenalty = overduePenalty;
            this.totalPointsAwarded = totalPointsAwarded;
        }

        public static PointsBreakdownBuilder builder() {
            return new PointsBreakdownBuilder();
        }

        public int getBasePoints() { return basePoints; }
        public void setBasePoints(int basePoints) { this.basePoints = basePoints; }

        public double getDifficultyMultiplier() { return difficultyMultiplier; }
        public void setDifficultyMultiplier(double difficultyMultiplier) { this.difficultyMultiplier = difficultyMultiplier; }

        public int getOnTimeBonus() { return onTimeBonus; }
        public void setOnTimeBonus(int onTimeBonus) { this.onTimeBonus = onTimeBonus; }

        public int getStreakBonus() { return streakBonus; }
        public void setStreakBonus(int streakBonus) { this.streakBonus = streakBonus; }

        public int getDurationEfficiencyBonus() { return durationEfficiencyBonus; }
        public void setDurationEfficiencyBonus(int durationEfficiencyBonus) { this.durationEfficiencyBonus = durationEfficiencyBonus; }

        public int getOverduePenalty() { return overduePenalty; }
        public void setOverduePenalty(int overduePenalty) { this.overduePenalty = overduePenalty; }

        public int getTotalPointsAwarded() { return totalPointsAwarded; }
        public void setTotalPointsAwarded(int totalPointsAwarded) { this.totalPointsAwarded = totalPointsAwarded; }

        public static class PointsBreakdownBuilder {
            private int basePoints;
            private double difficultyMultiplier;
            private int onTimeBonus;
            private int streakBonus;
            private int durationEfficiencyBonus;
            private int overduePenalty;
            private int totalPointsAwarded;

            public PointsBreakdownBuilder basePoints(int basePoints) { this.basePoints = basePoints; return this; }
            public PointsBreakdownBuilder difficultyMultiplier(double difficultyMultiplier) { this.difficultyMultiplier = difficultyMultiplier; return this; }
            public PointsBreakdownBuilder onTimeBonus(int onTimeBonus) { this.onTimeBonus = onTimeBonus; return this; }
            public PointsBreakdownBuilder streakBonus(int streakBonus) { this.streakBonus = streakBonus; return this; }
            public PointsBreakdownBuilder durationEfficiencyBonus(int durationEfficiencyBonus) { this.durationEfficiencyBonus = durationEfficiencyBonus; return this; }
            public PointsBreakdownBuilder overduePenalty(int overduePenalty) { this.overduePenalty = overduePenalty; return this; }
            public PointsBreakdownBuilder totalPointsAwarded(int totalPointsAwarded) { this.totalPointsAwarded = totalPointsAwarded; return this; }

            public PointsBreakdown build() {
                return new PointsBreakdown(basePoints, difficultyMultiplier, onTimeBonus, streakBonus, durationEfficiencyBonus, overduePenalty, totalPointsAwarded);
            }
        }
    }
}
