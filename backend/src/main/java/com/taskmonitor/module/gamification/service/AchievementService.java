package com.taskmonitor.module.gamification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.UserAchievement;
import com.taskmonitor.module.gamification.dto.BadgeResponse;
import com.taskmonitor.module.gamification.repository.AchievementRepository;
import com.taskmonitor.module.gamification.repository.UserAchievementRepository;
import com.taskmonitor.module.gamification.strategy.BadgeEvaluationContext;
import com.taskmonitor.module.gamification.strategy.BadgeRuleEvaluator;
import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.repository.TaskRepository;
import com.taskmonitor.module.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final TaskRepository taskRepository;
    private final PointService pointService;
    private final NotificationService notificationService;
    private final List<BadgeRuleEvaluator> evaluators;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              TaskRepository taskRepository,
                              PointService pointService,
                              NotificationService notificationService,
                              List<BadgeRuleEvaluator> evaluators) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.taskRepository = taskRepository;
        this.pointService = pointService;
        this.notificationService = notificationService;
        this.evaluators = evaluators;
    }

    @Transactional
    public List<BadgeResponse> evaluateBadgesOnTaskCompletion(User user, Task task, int currentStreak) {
        List<Achievement> unearned = achievementRepository.findUnearnedBadgesByUserId(user.getId());
        if (unearned.isEmpty()) {
            return Collections.emptyList();
        }

        long totalCompleted = taskRepository.countByUserIdAndStatus(user.getId(), TaskStatus.COMPLETED);
        BadgeEvaluationContext context = new BadgeEvaluationContext(user, task, currentStreak, totalCompleted);
        List<BadgeResponse> newlyUnlocked = new ArrayList<>();

        for (Achievement badge : unearned) {
            try {
                // Idempotency check: if already earned (e.g. concurrent thread), skip
                if (userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), badge.getId())) {
                    continue;
                }

                JsonNode criteria = objectMapper.readTree(badge.getCriteriaMetadata());

                Optional<BadgeRuleEvaluator> evaluator = evaluators.stream()
                        .filter(e -> e.supports(badge.getBadgeType()))
                        .findFirst();

                if (evaluator.isPresent() && evaluator.get().evaluate(badge, criteria, context)) {
                    UserAchievement ua = UserAchievement.builder()
                            .user(user)
                            .achievement(badge)
                            .build();

                    try {
                        UserAchievement saved = userAchievementRepository.save(ua);

                        pointService.awardBadgePoints(user, badge.getPointsReward(), badge.getCode(), badge.getName());

                        notificationService.createNotification(
                                user,
                                "Badge Unlocked: " + badge.getName() + " 🏆",
                                badge.getDescription() + " (+" + badge.getPointsReward() + " pts)",
                                NotificationType.BADGE_UNLOCKED
                        );

                        log.info("User {} unlocked badge: {}", user.getId(), badge.getName());
                        newlyUnlocked.add(BadgeResponse.builder()
                                .id(badge.getId())
                                .code(badge.getCode())
                                .name(badge.getName())
                                .description(badge.getDescription())
                                .iconUrl(badge.getIconUrl())
                                .badgeType(badge.getBadgeType())
                                .pointsReward(badge.getPointsReward())
                                .unlocked(true)
                                .unlockedAt(saved.getUnlockedAt())
                                .progress(100)
                                .target(100)
                                .build());
                    } catch (DataIntegrityViolationException dive) {
                        log.warn("Badge {} already unlocked concurrently for user {}", badge.getCode(), user.getId());
                    }
                }
            } catch (Exception ex) {
                log.error("Error evaluating badge criteria for badge {}: {}", badge.getCode(), ex.getMessage());
            }
        }

        return newlyUnlocked;
    }

    @Transactional(readOnly = true)
    public List<BadgeResponse> getAllBadgesWithUserProgress(UUID userId, int currentStreak) {
        List<Achievement> allBadges = achievementRepository.findAll();
        List<UserAchievement> userBadges = userAchievementRepository.findAllByUserIdOrderByUnlockedAtDesc(userId);

        Map<UUID, UserAchievement> earnedMap = new HashMap<>();
        for (UserAchievement ua : userBadges) {
            earnedMap.put(ua.getAchievement().getId(), ua);
        }

        long totalCompleted = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        BadgeEvaluationContext context = new BadgeEvaluationContext(null, null, currentStreak, totalCompleted);

        List<BadgeResponse> results = new ArrayList<>();
        for (Achievement badge : allBadges) {
            boolean unlocked = earnedMap.containsKey(badge.getId());
            int progress = 0;
            int target = 1;

            try {
                JsonNode criteria = objectMapper.readTree(badge.getCriteriaMetadata());
                target = criteria.path("target").asInt(1);

                if (unlocked) {
                    progress = target;
                } else {
                    Optional<BadgeRuleEvaluator> evaluator = evaluators.stream()
                            .filter(e -> e.supports(badge.getBadgeType()))
                            .findFirst();

                    if (evaluator.isPresent()) {
                        progress = evaluator.get().calculateProgress(badge, criteria, context, target);
                    }
                }
            } catch (Exception e) {
                log.warn("Could not parse criteria metadata for badge {}", badge.getCode());
            }

            results.add(BadgeResponse.builder()
                    .id(badge.getId())
                    .code(badge.getCode())
                    .name(badge.getName())
                    .description(badge.getDescription())
                    .iconUrl(badge.getIconUrl())
                    .badgeType(badge.getBadgeType())
                    .pointsReward(badge.getPointsReward())
                    .unlocked(unlocked)
                    .unlockedAt(unlocked ? earnedMap.get(badge.getId()).getUnlockedAt() : null)
                    .progress(progress)
                    .target(target)
                    .build());
        }

        return results;
    }
}
