package com.taskmonitor.module.analytics.dto;

import java.time.LocalDate;
import java.util.List;

public class WeeklyAnalyticsResponse {

    private List<DailyMetricDto> dailyMetrics;
    private int totalCreated;
    private int totalCompleted;
    private int totalOverdue;
    private double completionRate;
    private int totalPointsEarned;
    private double avgPointsPerDay;

    public WeeklyAnalyticsResponse() {
    }

    public WeeklyAnalyticsResponse(List<DailyMetricDto> dailyMetrics, int totalCreated, int totalCompleted, int totalOverdue, double completionRate, int totalPointsEarned, double avgPointsPerDay) {
        this.dailyMetrics = dailyMetrics;
        this.totalCreated = totalCreated;
        this.totalCompleted = totalCompleted;
        this.totalOverdue = totalOverdue;
        this.completionRate = completionRate;
        this.totalPointsEarned = totalPointsEarned;
        this.avgPointsPerDay = avgPointsPerDay;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<DailyMetricDto> getDailyMetrics() { return dailyMetrics; }
    public void setDailyMetrics(List<DailyMetricDto> dailyMetrics) { this.dailyMetrics = dailyMetrics; }

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

    public double getAvgPointsPerDay() { return avgPointsPerDay; }
    public void setAvgPointsPerDay(double avgPointsPerDay) { this.avgPointsPerDay = avgPointsPerDay; }

    public static class Builder {
        private List<DailyMetricDto> dailyMetrics;
        private int totalCreated;
        private int totalCompleted;
        private int totalOverdue;
        private double completionRate;
        private int totalPointsEarned;
        private double avgPointsPerDay;

        public Builder dailyMetrics(List<DailyMetricDto> dailyMetrics) { this.dailyMetrics = dailyMetrics; return this; }
        public Builder totalCreated(int totalCreated) { this.totalCreated = totalCreated; return this; }
        public Builder totalCompleted(int totalCompleted) { this.totalCompleted = totalCompleted; return this; }
        public Builder totalOverdue(int totalOverdue) { this.totalOverdue = totalOverdue; return this; }
        public Builder completionRate(double completionRate) { this.completionRate = completionRate; return this; }
        public Builder totalPointsEarned(int totalPointsEarned) { this.totalPointsEarned = totalPointsEarned; return this; }
        public Builder avgPointsPerDay(double avgPointsPerDay) { this.avgPointsPerDay = avgPointsPerDay; return this; }

        public WeeklyAnalyticsResponse build() {
            return new WeeklyAnalyticsResponse(dailyMetrics, totalCreated, totalCompleted, totalOverdue, completionRate, totalPointsEarned, avgPointsPerDay);
        }
    }

    public static class DailyMetricDto {
        private LocalDate date;
        private String dayOfWeek;
        private int tasksCreated;
        private int tasksCompleted;
        private int tasksOverdue;
        private int pointsEarned;
        private int totalDurationMinutes;

        public DailyMetricDto() {
        }

        public DailyMetricDto(LocalDate date, String dayOfWeek, int tasksCreated, int tasksCompleted, int tasksOverdue, int pointsEarned, int totalDurationMinutes) {
            this.date = date;
            this.dayOfWeek = dayOfWeek;
            this.tasksCreated = tasksCreated;
            this.tasksCompleted = tasksCompleted;
            this.tasksOverdue = tasksOverdue;
            this.pointsEarned = pointsEarned;
            this.totalDurationMinutes = totalDurationMinutes;
        }

        public static DailyMetricDtoBuilder builder() {
            return new DailyMetricDtoBuilder();
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

        public int getTasksCreated() { return tasksCreated; }
        public void setTasksCreated(int tasksCreated) { this.tasksCreated = tasksCreated; }

        public int getTasksCompleted() { return tasksCompleted; }
        public void setTasksCompleted(int tasksCompleted) { this.tasksCompleted = tasksCompleted; }

        public int getTasksOverdue() { return tasksOverdue; }
        public void setTasksOverdue(int tasksOverdue) { this.tasksOverdue = tasksOverdue; }

        public int getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

        public int getTotalDurationMinutes() { return totalDurationMinutes; }
        public void setTotalDurationMinutes(int totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

        public static class DailyMetricDtoBuilder {
            private LocalDate date;
            private String dayOfWeek;
            private int tasksCreated;
            private int tasksCompleted;
            private int tasksOverdue;
            private int pointsEarned;
            private int totalDurationMinutes;

            public DailyMetricDtoBuilder date(LocalDate date) { this.date = date; return this; }
            public DailyMetricDtoBuilder dayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
            public DailyMetricDtoBuilder tasksCreated(int tasksCreated) { this.tasksCreated = tasksCreated; return this; }
            public DailyMetricDtoBuilder tasksCompleted(int tasksCompleted) { this.tasksCompleted = tasksCompleted; return this; }
            public DailyMetricDtoBuilder tasksOverdue(int tasksOverdue) { this.tasksOverdue = tasksOverdue; return this; }
            public DailyMetricDtoBuilder pointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; return this; }
            public DailyMetricDtoBuilder totalDurationMinutes(int totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; return this; }

            public DailyMetricDto build() {
                return new DailyMetricDto(date, dayOfWeek, tasksCreated, tasksCompleted, tasksOverdue, pointsEarned, totalDurationMinutes);
            }
        }
    }
}
