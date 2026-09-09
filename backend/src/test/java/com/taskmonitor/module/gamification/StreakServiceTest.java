package com.taskmonitor.module.gamification;

import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.repository.StreakRepository;
import com.taskmonitor.module.gamification.service.StreakService;
import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.user.domain.Role;
import com.taskmonitor.module.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StreakServiceTest {

    @Mock
    private StreakRepository streakRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StreakService streakService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("streakuser@example.com")
                .fullName("Streak Hero")
                .timezone("America/New_York")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should initialize streak to 1 and grant 1 freeze credit on first user activity")
    void testRecordDailyActivity_FirstTimeActivity() {
        when(streakRepository.findByUserIdWithLock(testUser.getId())).thenReturn(Optional.empty());
        when(streakRepository.save(any(Streak.class))).thenAnswer(i -> i.getArgument(0));

        Streak result = streakService.recordDailyActivity(testUser);

        assertNotNull(result);
        assertEquals(1, result.getCurrentStreak());
        assertEquals(1, result.getLongestStreak());
        assertEquals(1, result.getFreezeCredits());
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        assertEquals(todayInTz, result.getLastActivityDate());
        verify(streakRepository, times(1)).save(any(Streak.class));
    }

    @Test
    @DisplayName("Should remain idempotent when multiple tasks are completed on the same calendar day")
    void testRecordDailyActivity_SameDayActivity_StreakUnchanged() {
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        Streak existingStreak = Streak.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .currentStreak(5)
                .longestStreak(10)
                .freezeCredits(2)
                .lastActivityDate(todayInTz)
                .build();

        when(streakRepository.findByUserIdWithLock(testUser.getId())).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(i -> i.getArgument(0));

        Streak result = streakService.recordDailyActivity(testUser);

        assertEquals(5, result.getCurrentStreak());
        assertEquals(10, result.getLongestStreak());
        assertEquals(2, result.getFreezeCredits());
    }

    @Test
    @DisplayName("Should increment streak when activity occurs on consecutive day and trigger milestone notification")
    void testRecordDailyActivity_ConsecutiveDayActivity_IncrementsStreak() {
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        Streak existingStreak = Streak.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .currentStreak(6)
                .longestStreak(6)
                .freezeCredits(1)
                .lastActivityDate(todayInTz.minusDays(1)) // Yesterday
                .build();

        when(streakRepository.findByUserIdWithLock(testUser.getId())).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(i -> i.getArgument(0));

        Streak result = streakService.recordDailyActivity(testUser);

        assertEquals(7, result.getCurrentStreak());
        assertEquals(7, result.getLongestStreak());
        assertEquals(todayInTz, result.getLastActivityDate());

        // 7-day milestone triggers notification
        verify(notificationService, times(1)).createNotification(
                eq(testUser), anyString(), anyString(), eq(NotificationType.STREAK_MILESTONE)
        );
    }

    @Test
    @DisplayName("Should consume 1 freeze credit and preserve streak when user misses exactly 1 day")
    void testRecordDailyActivity_MissedOneDayWithFreezeCredit_PreservesStreak() {
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        Streak existingStreak = Streak.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .currentStreak(14)
                .longestStreak(20)
                .freezeCredits(2)
                .lastActivityDate(todayInTz.minusDays(2)) // Missed yesterday
                .build();

        when(streakRepository.findByUserIdWithLock(testUser.getId())).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(i -> i.getArgument(0));

        Streak result = streakService.recordDailyActivity(testUser);

        assertEquals(15, result.getCurrentStreak(), "Streak should continue to 15 after freeze consumption");
        assertEquals(1, result.getFreezeCredits(), "Freeze credit should be decremented by 1");
        assertEquals(todayInTz, result.getLastActivityDate());

        verify(notificationService, times(1)).createNotification(
                eq(testUser), contains("Streak Freeze Activated"), anyString(), eq(NotificationType.STREAK_MILESTONE)
        );
    }

    @Test
    @DisplayName("Should reset streak to 1 when user misses days without available freeze credits")
    void testRecordDailyActivity_MissedDaysNoFreezeCredits_ResetsStreak() {
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        Streak existingStreak = Streak.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .currentStreak(10)
                .longestStreak(10)
                .freezeCredits(0) // No freezes available
                .lastActivityDate(todayInTz.minusDays(3))
                .build();

        when(streakRepository.findByUserIdWithLock(testUser.getId())).thenReturn(Optional.of(existingStreak));
        when(streakRepository.save(any(Streak.class))).thenAnswer(i -> i.getArgument(0));

        Streak result = streakService.recordDailyActivity(testUser);

        assertEquals(1, result.getCurrentStreak(), "Streak should reset to 1");
        assertEquals(10, result.getLongestStreak(), "Longest streak must be preserved");
    }

    @Test
    @DisplayName("Background decay should zero out streak and notify user when activity lapsed without freeze credits")
    void testEvaluateDecayedStreaks_ZerosOutInactiveStreak() {
        LocalDate todayInTz = LocalDate.now(ZoneId.of(testUser.getTimezone()));
        Streak inactiveStreak = Streak.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .currentStreak(8)
                .longestStreak(8)
                .freezeCredits(0)
                .lastActivityDate(todayInTz.minusDays(2))
                .build();

        when(streakRepository.findAll()).thenReturn(List.of(inactiveStreak));

        streakService.evaluateDecayedStreaks();

        assertEquals(0, inactiveStreak.getCurrentStreak());
        verify(notificationService, times(1)).createNotification(
                eq(testUser), contains("Streak Ended"), anyString(), eq(NotificationType.STREAK_MILESTONE)
        );
        verify(streakRepository, times(1)).save(inactiveStreak);
    }
}
