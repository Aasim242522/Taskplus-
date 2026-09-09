package com.taskmonitor.module.analytics.repository;

import com.taskmonitor.module.analytics.domain.DailyTaskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyTaskMetricRepository extends JpaRepository<DailyTaskMetric, UUID> {

    Optional<DailyTaskMetric> findByUserIdAndMetricDate(UUID userId, LocalDate metricDate);

    List<DailyTaskMetric> findByUserIdAndMetricDateBetweenOrderByMetricDateAsc(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
