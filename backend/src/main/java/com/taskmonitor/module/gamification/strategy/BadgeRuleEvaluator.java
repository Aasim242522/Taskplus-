package com.taskmonitor.module.gamification.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;

public interface BadgeRuleEvaluator {

    boolean supports(BadgeType badgeType);

    boolean evaluate(Achievement badge, JsonNode criteria, BadgeEvaluationContext context);

    int calculateProgress(Achievement badge, JsonNode criteria, BadgeEvaluationContext context, int target);
}
