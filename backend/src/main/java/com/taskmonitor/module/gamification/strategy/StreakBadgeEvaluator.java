package com.taskmonitor.module.gamification.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import org.springframework.stereotype.Component;

@Component
public class StreakBadgeEvaluator implements BadgeRuleEvaluator {

    @Override
    public boolean supports(BadgeType badgeType) {
        return badgeType == BadgeType.STREAK;
    }

    @Override
    public boolean evaluate(Achievement badge, JsonNode criteria, BadgeEvaluationContext context) {
        int targetStreak = criteria.path("target").asInt(7);
        return context.getCurrentStreak() >= targetStreak;
    }

    @Override
    public int calculateProgress(Achievement badge, JsonNode criteria, BadgeEvaluationContext context, int target) {
        return Math.min(context.getCurrentStreak(), target);
    }
}
