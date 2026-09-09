package com.taskmonitor.scheduler;

import com.taskmonitor.module.gamification.service.StreakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyStreakScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyStreakScheduler.class);

    private final StreakService streakService;

    public DailyStreakScheduler(StreakService streakService) {
        this.streakService = streakService;
    }

    @Scheduled(cron = "${app.scheduler.streak-cron:0 0 0 * * *}")
    public void runStreakMaintenance() {
        log.info("Starting scheduled streak maintenance job...");
        try {
            streakService.evaluateDecayedStreaks();
            log.info("Completed scheduled streak maintenance job successfully.");
        } catch (Exception ex) {
            log.error("Failed executing streak maintenance job: {}", ex.getMessage(), ex);
        }
    }
}
