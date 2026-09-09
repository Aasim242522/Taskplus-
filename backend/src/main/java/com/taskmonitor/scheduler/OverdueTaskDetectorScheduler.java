package com.taskmonitor.scheduler;

import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.domain.TaskStatus;
import com.taskmonitor.module.task.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OverdueTaskDetectorScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueTaskDetectorScheduler.class);

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public OverdueTaskDetectorScheduler(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.scheduler.overdue-detector-cron:0 0/15 * * * *}")
    @Transactional
    public void detectAndFlagOverdueTasks() {
        log.debug("Running overdue task detector scan...");
        Instant now = Instant.now();
        List<Task> overdueTasks = taskRepository.findOverdueTasks(now);

        for (Task task : overdueTasks) {
            task.setStatus(TaskStatus.OVERDUE);
            taskRepository.save(task);

            notificationService.createNotification(
                    task.getUser(),
                    "Task Overdue: " + task.getTitle(),
                    "This task was due at " + task.getDueDateTime() + " and is now marked as overdue.",
                    NotificationType.TASK_OVERDUE
            );
            log.info("Marked task {} as OVERDUE for user {}", task.getId(), task.getUser().getId());
        }

        if (!overdueTasks.isEmpty()) {
            log.info("Flagged {} tasks as OVERDUE", overdueTasks.size());
        }
    }
}
