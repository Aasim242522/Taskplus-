package com.taskmonitor.module.task.service;

import com.taskmonitor.common.exception.BusinessException;
import com.taskmonitor.common.exception.ErrorCode;
import com.taskmonitor.common.exception.ResourceNotFoundException;
import com.taskmonitor.module.gamification.domain.PointsLedger;
import com.taskmonitor.module.gamification.domain.Streak;
import com.taskmonitor.module.gamification.dto.BadgeResponse;
import com.taskmonitor.module.gamification.repository.PointsLedgerRepository;
import com.taskmonitor.module.gamification.service.AchievementService;
import com.taskmonitor.module.gamification.service.PointService;
import com.taskmonitor.module.gamification.service.StreakService;
import com.taskmonitor.module.task.domain.*;
import com.taskmonitor.module.task.dto.*;
import com.taskmonitor.module.task.repository.CategoryRepository;
import com.taskmonitor.module.task.repository.TagRepository;
import com.taskmonitor.module.task.repository.TaskRepository;
import com.taskmonitor.module.user.domain.User;
import com.taskmonitor.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final StreakService streakService;
    private final AchievementService achievementService;
    private final PointsLedgerRepository pointsLedgerRepository;

    public TaskService(TaskRepository taskRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       UserRepository userRepository,
                       PointService pointService,
                       StreakService streakService,
                       AchievementService achievementService,
                       PointsLedgerRepository pointsLedgerRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.pointService = pointService;
        this.streakService = streakService;
        this.achievementService = achievementService;
        this.pointsLedgerRepository = pointsLedgerRepository;
    }

    @Transactional
    public TaskResponse createTask(UUID userId, CreateTaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        Set<Tag> tags = resolveTags(user, request.getTags());

        Task task = Task.builder()
                .user(user)
                .category(category)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .status(TaskStatus.PENDING)
                .recurrenceType(request.getRecurrenceType() != null ? request.getRecurrenceType() : RecurrenceType.NONE)
                .dueDateTime(request.getDueDateTime())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes() != null ? request.getEstimatedDurationMinutes() : 0)
                .actualDurationMinutes(0)
                .difficultyLevel(request.getDifficultyLevel() != null ? request.getDifficultyLevel() : 2)
                .tags(tags)
                .build();

        Task saved = taskRepository.save(task);
        log.info("Created task '{}' with ID {} for user {}", saved.getTitle(), saved.getId(), userId);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(UUID userId, TaskStatus status, UUID categoryId, Priority priority, Pageable pageable) {
        return taskRepository.findFiltered(userId, status, categoryId, priority, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID userId, UUID taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID userId, UUID taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            task.setCategory(category);
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            if (request.getStatus() == TaskStatus.COMPLETED) {
                throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                        "Task completion must be performed via POST /api/v1/tasks/{id}/complete to trigger point scoring and streak calculation");
            }
            task.setStatus(request.getStatus());
        }
        if (request.getRecurrenceType() != null) {
            task.setRecurrenceType(request.getRecurrenceType());
        }
        if (request.getDueDateTime() != null) {
            task.setDueDateTime(request.getDueDateTime());
        }
        if (request.getEstimatedDurationMinutes() != null) {
            task.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        }
        if (request.getActualDurationMinutes() != null) {
            task.setActualDurationMinutes(request.getActualDurationMinutes());
        }
        if (request.getDifficultyLevel() != null) {
            task.setDifficultyLevel(request.getDifficultyLevel());
        }
        if (request.getTags() != null) {
            task.setTags(resolveTags(task.getUser(), request.getTags()));
        }

        Task updated = taskRepository.save(task);
        log.info("Updated task {} for user {}", taskId, userId);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTask(UUID userId, UUID taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        taskRepository.delete(task);
        log.info("Deleted task {} for user {}", taskId, userId);
    }

    @Transactional
    public TaskResponse checkIn(UUID userId, UUID taskId, CheckInRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        int currentActual = task.getActualDurationMinutes() != null ? task.getActualDurationMinutes() : 0;
        task.setActualDurationMinutes(currentActual + request.getAdditionalMinutes());
        if (task.getStatus() == TaskStatus.PENDING) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }

        Task saved = taskRepository.save(task);
        log.info("Checked in {} mins on task {}", request.getAdditionalMinutes(), taskId);
        return mapToResponse(saved);
    }

    @Transactional
    public TaskCompletionResponse completeTask(UUID userId, UUID taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.TASK_ALREADY_COMPLETED);
        }

        User user = task.getUser();
        Instant now = Instant.now();
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(now);
        taskRepository.save(task);

        // 1. Update Streak
        Streak updatedStreak = streakService.recordDailyActivity(user);

        // 2. Award Points & Write Immutable Ledger
        TaskCompletionResponse.PointsBreakdown breakdown = pointService.awardTaskCompletionPoints(
                user, task, updatedStreak.getCurrentStreak()
        );

        // 3. Evaluate Unlocked Badges
        List<BadgeResponse> unlockedBadges = achievementService.evaluateBadgesOnTaskCompletion(
                user, task, updatedStreak.getCurrentStreak()
        );

        // 4. Calculate New Total Points
        int newTotalPoints = pointsLedgerRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(PointsLedger::getBalanceAfter)
                .orElse(0);

        // 5. Handle Recurring Task Next Instance Generation
        if (task.getRecurrenceType() != null && task.getRecurrenceType() != RecurrenceType.NONE) {
            spawnNextRecurringInstance(task);
        }

        int pointsAwarded = breakdown.getTotalPointsAwarded();

        log.info("Completed task {} for user {}: awarded {} points", taskId, userId, pointsAwarded);

        return TaskCompletionResponse.builder()
                .taskId(task.getId())
                .status(task.getStatus().name())
                .completedAt(task.getCompletedAt())
                .pointsAwarded(pointsAwarded)
                .breakdown(breakdown)
                .newTotalPoints(newTotalPoints)
                .currentStreak(updatedStreak.getCurrentStreak())
                .badgesUnlocked(unlockedBadges)
                .build();
    }

    private void spawnNextRecurringInstance(Task task) {
        Instant nextDue = null;
        if (task.getDueDateTime() != null) {
            String tz = task.getUser().getTimezone() != null ? task.getUser().getTimezone() : "UTC";
            java.time.ZoneId zoneId;
            try {
                zoneId = java.time.ZoneId.of(tz);
            } catch (Exception e) {
                zoneId = java.time.ZoneId.of("UTC");
            }
            java.time.ZonedDateTime zonedDue = task.getDueDateTime().atZone(zoneId);
            java.time.ZonedDateTime nextZoned = switch (task.getRecurrenceType()) {
                case DAILY -> zonedDue.plusDays(1);
                case WEEKLY -> zonedDue.plusWeeks(1);
                case MONTHLY -> zonedDue.plusMonths(1);
                default -> null;
            };

            // If calculated next due date is already in the past (late completion), advance to next future cycle
            if (nextZoned != null) {
                java.time.ZonedDateTime nowZoned = java.time.ZonedDateTime.now(zoneId);
                while (nextZoned.isBefore(nowZoned)) {
                    nextZoned = switch (task.getRecurrenceType()) {
                        case DAILY -> nextZoned.plusDays(1);
                        case WEEKLY -> nextZoned.plusWeeks(1);
                        case MONTHLY -> nextZoned.plusMonths(1);
                        default -> nextZoned;
                    };
                }
                nextDue = nextZoned.toInstant();
            }
        }

        Task nextTask = Task.builder()
                .user(task.getUser())
                .category(task.getCategory())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(TaskStatus.PENDING)
                .recurrenceType(task.getRecurrenceType())
                .dueDateTime(nextDue)
                .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
                .actualDurationMinutes(0)
                .difficultyLevel(task.getDifficultyLevel())
                .parentRecurringTaskId(task.getParentRecurringTaskId() != null ? task.getParentRecurringTaskId() : task.getId())
                .tags(new HashSet<>(task.getTags()))
                .build();

        taskRepository.save(nextTask);
        log.info("Spawned next recurring task instance for parent {}", task.getId());
    }

    private Set<Tag> resolveTags(User user, Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Tag> resolved = new HashSet<>();
        for (String name : tagNames) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            Tag tag = tagRepository.findByNameAndUserId(trimmed, user.getId())
                    .orElseGet(() -> tagRepository.save(Tag.builder().user(user).name(trimmed).build()));
            resolved.add(tag);
        }
        return resolved;
    }

    public TaskResponse mapToResponse(Task task) {
        CategoryResponse categoryResponse = null;
        if (task.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .colorHex(task.getCategory().getColorHex())
                    .icon(task.getCategory().getIcon())
                    .isSystem(task.getCategory().isSystem())
                    .createdAt(task.getCategory().getCreatedAt())
                    .build();
        }

        Set<String> tagNames = task.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .category(categoryResponse)
                .priority(task.getPriority())
                .status(task.getStatus())
                .recurrenceType(task.getRecurrenceType())
                .dueDateTime(task.getDueDateTime())
                .completedAt(task.getCompletedAt())
                .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
                .actualDurationMinutes(task.getActualDurationMinutes())
                .difficultyLevel(task.getDifficultyLevel())
                .tags(tagNames)
                .version(task.getVersion())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
