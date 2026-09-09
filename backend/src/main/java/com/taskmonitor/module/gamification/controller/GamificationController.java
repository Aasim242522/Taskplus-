package com.taskmonitor.module.gamification.controller;

import com.taskmonitor.common.response.ApiResponse;
import com.taskmonitor.common.response.PageResponse;
import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.dto.BadgeResponse;
import com.taskmonitor.module.gamification.dto.LeaderboardEntryResponse;
import com.taskmonitor.module.gamification.dto.PointsLedgerResponse;
import com.taskmonitor.module.gamification.dto.PointsSummaryResponse;
import com.taskmonitor.module.gamification.service.AchievementService;
import com.taskmonitor.module.gamification.service.PointService;
import com.taskmonitor.module.gamification.service.StreakService;
import com.taskmonitor.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamification")
@Tag(name = "Gamification", description = "Points ledger, streaks, achievement badges, and leaderboards")
public class GamificationController {

    private final PointService pointService;
    private final StreakService streakService;
    private final AchievementService achievementService;

    public GamificationController(PointService pointService, StreakService streakService, AchievementService achievementService) {
        this.pointService = pointService;
        this.streakService = streakService;
        this.achievementService = achievementService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get user points balance, level, and streak details")
    public ResponseEntity<ApiResponse<PointsSummaryResponse>> getSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        PointsSummaryResponse summary = pointService.getPointsSummary(principal.getId(), principal.getTimezone());
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/ledger")
    @Operation(summary = "Get paginated points audit ledger")
    public ResponseEntity<ApiResponse<PageResponse<PointsLedgerResponse>>> getLedger(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = pointService.getLedger(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/badges")
    @Operation(summary = "Get all available badges with user progress and unlocked status")
    public ResponseEntity<ApiResponse<List<BadgeResponse>>> getBadges(
            @AuthenticationPrincipal UserPrincipal principal) {
        Streak streak = streakService.getStreakByUserId(principal.getId());
        int currentStreak = streak != null ? streak.getCurrentStreak() : 0;
        List<BadgeResponse> badges = achievementService.getAllBadgesWithUserProgress(principal.getId(), currentStreak);
        return ResponseEntity.ok(ApiResponse.ok(badges));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get top users leaderboard by points")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntryResponse> leaderboard = pointService.getLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.ok(leaderboard));
    }
}
