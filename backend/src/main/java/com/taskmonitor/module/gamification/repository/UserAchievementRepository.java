package com.taskmonitor.module.gamification.repository;

import com.taskmonitor.module.gamification.domain.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

    List<UserAchievement> findAllByUserIdOrderByUnlockedAtDesc(UUID userId);

    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
}
