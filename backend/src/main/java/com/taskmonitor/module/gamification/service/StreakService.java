package com.taskmonitor.module.gamification.service;

import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.repository.StreakRepository;
import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class StreakService {

    private static final Logger log = LoggerFactory.getLogger(StreakService.class);

    private final StreakRepository streakRepository;
    private final NotificationService notificationService;

    public StreakService(StreakRepository streakRepository, NotificationService notificationService) {
        this.streakRepository = streakRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Streak recordDailyActivity(User user) {
        Streak streak = streakRepository.findByUserIdWithLock(user.getId())
                .orElseGet(() -> Streak.builder()
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .freezeCredits(1)
                        .build());

        LocalDate userToday = LocalDate.now(ZoneId.of(user.getTimezone() != null ? user.getTimezone() : "UTC"));
        LocalDate lastActivity = streak.getLastActivityDate();

        if (lastActivity == null) {
            streak.setCurrentStreak(1);
            streak.setLongestStreak(1);
            streak.setLastActivityDate(userToday);
            log.info("Initialized streak for user {} to 1", user.getId());
        } else if (lastActivity.equals(userToday)) {
            log.debug("User {} already active today, streak stays at {}", user.getId(), streak.getCurrentStreak());
        } else if (lastActivity.equals(userToday.minusDays(1))) {
            int newStreak = streak.getCurrentStreak() + 1;
            streak.setCurrentStreak(newStreak);
            if (newStreak > streak.getLongestStreak()) {
                streak.setLongestStreak(newStreak);
            }
            streak.setLastActivityDate(userToday);
            log.info("Incremented streak for user {} to {}", user.getId(), newStreak);

            if (newStreak % 7 == 0 || newStreak == 3 || newStreak == 30 || newStreak == 100) {
                notificationService.createNotification(
                        user,
                        "Streak Milestone Achieved! 🔥",
                        "Incredible dedication! You reached a " + newStreak + "-day streak!",
                        NotificationType.STREAK_MILESTONE
                );
            }
        } else {
            long daysMissed = java.time.temporal.ChronoUnit.DAYS.between(lastActivity, userToday) - 1;
            if (daysMissed == 1 && streak.getFreezeCredits() > 0) {
                streak.setFreezeCredits(streak.getFreezeCredits() - 1);
                int newStreak = streak.getCurrentStreak() + 1;
                streak.setCurrentStreak(newStreak);
                if (newStreak > streak.getLongestStreak()) {
                    streak.setLongestStreak(newStreak);
                }
                streak.setLastActivityDate(userToday);

                notificationService.createNotification(
                        user,
                        "Streak Freeze Activated 🛡️",
                        "A streak freeze was used to protect your " + newStreak + "-day streak!",
                        NotificationType.STREAK_MILESTONE
                );
                log.info("Used streak freeze credit for user {}. Streak saved and increased to {}", user.getId(), newStreak);
            } else {
                streak.setCurrentStreak(1);
                streak.setLastActivityDate(userToday);
                log.info("Streak reset to 1 for user {} due to {} missed days", user.getId(), daysMissed);
            }
        }

        return streakRepository.save(streak);
    }

    @Transactional
    public void evaluateDecayedStreaks() {
        List<Streak> streaks = streakRepository.findAll();
        for (Streak streak : streaks) {
            User user = streak.getUser();
            if (user == null || streak.getCurrentStreak() == 0 || streak.getLastActivityDate() == null) {
                continue;
            }

            LocalDate userToday = LocalDate.now(ZoneId.of(user.getTimezone() != null ? user.getTimezone() : "UTC"));
            LocalDate lastActivity = streak.getLastActivityDate();

            if (lastActivity.isBefore(userToday.minusDays(1))) {
                if (streak.getFreezeCredits() > 0) {
                    streak.setFreezeCredits(streak.getFreezeCredits() - 1);
                    streak.setLastActivityDate(userToday.minusDays(1));
                    notificationService.createNotification(
                            user,
                            "Streak Freeze Safeguard 🛡️",
                            "Your streak was frozen because you didn't complete a task yesterday.",
                            NotificationType.STREAK_MILESTONE
                    );
                    log.info("Auto-consumed freeze credit for user {} to preserve streak of {}", user.getId(), streak.getCurrentStreak());
                } else {
                    int lostStreak = streak.getCurrentStreak();
                    streak.setCurrentStreak(0);
                    notificationService.createNotification(
                            user,
                            "Streak Ended ⏳",
                            "Your " + lostStreak + "-day streak has ended. Complete a task today to start a new streak!",
                            NotificationType.STREAK_MILESTONE
                    );
                    log.info("Reset streak to 0 for user {} (was {})", user.getId(), lostStreak);
                }
                streakRepository.save(streak);
            }
        }
    }

    @Transactional(readOnly = true)
    public Streak getStreakByUserId(UUID userId) {
        return streakRepository.findByUserId(userId).orElse(null);
    }
}
