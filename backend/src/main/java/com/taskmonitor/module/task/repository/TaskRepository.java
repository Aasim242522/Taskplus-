package com.taskmonitor.module.task.repository;

import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"category"})
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findFiltered(
            @Param("userId") UUID userId,
            @Param("status") TaskStatus status,
            @Param("categoryId") UUID categoryId,
            @Param("priority") Priority priority,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDateTime >= :start AND t.dueDateTime <= :end ORDER BY t.dueDateTime ASC")
    List<Task> findDueBetween(
            @Param("userId") UUID userId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' AND t.dueDateTime < :now")
    List<Task> findOverdueTasks(@Param("now") Instant now);

    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' AND t.dueDateTime BETWEEN :windowStart AND :windowEnd")
    List<Task> findUpcomingTasksForReminder(
            @Param("windowStart") Instant windowStart,
            @Param("windowEnd") Instant windowEnd
    );

    long countByUserIdAndStatus(UUID userId, TaskStatus status);

    long countByUserIdAndCategoryNameAndStatus(UUID userId, String categoryName, TaskStatus status);

    @Query("SELECT CAST(t.completedAt AS LocalDate), COUNT(t), COALESCE(SUM(t.actualDurationMinutes), 0) " +
           "FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' " +
           "AND t.completedAt >= :startDate AND t.completedAt <= :endDate " +
           "GROUP BY CAST(t.completedAt AS LocalDate) " +
           "ORDER BY CAST(t.completedAt AS LocalDate) ASC")
    List<Object[]> aggregateCompletedByDate(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT CAST(t.createdAt AS LocalDate), COUNT(t) " +
           "FROM Task t WHERE t.user.id = :userId " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate " +
           "GROUP BY CAST(t.createdAt AS LocalDate)")
    List<Object[]> aggregateCreatedByDate(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT CAST(t.dueDateTime AS LocalDate), COUNT(t) " +
           "FROM Task t WHERE t.user.id = :userId AND t.status = 'OVERDUE' " +
           "AND t.dueDateTime >= :startDate AND t.dueDateTime <= :endDate " +
           "GROUP BY CAST(t.dueDateTime AS LocalDate)")
    List<Object[]> aggregateOverdueByDate(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    long countCreatedBetween(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' AND t.completedAt >= :startDate AND t.completedAt <= :endDate")
    long countCompletedBetween(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'OVERDUE' AND t.dueDateTime >= :startDate AND t.dueDateTime <= :endDate")
    long countOverdueBetween(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}
