package com.taskmonitor.module.gamification.service;

import com.taskmonitor.module.gamification.domain.PointsLedger;
import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.domain.TransactionType;
import com.taskmonitor.module.gamification.dto.LeaderboardEntryResponse;
import com.taskmonitor.module.gamification.dto.PointsLedgerResponse;
import com.taskmonitor.module.gamification.dto.PointsSummaryResponse;
import com.taskmonitor.module.gamification.repository.PointsLedgerRepository;
import com.taskmonitor.module.gamification.repository.StreakRepository;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.dto.TaskCompletionResponse;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final PointsLedgerRepository pointsLedgerRepository;
    private final StreakRepository streakRepository;
    private final UserRepository userRepository;

    @Value("${app.gamification.base-points:50}")
    private int basePoints;

    @Value("${app.gamification.on-time-bonus:25}")
    private int onTimeBonus;

    @Value("${app.gamification.streak-multiplier:2.5}")
    private double streakMultiplier;

    @Value("${app.gamification.overdue-penalty:15}")
    private int overduePenalty;

    @Value("${app.gamification.daily-diminishing-cap:500}")
    private int dailyDiminishingCap;

    public PointService(PointsLedgerRepository pointsLedgerRepository,
                        StreakRepository streakRepository,
                        UserRepository userRepository) {
        this.pointsLedgerRepository = pointsLedgerRepository;
        this.streakRepository = streakRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskCompletionResponse.PointsBreakdown awardTaskCompletionPoints(User user, Task task, int currentStreak) {
        // 1. Difficulty Multiplier
        double diffMultiplier = switch (task.getDifficultyLevel() != null ? task.getDifficultyLevel() : 2) {
            case 1 -> 1.0;
            case 2 -> 1.2;
            case 3 -> 1.5;
            case 4 -> 2.0;
            case 5 -> 3.0;
            default -> 1.2;
        };
        int difficultyPoints = (int) Math.round(basePoints * diffMultiplier);

        // 2. On-Time Bonus vs Overdue Penalty
        int calculatedOnTimeBonus = 0;
        int calculatedOverduePenalty = 0;
        if (task.getDueDateTime() != null && task.getCompletedAt() != null) {
            if (!task.getCompletedAt().isAfter(task.getDueDateTime())) {
                calculatedOnTimeBonus = onTimeBonus;
            } else {
                calculatedOverduePenalty = overduePenalty;
            }
        } else {
            calculatedOnTimeBonus = 10;
        }

        // 3. Streak Multiplier Bonus
        int calculatedStreakBonus = Math.min(50, (int) Math.floor(currentStreak * streakMultiplier));

        // 4. Duration Efficiency Bonus
        int calculatedEfficiencyBonus = 0;
        if (task.getEstimatedDurationMinutes() != null && task.getEstimatedDurationMinutes() > 0
                && task.getActualDurationMinutes() != null && task.getActualDurationMinutes() > 0
                && task.getActualDurationMinutes() <= task.getEstimatedDurationMinutes()) {
            double ratio = (double) task.getActualDurationMinutes() / task.getEstimatedDurationMinutes();
            calculatedEfficiencyBonus = Math.min(20, (int) Math.floor(15 * (1.0 - ratio)));
        }

        // 5. Total Raw Points with Minimum Floor
        int subtotal = difficultyPoints + calculatedOnTimeBonus + calculatedStreakBonus + calculatedEfficiencyBonus - calculatedOverduePenalty;
        int finalPoints = Math.max(5, subtotal);

        // 6. Anti-Gaming Diminishing Returns check for today
        LocalDate today = LocalDate.now(ZoneId.of(user.getTimezone() != null ? user.getTimezone() : "UTC"));
        Instant startOfDay = today.atStartOfDay(ZoneId.of(user.getTimezone() != null ? user.getTimezone() : "UTC")).toInstant();
        int pointsEarnedToday = pointsLedgerRepository.sumPointsEarnedSince(user.getId(), startOfDay);

        if (pointsEarnedToday >= dailyDiminishingCap) {
            finalPoints = Math.max(5, (int) Math.round(finalPoints * 0.5));
            log.info("Daily diminishing returns applied for user {}: points scaled to {}", user.getId(), finalPoints);
        }

        // 7. Record Immutable Ledger Transaction
        recordTransaction(user, finalPoints, TransactionType.TASK_COMPLETION, "TASK", task.getId(),
                "Completed task: " + task.getTitle());

        return TaskCompletionResponse.PointsBreakdown.builder()
                .basePoints(basePoints)
                .difficultyMultiplier(diffMultiplier)
                .onTimeBonus(calculatedOnTimeBonus)
                .streakBonus(calculatedStreakBonus)
                .durationEfficiencyBonus(calculatedEfficiencyBonus)
                .overduePenalty(calculatedOverduePenalty)
                .totalPointsAwarded(finalPoints)
                .build();
    }

    @Transactional
    public void awardBadgePoints(User user, int points, String badgeCode, String badgeName) {
        if (points <= 0) return;
        recordTransaction(user, points, TransactionType.BADGE_REWARD, "BADGE", null,
                "Unlocked badge [" + badgeCode + "]: " + badgeName);
    }

    @Transactional
    public PointsLedger recordTransaction(User user, int pointsDelta, TransactionType type,
                                          String refType, UUID refId, String description) {
        // Pessimistic write lock on user record to serialize concurrent point balance mutations
        userRepository.findByIdWithLock(user.getId());

        int previousBalance = pointsLedgerRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .map(PointsLedger::getBalanceAfter)
                .orElse(0);

        int newBalance = Math.max(0, previousBalance + pointsDelta);

        PointsLedger entry = PointsLedger.builder()
                .user(user)
                .pointsDelta(pointsDelta)
                .balanceAfter(newBalance)
                .transactionType(type)
                .referenceType(refType)
                .referenceId(refId)
                .description(description)
                .build();

        return pointsLedgerRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public PointsSummaryResponse getPointsSummary(UUID userId, String timezone) {
        int currentBalance = pointsLedgerRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(PointsLedger::getBalanceAfter)
                .orElse(0);

        int lifetimePoints = pointsLedgerRepository.calculateLifetimePoints(userId);

        LocalDate today = LocalDate.now(ZoneId.of(timezone != null ? timezone : "UTC"));
        Instant startOfDay = today.atStartOfDay(ZoneId.of(timezone != null ? timezone : "UTC")).toInstant();
        int pointsEarnedToday = pointsLedgerRepository.sumPointsEarnedSince(userId, startOfDay);

        Streak streak = streakRepository.findByUserId(userId).orElse(null);
        int currentStreak = streak != null ? streak.getCurrentStreak() : 0;
        int longestStreak = streak != null ? streak.getLongestStreak() : 0;
        LocalDate lastActivityDate = streak != null ? streak.getLastActivityDate() : null;
        int freezeCredits = streak != null ? streak.getFreezeCredits() : 0;

        int level = (int) Math.floor(Math.sqrt((double) lifetimePoints / 100.0)) + 1;
        int pointsForNextLevel = (int) (Math.pow(level, 2) * 100);
        int pointsToNextLevel = Math.max(0, pointsForNextLevel - lifetimePoints);

        return PointsSummaryResponse.builder()
                .currentBalance(currentBalance)
                .lifetimePoints(lifetimePoints)
                .pointsEarnedToday(pointsEarnedToday)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .lastActivityDate(lastActivityDate)
                .freezeCredits(freezeCredits)
                .userLevel(level)
                .pointsToNextLevel(pointsToNextLevel)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PointsLedgerResponse> getLedger(UUID userId, Pageable pageable) {
        return pointsLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToLedgerResponse);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 50));
        List<Object[]> rows = pointsLedgerRepository.findTopUsersLeaderboard(pageable);
        List<LeaderboardEntryResponse> entries = new ArrayList<>();

        // Batch fetch streaks to completely eliminate N+1 query problem
        List<UUID> userIds = rows.stream().map(r -> (UUID) r[0]).toList();
        Map<UUID, Integer> streakMap = streakRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(s -> s.getUser().getId(), Streak::getCurrentStreak));

        int rank = 1;
        for (Object[] row : rows) {
            UUID uid = (UUID) row[0];
            String name = (String) row[1];
            Number points = (Number) row[2];

            int userStreak = streakMap.getOrDefault(uid, 0);

            entries.add(LeaderboardEntryResponse.builder()
                    .rank(rank++)
                    .userId(uid)
                    .fullName(name)
                    .totalPoints(points != null ? points.intValue() : 0)
                    .streak(userStreak)
                    .build());
        }
        return entries;
    }

    private PointsLedgerResponse mapToLedgerResponse(PointsLedger ledger) {
        return PointsLedgerResponse.builder()
                .id(ledger.getId())
                .pointsDelta(ledger.getPointsDelta())
                .balanceAfter(ledger.getBalanceAfter())
                .transactionType(ledger.getTransactionType())
                .referenceType(ledger.getReferenceType())
                .referenceId(ledger.getReferenceId())
                .description(ledger.getDescription())
                .createdAt(ledger.getCreatedAt())
                .build();
    }
}
