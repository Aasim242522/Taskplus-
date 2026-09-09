package com.taskmonitor.scheduler;

import com.taskmonitor.module.notification.domain.NotificationType;
import com.taskmonitor.module.notification.service.NotificationService;
import com.taskmonitor.module.task.domain.Task;
import com.taskmonitor.module.task.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ReminderDispatcherScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderDispatcherScheduler.class);

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public ReminderDispatcherScheduler(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.scheduler.reminder-cron:0 0/10 * * * *}")
    @Transactional
    public void dispatchUpcomingTaskReminders() {
        log.debug("Scanning for upcoming task reminders...");
        Instant now = Instant.now();
        Instant windowEnd = now.plus(30, ChronoUnit.MINUTES);

        List<Task> upcoming = taskRepository.findUpcomingTasksForReminder(now, windowEnd);
        for (Task task : upcoming) {
            notificationService.createNotification(
                    task.getUser(),
                    "Upcoming Task Reminder: " + task.getTitle(),
                    "Task is due soon at " + task.getDueDateTime(),
                    NotificationType.TASK_DUE_REMINDER
            );
        }

        if (!upcoming.isEmpty()) {
            log.info("Dispatched {} upcoming task reminders", upcoming.size());
        }
    }
}
