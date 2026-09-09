package com.taskmonitor.module.gamification.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import org.springframework.stereotype.Component;

@Component
public class TaskCountBadgeEvaluator implements BadgeRuleEvaluator {

    @Override
    public boolean supports(BadgeType badgeType) {
        return badgeType == BadgeType.TASK_COUNT;
    }

    @Override
    public boolean evaluate(Achievement badge, JsonNode criteria, BadgeEvaluationContext context) {
        int targetCount = criteria.path("target").asInt(10);
        return context.getTotalCompletedTasks() >= targetCount;
    }

    @Override
    public int calculateProgress(Achievement badge, JsonNode criteria, BadgeEvaluationContext context, int target) {
        return (int) Math.min(context.getTotalCompletedTasks(), target);
    }
}
