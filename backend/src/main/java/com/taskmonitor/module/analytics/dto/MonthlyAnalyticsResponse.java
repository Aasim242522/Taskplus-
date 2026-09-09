package com.taskmonitor.module.analytics.dto;

import java.time.LocalDate;
import java.util.List;

public class MonthlyAnalyticsResponse {

    private int year;
    private int month;
    private int totalCreated;
    private int totalCompleted;
    private int totalOverdue;
    private double completionRate;
    private int totalPointsEarned;
    private int totalFocusMinutes;
    private List<DailyTrendPoint> trendPoints;

    public MonthlyAnalyticsResponse() {
    }

    public MonthlyAnalyticsResponse(int year, int month, int totalCreated, int totalCompleted, int totalOverdue, double completionRate, int totalPointsEarned, int totalFocusMinutes, List<DailyTrendPoint> trendPoints) {
        this.year = year;
        this.month = month;
        this.totalCreated = totalCreated;
        this.totalCompleted = totalCompleted;
        this.totalOverdue = totalOverdue;
        this.completionRate = completionRate;
        this.totalPointsEarned = totalPointsEarned;
        this.totalFocusMinutes = totalFocusMinutes;
        this.trendPoints = trendPoints;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getTotalCreated() { return totalCreated; }
    public void setTotalCreated(int totalCreated) { this.totalCreated = totalCreated; }

    public int getTotalCompleted() { return totalCompleted; }
    public void setTotalCompleted(int totalCompleted) { this.totalCompleted = totalCompleted; }

    public int getTotalOverdue() { return totalOverdue; }
    public void setTotalOverdue(int totalOverdue) { this.totalOverdue = totalOverdue; }

    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }

    public int getTotalPointsEarned() { return totalPointsEarned; }
    public void setTotalPointsEarned(int totalPointsEarned) { this.totalPointsEarned = totalPointsEarned; }

    public int getTotalFocusMinutes() { return totalFocusMinutes; }
    public void setTotalFocusMinutes(int totalFocusMinutes) { this.totalFocusMinutes = totalFocusMinutes; }

    public List<DailyTrendPoint> getTrendPoints() { return trendPoints; }
    public void setTrendPoints(List<DailyTrendPoint> trendPoints) { this.trendPoints = trendPoints; }

    public static class Builder {
        private int year;
        private int month;
        private int totalCreated;
        private int totalCompleted;
        private int totalOverdue;
        private double completionRate;
        private int totalPointsEarned;
        private int totalFocusMinutes;
        private List<DailyTrendPoint> trendPoints;

        public Builder year(int year) { this.year = year; return this; }
        public Builder month(int month) { this.month = month; return this; }
        public Builder totalCreated(int totalCreated) { this.totalCreated = totalCreated; return this; }
        public Builder totalCompleted(int totalCompleted) { this.totalCompleted = totalCompleted; return this; }
        public Builder totalOverdue(int totalOverdue) { this.totalOverdue = totalOverdue; return this; }
        public Builder completionRate(double completionRate) { this.completionRate = completionRate; return this; }
        public Builder totalPointsEarned(int totalPointsEarned) { this.totalPointsEarned = totalPointsEarned; return this; }
        public Builder totalFocusMinutes(int totalFocusMinutes) { this.totalFocusMinutes = totalFocusMinutes; return this; }
        public Builder trendPoints(List<DailyTrendPoint> trendPoints) { this.trendPoints = trendPoints; return this; }

        public MonthlyAnalyticsResponse build() {
            return new MonthlyAnalyticsResponse(year, month, totalCreated, totalCompleted, totalOverdue, completionRate, totalPointsEarned, totalFocusMinutes, trendPoints);
        }
    }

    public static class DailyTrendPoint {
        private LocalDate date;
        private int completedCount;
        private int pointsEarned;

        public DailyTrendPoint() {
        }

        public DailyTrendPoint(LocalDate date, int completedCount, int pointsEarned) {
            this.date = date;
            this.completedCount = completedCount;
            this.pointsEarned = pointsEarned;
        }

        public static DailyTrendPointBuilder builder() {
            return new DailyTrendPointBuilder();
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

        public int getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

        public static class DailyTrendPointBuilder {
            private LocalDate date;
            private int completedCount;
            private int pointsEarned;

            public DailyTrendPointBuilder date(LocalDate date) { this.date = date; return this; }
            public DailyTrendPointBuilder completedCount(int completedCount) { this.completedCount = completedCount; return this; }
            public DailyTrendPointBuilder pointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; return this; }

            public DailyTrendPoint build() {
                return new DailyTrendPoint(date, completedCount, pointsEarned);
            }
        }
    }
}
