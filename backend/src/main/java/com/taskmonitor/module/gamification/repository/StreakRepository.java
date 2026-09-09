package com.taskmonitor.module.gamification.repository;

import com.taskmonitor.module.gamification.domain.Streak;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreakRepository extends JpaRepository<Streak, UUID> {

    Optional<Streak> findByUserId(UUID userId);

    List<Streak> findAllByUserIdIn(java.util.Collection<UUID> userIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Streak s WHERE s.user.id = :userId")
    Optional<Streak> findByUserIdWithLock(@Param("userId") UUID userId);
}
