package com.taskmonitor.module.gamification.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.taskmonitor.module.gamification.domain.Achievement;
import com.taskmonitor.module.gamification.domain.BadgeType;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryMasteryBadgeEvaluator implements BadgeRuleEvaluator {

    private final TaskRepository taskRepository;

    public CategoryMasteryBadgeEvaluator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public boolean supports(BadgeType badgeType) {
        return badgeType == BadgeType.CATEGORY_MASTERY;
    }

    @Override
    public boolean evaluate(Achievement badge, JsonNode criteria, BadgeEvaluationContext context) {
        String category = criteria.path("category").asText("");
        int targetCatCount = criteria.path("target").asInt(5);
        long catCompleted = taskRepository.countByUserIdAndCategoryNameAndStatus(
                context.getUser().getId(), category, TaskStatus.COMPLETED);
        return catCompleted >= targetCatCount;
    }

    @Override
    public int calculateProgress(Achievement badge, JsonNode criteria, BadgeEvaluationContext context, int target) {
        String category = criteria.path("category").asText("");
        long catCompleted = taskRepository.countByUserIdAndCategoryNameAndStatus(
                context.getUser().getId(), category, TaskStatus.COMPLETED);
        return (int) Math.min(catCompleted, target);
    }
}
