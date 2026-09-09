package com.taskmonitor.module.gamification;

import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import com.taskmonitor.module.gamification.domain.UserAchievement;
import com.taskmonitor.module.gamification.dto.BadgeResponse;
import com.taskmonitor.module.gamification.repository.AchievementRepository;
import com.taskmonitor.module.gamification.repository.UserAchievementRepository;
import com.taskmonitor.module.gamification.service.AchievementService;
import com.taskmonitor.module.gamification.service.PointService;
import com.taskmonitor.module.gamification.strategy.*;
import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.repository.TaskRepository;
import com.taskmonitor.module.user.domain.Role;
import com.taskmonitor.module.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PointService pointService;

    @Mock
    private NotificationService notificationService;

    private AchievementService achievementService;
    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        List<BadgeRuleEvaluator> evaluators = List.of(
                new StreakBadgeEvaluator(),
                new TaskCountBadgeEvaluator(),
                new CategoryMasteryBadgeEvaluator(taskRepository),
                new SpeedBadgeEvaluator()
        );

        achievementService = new AchievementService(
                achievementRepository,
                userAchievementRepository,
                taskRepository,
                pointService,
                notificationService,
                evaluators
        );

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("badgehero@example.com")
                .fullName("Badge Champion")
                .timezone("UTC")
                .role(Role.ROLE_USER)
                .build();

        testTask = Task.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .title("Sample Completed Task")
                .status(TaskStatus.COMPLETED)
                .estimatedDurationMinutes(60)
                .actualDurationMinutes(20) // qualifies for speed badge
                .build();
    }

    @Test
    @DisplayName("Should unlock streak badge when streak reaches criteria target")
    void testEvaluateBadges_StreakBadgeUnlocked() {
        Achievement streakBadge = Achievement.builder()
                .id(UUID.randomUUID())
                .code("STREAK_7")
                .name("Unstoppable")
                .description("Reach a 7-day streak")
                .badgeType(BadgeType.STREAK)
                .pointsReward(100)
                .criteriaMetadata("{\"target\": 7}")
                .build();

        when(achievementRepository.findUnearnedBadgesByUserId(testUser.getId()))
                .thenReturn(List.of(streakBadge));
        when(userAchievementRepository.existsByUserIdAndAchievementId(testUser.getId(), streakBadge.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<BadgeResponse> unlocked = achievementService.evaluateBadgesOnTaskCompletion(testUser, testTask, 7);

        assertEquals(1, unlocked.size());
        assertEquals("STREAK_7", unlocked.get(0).getCode());
        assertTrue(unlocked.get(0).isUnlocked());

        verify(pointService, times(1)).awardBadgePoints(testUser, 100, "STREAK_7", "Unstoppable");
        verify(notificationService, times(1)).createNotification(
                eq(testUser), contains("Badge Unlocked"), anyString(), eq(NotificationType.BADGE_UNLOCKED)
        );
    }

    @Test
    @DisplayName("Should unlock task count badge when total completed tasks reaches criteria target")
    void testEvaluateBadges_TaskCountBadgeUnlocked() {
        Achievement taskCountBadge = Achievement.builder()
                .id(UUID.randomUUID())
                .code("TASKS_10")
                .name("Getting Started")
                .description("Complete 10 tasks")
                .badgeType(BadgeType.TASK_COUNT)
                .pointsReward(50)
                .criteriaMetadata("{\"target\": 10}")
                .build();

        when(achievementRepository.findUnearnedBadgesByUserId(testUser.getId()))
                .thenReturn(List.of(taskCountBadge));
        when(taskRepository.countByUserIdAndStatus(testUser.getId(), TaskStatus.COMPLETED))
                .thenReturn(10L);
        when(userAchievementRepository.existsByUserIdAndAchievementId(testUser.getId(), taskCountBadge.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<BadgeResponse> unlocked = achievementService.evaluateBadgesOnTaskCompletion(testUser, testTask, 1);

        assertEquals(1, unlocked.size());
        assertEquals("TASKS_10", unlocked.get(0).getCode());
        verify(pointService, times(1)).awardBadgePoints(testUser, 50, "TASKS_10", "Getting Started");
    }

    @Test
    @DisplayName("Should unlock speed badge when actual duration is at or below 50% of estimated duration")
    void testEvaluateBadges_SpeedBadgeUnlocked() {
        Achievement speedBadge = Achievement.builder()
                .id(UUID.randomUUID())
                .code("SPEED_DEMON")
                .name("Speed Demon")
                .description("Complete task in half the estimated time")
                .badgeType(BadgeType.SPEED)
                .pointsReward(75)
                .criteriaMetadata("{}")
                .build();

        when(achievementRepository.findUnearnedBadgesByUserId(testUser.getId()))
                .thenReturn(List.of(speedBadge));
        when(userAchievementRepository.existsByUserIdAndAchievementId(testUser.getId(), speedBadge.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(i -> i.getArgument(0));

        // testTask has estimated=60 and actual=20 (<= 30)
        List<BadgeResponse> unlocked = achievementService.evaluateBadgesOnTaskCompletion(testUser, testTask, 1);

        assertEquals(1, unlocked.size());
        assertEquals("SPEED_DEMON", unlocked.get(0).getCode());
        verify(pointService, times(1)).awardBadgePoints(testUser, 75, "SPEED_DEMON", "Speed Demon");
    }

    @Test
    @DisplayName("Should prevent duplicate badge unlock when user already earned the badge")
    void testEvaluateBadges_SkipsAlreadyEarnedBadge() {
        Achievement badge = Achievement.builder()
                .id(UUID.randomUUID())
                .code("STREAK_3")
                .badgeType(BadgeType.STREAK)
                .criteriaMetadata("{\"target\": 3}")
                .build();

        when(achievementRepository.findUnearnedBadgesByUserId(testUser.getId()))
                .thenReturn(List.of(badge));
        // Simulate concurrent thread already having saved the user badge
        when(userAchievementRepository.existsByUserIdAndAchievementId(testUser.getId(), badge.getId()))
                .thenReturn(true);

        List<BadgeResponse> unlocked = achievementService.evaluateBadgesOnTaskCompletion(testUser, testTask, 5);

        assertTrue(unlocked.isEmpty(), "Already earned badge should not be re-unlocked");
        verify(userAchievementRepository, never()).save(any(UserAchievement.class));
        verify(pointService, never()).awardBadgePoints(any(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should gracefully handle malformed criteria JSON without failing task completion")
    void testEvaluateBadges_MalformedJsonHandledGracefully() {
        Achievement malformedBadge = Achievement.builder()
                .id(UUID.randomUUID())
                .code("CORRUPT_JSON")
                .badgeType(BadgeType.STREAK)
                .criteriaMetadata("INVALID_JSON_CONTENT{{{")
                .build();

        when(achievementRepository.findUnearnedBadgesByUserId(testUser.getId()))
                .thenReturn(List.of(malformedBadge));

        // Must not throw an unhandled exception
        assertDoesNotThrow(() -> {
            List<BadgeResponse> unlocked = achievementService.evaluateBadgesOnTaskCompletion(testUser, testTask, 5);
            assertTrue(unlocked.isEmpty());
        });
    }
}
