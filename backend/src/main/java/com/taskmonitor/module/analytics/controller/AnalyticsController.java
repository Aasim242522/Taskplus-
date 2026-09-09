package com.taskmonitor.module.analytics.controller;

import com.taskmonitor.common.response.ApiResponse;
import com.taskmonitor.module.analytics.dto.HeatmapDayResponse;
import com.taskmonitor.module.analytics.dto.MonthlyAnalyticsResponse;
import com.taskmonitor.module.analytics.dto.WeeklyAnalyticsResponse;
import com.taskmonitor.module.analytics.service.AnalyticsService;
import com.taskmonitor.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Performance analytics, weekly/monthly graphs, and activity heatmap")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get trailing 7-day completion and points metrics")
    public ResponseEntity<ApiResponse<WeeklyAnalyticsResponse>> getWeeklyAnalytics(
            @AuthenticationPrincipal UserPrincipal principal) {
        WeeklyAnalyticsResponse response = analyticsService.getWeeklyAnalytics(
                principal.getId(), principal.getTimezone());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly completion trend points and focus time")
    public ResponseEntity<ApiResponse<MonthlyAnalyticsResponse>> getMonthlyAnalytics(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        MonthlyAnalyticsResponse response = analyticsService.getMonthlyAnalytics(
                principal.getId(), targetYear, targetMonth, principal.getTimezone());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/heatmap")
    @Operation(summary = "Get activity heatmap matrix for calendar grid (GitHub style)")
    public ResponseEntity<ApiResponse<List<HeatmapDayResponse>>> getHeatmap(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "60") int days) {
        List<HeatmapDayResponse> heatmap = analyticsService.getActivityHeatmap(
                principal.getId(), days, principal.getTimezone());
        return ResponseEntity.ok(ApiResponse.ok(heatmap));
    }
}
