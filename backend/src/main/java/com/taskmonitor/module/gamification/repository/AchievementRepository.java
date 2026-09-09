package com.taskmonitor.module.gamification.repository;

import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    Optional<Achievement> findByCode(String code);

    List<Achievement> findAllByBadgeType(BadgeType badgeType);

    @Query("SELECT a FROM Achievement a WHERE a.id NOT IN " +
           "(SELECT ua.achievement.id FROM UserAchievement ua WHERE ua.user.id = :userId)")
    List<Achievement> findUnearnedBadgesByUserId(@Param("userId") UUID userId);
}
