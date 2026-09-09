package com.taskmonitor.module.analytics.dto;

import java.time.LocalDate;

public class HeatmapDayResponse {

    private LocalDate date;
    private int count;
    private int level; // 0: none, 1: 1-2, 2: 3-4, 3: 5-6, 4: 7+

    public HeatmapDayResponse() {
    }

    public HeatmapDayResponse(LocalDate date, int count, int level) {
        this.date = date;
        this.count = count;
        this.level = level;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public static class Builder {
        private LocalDate date;
        private int count;
        private int level;

        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder count(int count) { this.count = count; return this; }
        public Builder level(int level) { this.level = level; return this; }

        public HeatmapDayResponse build() {
            return new HeatmapDayResponse(date, count, level);
        }
    }
}
