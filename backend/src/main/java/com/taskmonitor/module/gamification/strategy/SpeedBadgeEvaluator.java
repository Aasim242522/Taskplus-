package com.taskmonitor.module.gamification.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import org.springframework.stereotype.Component;

@Component
public class SpeedBadgeEvaluator implements BadgeRuleEvaluator {

    @Override
    public boolean supports(BadgeType badgeType) {
        return badgeType == BadgeType.SPEED;
    }

    @Override
    public boolean evaluate(Achievement badge, JsonNode criteria, BadgeEvaluationContext context) {
        if (context.getTask() == null) return false;
        Integer est = context.getTask().getEstimatedDurationMinutes();
        Integer actual = context.getTask().getActualDurationMinutes();
        if (est != null && est > 0 && actual != null && actual > 0) {
            return actual <= (est / 2);
        }
        return false;
    }

    @Override
    public int calculateProgress(Achievement badge, JsonNode criteria, BadgeEvaluationContext context, int target) {
        return evaluate(badge, criteria, context) ? target : 0;
    }
}
