package com.taskmonitor.module.gamification.repository;

import com.taskmonitor.module.gamification.domain.PointsLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointsLedgerRepository extends JpaRepository<PointsLedger, UUID> {

    Page<PointsLedger> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<PointsLedger> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COALESCE(SUM(p.pointsDelta), 0) FROM PointsLedger p " +
           "WHERE p.user.id = :userId AND p.createdAt >= :start AND p.pointsDelta > 0")
    int sumPointsEarnedSince(@Param("userId") UUID userId, @Param("start") Instant start);

    @Query("SELECT COALESCE(SUM(p.pointsDelta), 0) FROM PointsLedger p " +
           "WHERE p.user.id = :userId AND p.createdAt >= :start AND p.createdAt <= :end AND p.pointsDelta > 0")
    int sumPointsEarnedBetween(@Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT CAST(p.createdAt AS LocalDate), COALESCE(SUM(p.pointsDelta), 0) " +
           "FROM PointsLedger p " +
           "WHERE p.user.id = :userId AND p.createdAt >= :start AND p.createdAt <= :end AND p.pointsDelta > 0 " +
           "GROUP BY CAST(p.createdAt AS LocalDate)")
    List<Object[]> aggregatePointsByDate(@Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COALESCE(SUM(p.pointsDelta), 0) FROM PointsLedger p " +
           "WHERE p.user.id = :userId AND p.pointsDelta > 0")
    int calculateLifetimePoints(@Param("userId") UUID userId);

    @Query("SELECT p.user.id, p.user.fullName, MAX(p.balanceAfter) " +
           "FROM PointsLedger p " +
           "GROUP BY p.user.id, p.user.fullName " +
           "ORDER BY MAX(p.balanceAfter) DESC")
    List<Object[]> findTopUsersLeaderboard(Pageable pageable);
}
