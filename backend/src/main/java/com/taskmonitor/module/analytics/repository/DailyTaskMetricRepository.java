package com.taskmonitor.module.analytics.repository;

import com.taskmonitor.module.analytics.domain.DailyTaskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyTaskMetricRepository extends JpaRepository<DailyTaskMetric, UUID> {

    Optional<DailyTaskMetric> findByUserIdAndMetricDate(UUID userId, LocalDate metricDate);

    List<DailyTaskMetric> findByUserIdAndMetricDateBetweenOrderByMetricDateAsc(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
