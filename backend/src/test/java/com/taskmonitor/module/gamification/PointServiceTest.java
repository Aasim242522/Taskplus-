package com.taskmonitor.module.gamification;

import com.taskmonitor.module.gamification.domain.PointsLedger;
import com.taskmonitor.module.gamification.repository.PointsLedgerRepository;
import com.taskmonitor.module.gamification.repository.StreakRepository;
import com.taskmonitor.module.gamification.service.PointService;
import com.taskmonitor.module.task.domain.Priority;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.dto.TaskCompletionResponse;
import com.taskmonitor.module.user.domain.Role;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointsLedgerRepository pointsLedgerRepository;

    @Mock
    private StreakRepository streakRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PointService pointService;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pointService, "basePoints", 50);
        ReflectionTestUtils.setField(pointService, "onTimeBonus", 25);
        ReflectionTestUtils.setField(pointService, "streakMultiplier", 2.5);
        ReflectionTestUtils.setField(pointService, "overduePenalty", 15);
        ReflectionTestUtils.setField(pointService, "dailyDiminishingCap", 500);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .timezone("UTC")
                .role(Role.ROLE_USER)
                .build();

        lenient().when(userRepository.findByIdWithLock(testUser.getId())).thenReturn(Optional.of(testUser));
        lenient().when(pointsLedgerRepository.findTopByUserIdOrderByCreatedAtDesc(testUser.getId()))
                .thenReturn(Optional.empty());
        lenient().when(pointsLedgerRepository.save(any(PointsLedger.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should award base, multiplier, on-time bonus, streak bonus, and efficiency bonus on on-time completion")
    void testAwardTaskCompletionPoints_OnTime_WithStreak() {
        Instant now = Instant.now();
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .title("Test High Difficulty Task")
                .priority(Priority.HIGH)
                .status(TaskStatus.COMPLETED)
                .difficultyLevel(4) // 2.0x multiplier -> 100 base
                .dueDateTime(now.plus(2, ChronoUnit.HOURS))
                .completedAt(now)
                .estimatedDurationMinutes(60)
                .actualDurationMinutes(30) // 50% time -> efficiency bonus
                .build();

        int currentStreak = 4; // streak bonus: floor(4 * 2.5) = 10
        TaskCompletionResponse.PointsBreakdown breakdown = pointService.awardTaskCompletionPoints(
                testUser, task, currentStreak
        );

        assertNotNull(breakdown);
        assertEquals(50, breakdown.getBasePoints());
        assertEquals(2.0, breakdown.getDifficultyMultiplier());
        assertEquals(25, breakdown.getOnTimeBonus());
        assertEquals(10, breakdown.getStreakBonus());
        assertTrue(breakdown.getDurationEfficiencyBonus() > 0);
        assertEquals(0, breakdown.getOverduePenalty());
        assertTrue(breakdown.getTotalPointsAwarded() >= 135);

        verify(userRepository, times(1)).findByIdWithLock(testUser.getId());
        verify(pointsLedgerRepository, times(1)).save(any(PointsLedger.class));
    }

    @Test
    @DisplayName("Should deduct overdue penalty and grant zero on-time bonus when task is completed past due date")
    void testAwardTaskCompletionPoints_Overdue_AppliesPenalty() {
        Instant now = Instant.now();
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .title("Overdue Task")
                .priority(Priority.LOW)
                .status(TaskStatus.COMPLETED)
                .difficultyLevel(1) // 1.0x -> 50 base
                .dueDateTime(now.minus(2, ChronoUnit.HOURS)) // past due
                .completedAt(now)
                .build();

        TaskCompletionResponse.PointsBreakdown breakdown = pointService.awardTaskCompletionPoints(
                testUser, task, 0
        );

        assertEquals(15, breakdown.getOverduePenalty());
        assertEquals(0, breakdown.getOnTimeBonus());
        // 50 base - 15 penalty = 35 points awarded
        assertEquals(35, breakdown.getTotalPointsAwarded());
    }

    @Test
    @DisplayName("Should enforce minimum floor of 5 points even when penalties exceed base points")
    void testAwardTaskCompletionPoints_MinimumFloorEnforced() {
        Instant now = Instant.now();
        ReflectionTestUtils.setField(pointService, "basePoints", 10);
        ReflectionTestUtils.setField(pointService, "overduePenalty", 50);

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .title("Severely Overdue Task")
                .status(TaskStatus.COMPLETED)
                .difficultyLevel(1)
                .dueDateTime(now.minus(24, ChronoUnit.HOURS))
                .completedAt(now)
                .build();

        TaskCompletionResponse.PointsBreakdown breakdown = pointService.awardTaskCompletionPoints(
                testUser, task, 0
        );

        assertEquals(5, breakdown.getTotalPointsAwarded(), "Points awarded must never fall below minimum floor of 5");
    }

    @Test
    @DisplayName("Should scale points by 50% when daily diminishing cap is exceeded")
    void testAwardTaskCompletionPoints_DailyDiminishingCapApplied() {
        Instant now = Instant.now();
        when(pointsLedgerRepository.sumPointsEarnedSince(eq(testUser.getId()), any(Instant.class)))
                .thenReturn(600); // Exceeds cap of 500

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .title("High Volume Task")
                .status(TaskStatus.COMPLETED)
                .difficultyLevel(2) // 1.2x -> 60 base
                .dueDateTime(now.plus(1, ChronoUnit.HOURS))
                .completedAt(now)
                .build();

        // Raw: 60 (diff) + 25 (on-time) = 85. With 50% diminishing scale: round(85 * 0.5) = 43
        TaskCompletionResponse.PointsBreakdown breakdown = pointService.awardTaskCompletionPoints(
                testUser, task, 0
        );

        assertEquals(43, breakdown.getTotalPointsAwarded());
    }

    @Test
    @DisplayName("Should correctly compute balanceAfter starting from existing non-zero ledger balance")
    void testRecordTransaction_AccumulatesBalance() {
        PointsLedger priorEntry = PointsLedger.builder()
                .balanceAfter(250)
                .build();
        when(pointsLedgerRepository.findTopByUserIdOrderByCreatedAtDesc(testUser.getId()))
                .thenReturn(Optional.of(priorEntry));

        PointsLedger result = pointService.recordTransaction(
                testUser, 50, com.taskmonitor.module.gamification.domain.TransactionType.TASK_COMPLETION,
                "TASK", UUID.randomUUID(), "Test task"
        );

        assertNotNull(result);
        assertEquals(300, result.getBalanceAfter());
        assertEquals(50, result.getPointsDelta());
        verify(userRepository, times(1)).findByIdWithLock(testUser.getId());
    }
}
