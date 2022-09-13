package dk.liga.mobileid.backendapi.notification.infrastructure;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import dk.liga.mobileid.backendapi.notification.domain.NotificationRepository;

@Configuration
@EnableAsync
@EnableScheduling
public class PruneNotifications {
    @Autowired
    NotificationRepository repo;

    Logger logger = LoggerFactory.getLogger(PruneNotifications.class);

    @Async
    @Scheduled(fixedRate = 1000 * 60)
    public void scheduleFixedRateTaskAsync() throws InterruptedException {
        logger.info("Pruning old notifications");
        repo.deleteByCreatedAtLessThan(LocalDateTime.now().minusMinutes(1));
    }

}