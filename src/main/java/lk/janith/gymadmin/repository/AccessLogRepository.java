package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.AccessLog;
import lk.janith.gymadmin.entity.AccessStatus;
import lk.janith.gymadmin.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    long countByAccessTimeBefore(LocalDateTime cutoffDateTime);

    void deleteByAccessTimeBefore(LocalDateTime cutoffDateTime);

    @Query(
            value = """
                    SELECT l FROM AccessLog l
                    LEFT JOIN l.member m
                    WHERE
                        (
                            :keyword IS NULL OR
                            LOWER(l.enteredMemberCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.memberCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.nic, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                    AND
                        (
                            :accessStatus IS NULL OR l.accessStatus = :accessStatus
                        )
                    AND
                        (
                            :startDateTime IS NULL OR l.accessTime >= :startDateTime
                        )
                    AND
                        (
                            :endDateTime IS NULL OR l.accessTime <= :endDateTime
                        )
                    ORDER BY l.accessTime DESC
                    """,
            countQuery = """
                    SELECT COUNT(l) FROM AccessLog l
                    LEFT JOIN l.member m
                    WHERE
                        (
                            :keyword IS NULL OR
                            LOWER(l.enteredMemberCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.memberCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                            LOWER(COALESCE(m.nic, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                    AND
                        (
                            :accessStatus IS NULL OR l.accessStatus = :accessStatus
                        )
                    AND
                        (
                            :startDateTime IS NULL OR l.accessTime >= :startDateTime
                        )
                    AND
                        (
                            :endDateTime IS NULL OR l.accessTime <= :endDateTime
                        )
                    """
    )
    Page<AccessLog> searchAccessLogs(
            @Param("keyword") String keyword,
            @Param("accessStatus") AccessStatus accessStatus,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}