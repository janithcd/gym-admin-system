package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.AccessLog;
import lk.janith.gymadmin.entity.AccessStatus;
import lk.janith.gymadmin.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    List<AccessLog> findAllByOrderByAccessTimeDesc();

    List<AccessLog> findByMemberOrderByAccessTimeDesc(Member member);

    long countByAccessTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByAccessStatusAndAccessTimeBetween(
            AccessStatus accessStatus,
            LocalDateTime start,
            LocalDateTime end
    );

    List<AccessLog> findByAccessTimeBetweenOrderByAccessTimeAsc(
            LocalDateTime start,
            LocalDateTime end
    );
}