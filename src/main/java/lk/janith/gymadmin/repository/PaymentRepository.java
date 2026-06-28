package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}