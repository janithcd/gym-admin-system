package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.entity.PaymentMethod;
import lk.janith.gymadmin.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberOrderByExpiryDateDesc(Member member);

    Optional<Payment> findTopByMemberAndStatusOrderByExpiryDateDesc(Member member, PaymentStatus status);

    List<Payment> findByStatusAndPaymentDateBetweenOrderByPaymentDateAsc(
            PaymentStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByStatusAndPaymentDate(PaymentStatus status, LocalDate paymentDate);

    @Query("""
            SELECT p FROM Payment p
            WHERE
                (
                    :keyword IS NULL OR
                    LOWER(p.member.memberCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(p.member.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(p.member.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(COALESCE(p.member.nic, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :paymentMethod IS NULL OR p.paymentMethod = :paymentMethod
                )
            AND
                (
                    :status IS NULL OR p.status = :status
                )
            AND
                (
                    :startDate IS NULL OR p.paymentDate >= :startDate
                )
            AND
                (
                    :endDate IS NULL OR p.paymentDate <= :endDate
                )
            ORDER BY p.paymentDate DESC, p.id DESC
            """)
    Page<Payment> searchPayments(
            @Param("keyword") String keyword,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}