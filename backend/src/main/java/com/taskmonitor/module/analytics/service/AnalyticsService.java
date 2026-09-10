package com.taskmonitor.module.analytics.service;

import com.taskmonitor.module.analytics.dto.HeatmapDayResponse;
import com.taskmonitor.module.analytics.dto.MonthlyAnalyticsResponse;
import com.taskmonitor.module.analytics.dto.WeeklyAnalyticsResponse;
import com.taskmonitor.module.gamification.repository.PointsLedgerRepository;
import com.taskmonitor.module.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AnalyticsService {

    private final TaskRepository taskRepository;
    private final PointsLedgerRepository pointsLedgerRepository;

    public AnalyticsService(TaskRepository taskRepository,
                            PointsLedgerRepository pointsLedgerRepository) {
        this.taskRepository = taskRepository;
        this.pointsLedgerRepository = pointsLedgerRepository;
    }

    @Transactional(readOnly = true)
    public WeeklyAnalyticsResponse getWeeklyAnalytics(UUID userId, String timezone) {
        ZoneId zoneId = getSafeZoneId(timezone);
        LocalDate today = LocalDate.now(zoneId);
        LocalDate startOfWeek = today.minusDays(6);

        Instant weekStart = startOfWeek.atStartOfDay(zoneId).toInstant();
        Instant weekEnd = today.plusDays(1).atStartOfDay(zoneId).toInstant().minusMillis(1);

        // Batch fetch 7-day aggregates in single queries instead of looping queries (eliminates N+1)
        Map<String, Integer> createdMap = parseDateCountMap(taskRepository.aggregateCreatedByDate(userId, weekStart, weekEnd));
        Map<String, Integer> completedMap = parseDateCountMap(taskRepository.aggregateCompletedByDate(userId, weekStart, weekEnd));
        Map<String, Integer> overdueMap = parseDateCountMap(taskRepository.aggregateOverdueByDate(userId, weekStart, weekEnd));
        Map<String, Integer> pointsMap = parseDateCountMap(pointsLedgerRepository.aggregatePointsByDate(userId, weekStart, weekEnd));

        List<WeeklyAnalyticsResponse.DailyMetricDto> metricsList = new ArrayList<>();
        int totalCreated = 0;
        int totalCompleted = 0;
        int totalOverdue = 0;
        int totalPoints = 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            String dateKey = date.format(dtf);

            int created = createdMap.getOrDefault(dateKey, 0);
            int completed = completedMap.getOrDefault(dateKey, 0);
            int overdue = overdueMap.getOrDefault(dateKey, 0);
            int points = pointsMap.getOrDefault(dateKey, 0);

            totalCreated += created;
            totalCompleted += completed;
            totalOverdue += overdue;
            totalPoints += points;

            metricsList.add(WeeklyAnalyticsResponse.DailyMetricDto.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .tasksCreated(created)
                    .tasksCompleted(completed)
                    .tasksOverdue(overdue)
                    .pointsEarned(points)
                    .totalDurationMinutes(completed * 30)
                    .build());
        }

        double completionRate = (totalCreated + totalOverdue) > 0 ? ((double) totalCompleted / (totalCreated + totalOverdue)) * 100.0 : 0.0;
        completionRate = Math.min(100.0, Math.round(completionRate * 10.0) / 10.0);
        double avgPoints = Math.round((totalPoints / 7.0) * 10.0) / 10.0;

        return WeeklyAnalyticsResponse.builder()
                .dailyMetrics(metricsList)
                .totalCreated(totalCreated)
                .totalCompleted(totalCompleted)
                .totalOverdue(totalOverdue)
                .completionRate(completionRate)
                .totalPointsEarned(totalPoints)
                .avgPointsPerDay(avgPoints)
                .build();
    }

    @Transactional(readOnly = true)
    public MonthlyAnalyticsResponse getMonthlyAnalytics(UUID userId, int year, int month, String timezone) {
        ZoneId zoneId = getSafeZoneId(timezone);
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startOfMonth = ym.atDay(1);
        LocalDate endOfMonth = ym.atEndOfMonth();

        Instant monthStart = startOfMonth.atStartOfDay(zoneId).toInstant();
        Instant monthEnd = endOfMonth.plusDays(1).atStartOfDay(zoneId).toInstant().minusMillis(1);

        // Batch fetch monthly aggregates in single queries (reducing 65 queries down to 4 queries)
        Map<String, Integer> createdMap = parseDateCountMap(taskRepository.aggregateCreatedByDate(userId, monthStart, monthEnd));
        Map<String, Integer> completedMap = parseDateCountMap(taskRepository.aggregateCompletedByDate(userId, monthStart, monthEnd));
        Map<String, Integer> overdueMap = parseDateCountMap(taskRepository.aggregateOverdueByDate(userId, monthStart, monthEnd));
        Map<String, Integer> pointsMap = parseDateCountMap(pointsLedgerRepository.aggregatePointsByDate(userId, monthStart, monthEnd));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int totalCreated = createdMap.values().stream().mapToInt(v -> v != null ? v : 0).sum();
        int totalCompleted = completedMap.values().stream().mapToInt(v -> v != null ? v : 0).sum();
        int totalOverdue = overdueMap.values().stream().mapToInt(v -> v != null ? v : 0).sum();
        int totalPoints = pointsMap.values().stream().mapToInt(v -> v != null ? v : 0).sum();

        List<MonthlyAnalyticsResponse.DailyTrendPoint> trendPoints = new ArrayList<>();
        int daysInMonth = ym.lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = ym.atDay(d);
            String dateKey = date.format(dtf);

            int completed = completedMap.getOrDefault(dateKey, 0);
            int pts = pointsMap.getOrDefault(dateKey, 0);

            trendPoints.add(MonthlyAnalyticsResponse.DailyTrendPoint.builder()
                    .date(date)
                    .completedCount(completed)
                    .pointsEarned(pts)
                    .build());
        }

        double rate = (totalCreated + totalOverdue) > 0 ? ((double) totalCompleted / (totalCreated + totalOverdue)) * 100.0 : 0.0;
        rate = Math.min(100.0, Math.round(rate * 10.0) / 10.0);

        return MonthlyAnalyticsResponse.builder()
                .year(year)
                .month(month)
                .totalCreated(totalCreated)
                .totalCompleted(totalCompleted)
                .totalOverdue(totalOverdue)
                .completionRate(rate)
                .totalPointsEarned(totalPoints)
                .totalFocusMinutes(totalCompleted * 35)
                .trendPoints(trendPoints)
                .build();
    }

    @Transactional(readOnly = true)
    public List<HeatmapDayResponse> getActivityHeatmap(UUID userId, int daysBack, String timezone) {
        ZoneId zoneId = getSafeZoneId(timezone);
        LocalDate today = LocalDate.now(zoneId);
        LocalDate start = today.minusDays(Math.max(30, Math.min(daysBack, 120)));

        Instant startInstant = start.atStartOfDay(zoneId).toInstant();
        Instant endInstant = today.plusDays(1).atStartOfDay(zoneId).toInstant().minusMillis(1);

        // 1 single aggregation query instead of 60 individual count queries
        Map<String, Integer> completedMap = parseDateCountMap(taskRepository.aggregateCompletedByDate(userId, startInstant, endInstant));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<HeatmapDayResponse> heatmap = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            String dateKey = d.format(dtf);
            int count = completedMap.getOrDefault(dateKey, 0);

            int level = 0;
            if (count >= 7) level = 4;
            else if (count >= 5) level = 3;
            else if (count >= 3) level = 2;
            else if (count >= 1) level = 1;

            heatmap.add(HeatmapDayResponse.builder()
                    .date(d)
                    .count(count)
                    .level(level)
                    .build());
        }
        return heatmap;
    }

    private ZoneId getSafeZoneId(String timezone) {
        if (timezone == null || timezone.isBlank()) return ZoneId.of("UTC");
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }

    private Map<String, Integer> parseDateCountMap(List<Object[]> rows) {
        Map<String, Integer> map = new HashMap<>();
        if (rows == null) return map;
        for (Object[] r : rows) {
            if (r != null && r.length >= 2 && r[0] != null && r[1] != null) {
                String dateStr = r[0].toString();
                int count = ((Number) r[1]).intValue();
                map.put(dateStr, count);
            }
        }
        return map;
    }
}
