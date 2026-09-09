package com.taskmonitor.module.gamification.dto;

import java.util.UUID;

public class LeaderboardEntryResponse {

    private int rank;
    private UUID userId;
    private String fullName;
    private int totalPoints;
    private int streak;

    public LeaderboardEntryResponse() {
    }

    public LeaderboardEntryResponse(int rank, UUID userId, String fullName, int totalPoints, int streak) {
        this.rank = rank;
        this.userId = userId;
        this.fullName = fullName;
        this.totalPoints = totalPoints;
        this.streak = streak;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public static class Builder {
        private int rank;
        private UUID userId;
        private String fullName;
        private int totalPoints;
        private int streak;

        public Builder rank(int rank) { this.rank = rank; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder totalPoints(int totalPoints) { this.totalPoints = totalPoints; return this; }
        public Builder streak(int streak) { this.streak = streak; return this; }

        public LeaderboardEntryResponse build() {
            return new LeaderboardEntryResponse(rank, userId, fullName, totalPoints, streak);
        }
    }
}
