package com.taskmonitor.scheduler;

import com.taskmonitor.module.analytics.domain.DailyTaskMetric;
import com.taskmonitor.module.analytics.repository.DailyTaskMetricRepository;
import com.taskmonitor.module.gamification.repository.PointsLedgerRepository;
import com.taskmonitor.module.task.repository.TaskRepository;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class DailyMetricsAggregationScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyMetricsAggregationScheduler.class);

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final DailyTaskMetricRepository metricRepository;
    private final PointsLedgerRepository pointsLedgerRepository;

    public DailyMetricsAggregationScheduler(UserRepository userRepository,
                                            TaskRepository taskRepository,
                                            DailyTaskMetricRepository metricRepository,
                                            PointsLedgerRepository pointsLedgerRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.metricRepository = metricRepository;
        this.pointsLedgerRepository = pointsLedgerRepository;
    }

    @Scheduled(cron = "${app.scheduler.metrics-aggregation-cron:0 5 0 * * *}")
    @Transactional
    public void aggregateDailyMetrics() {
        log.info("Running scheduled daily metrics aggregation job...");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            try {
                ZoneId zone = ZoneId.of(user.getTimezone() != null ? user.getTimezone() : "UTC");
                LocalDate yesterday = LocalDate.now(zone).minusDays(1);

                Instant dayStart = yesterday.atStartOfDay(zone).toInstant();
                Instant dayEnd = yesterday.plusDays(1).atStartOfDay(zone).toInstant().minusMillis(1);

                int created = (int) taskRepository.countCreatedBetween(user.getId(), dayStart, dayEnd);
                int completed = (int) taskRepository.countCompletedBetween(user.getId(), dayStart, dayEnd);
                int overdue = (int) taskRepository.countOverdueBetween(user.getId(), dayStart, dayEnd);
                int points = Math.max(0, pointsLedgerRepository.sumPointsEarnedSince(user.getId(), dayStart)
                        - pointsLedgerRepository.sumPointsEarnedSince(user.getId(), dayEnd));

                DailyTaskMetric metric = metricRepository.findByUserIdAndMetricDate(user.getId(), yesterday)
                        .orElseGet(() -> DailyTaskMetric.builder()
                                .user(user)
                                .metricDate(yesterday)
                                .build());

                metric.setTasksCreated(created);
                metric.setTasksCompleted(completed);
                metric.setTasksOverdue(overdue);
                metric.setPointsEarned(points);
                metric.setTotalDurationMinutes(completed * 30);

                metricRepository.save(metric);
            } catch (Exception ex) {
                log.error("Failed to aggregate daily metrics for user {}: {}", user.getId(), ex.getMessage());
            }
        }
        log.info("Finished daily metrics aggregation for {} users.", users.size());
    }
}
