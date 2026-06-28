package lk.janith.gymadmin.service;

import jakarta.transaction.Transactional;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccessLogCleanupService {

    private final AccessLogRepository accessLogRepository;

    private static final int ACCESS_LOG_RETENTION_DAYS = 7;

    @Transactional
    public long deleteOldAccessLogs() {
        LocalDateTime cutoffDateTime = LocalDateTime.now()
                .minusDays(ACCESS_LOG_RETENTION_DAYS);

        long oldLogCount = accessLogRepository.countByAccessTimeBefore(cutoffDateTime);

        if (oldLogCount > 0) {
            accessLogRepository.deleteByAccessTimeBefore(cutoffDateTime);
        }

        return oldLogCount;
    }

    // Runs every day at 2:00 AM Sri Lanka time
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Colombo")
    public void scheduledAccessLogCleanup() {
        long deletedCount = deleteOldAccessLogs();

        System.out.println("Access log cleanup completed. Deleted logs: " + deletedCount);
    }
}