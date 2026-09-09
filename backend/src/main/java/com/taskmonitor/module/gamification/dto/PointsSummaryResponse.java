package com.taskmonitor.module.gamification.dto;

import java.time.LocalDate;

public class PointsSummaryResponse {

    private int currentBalance;
    private int lifetimePoints;
    private int pointsEarnedToday;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActivityDate;
    private int freezeCredits;
    private int userLevel;
    private int pointsToNextLevel;

    public PointsSummaryResponse() {
    }

    public PointsSummaryResponse(int currentBalance, int lifetimePoints, int pointsEarnedToday, int currentStreak, int longestStreak, LocalDate lastActivityDate, int freezeCredits, int userLevel, int pointsToNextLevel) {
        this.currentBalance = currentBalance;
        this.lifetimePoints = lifetimePoints;
        this.pointsEarnedToday = pointsEarnedToday;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActivityDate = lastActivityDate;
        this.freezeCredits = freezeCredits;
        this.userLevel = userLevel;
        this.pointsToNextLevel = pointsToNextLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(int currentBalance) { this.currentBalance = currentBalance; }

    public int getLifetimePoints() { return lifetimePoints; }
    public void setLifetimePoints(int lifetimePoints) { this.lifetimePoints = lifetimePoints; }

    public int getPointsEarnedToday() { return pointsEarnedToday; }
    public void setPointsEarnedToday(int pointsEarnedToday) { this.pointsEarnedToday = pointsEarnedToday; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public int getFreezeCredits() { return freezeCredits; }
    public void setFreezeCredits(int freezeCredits) { this.freezeCredits = freezeCredits; }

    public int getUserLevel() { return userLevel; }
    public void setUserLevel(int userLevel) { this.userLevel = userLevel; }

    public int getPointsToNextLevel() { return pointsToNextLevel; }
    public void setPointsToNextLevel(int pointsToNextLevel) { this.pointsToNextLevel = pointsToNextLevel; }

    public static class Builder {
        private int currentBalance;
        private int lifetimePoints;
        private int pointsEarnedToday;
        private int currentStreak;
        private int longestStreak;
        private LocalDate lastActivityDate;
        private int freezeCredits;
        private int userLevel;
        private int pointsToNextLevel;

        public Builder currentBalance(int currentBalance) { this.currentBalance = currentBalance; return this; }
        public Builder lifetimePoints(int lifetimePoints) { this.lifetimePoints = lifetimePoints; return this; }
        public Builder pointsEarnedToday(int pointsEarnedToday) { this.pointsEarnedToday = pointsEarnedToday; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(int longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder lastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; return this; }
        public Builder freezeCredits(int freezeCredits) { this.freezeCredits = freezeCredits; return this; }
        public Builder userLevel(int userLevel) { this.userLevel = userLevel; return this; }
        public Builder pointsToNextLevel(int pointsToNextLevel) { this.pointsToNextLevel = pointsToNextLevel; return this; }

        public PointsSummaryResponse build() {
            return new PointsSummaryResponse(currentBalance, lifetimePoints, pointsEarnedToday, currentStreak, longestStreak, lastActivityDate, freezeCredits, userLevel, pointsToNextLevel);
        }
    }
}
