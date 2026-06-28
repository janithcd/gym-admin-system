package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.ReportSummary;
import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lk.janith.gymadmin.repository.MemberRepository;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final AccessLogRepository accessLogRepository;

    public List<Payment> getPaidPaymentsBetween(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByStatusAndPaymentDateBetweenOrderByPaymentDateAsc(
                PaymentStatus.PAID,
                startDate,
                endDate
        );
    }

    public ReportSummary getPaymentSummary(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = getPaidPaymentsBetween(startDate, endDate);

        BigDecimal totalIncome = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportSummary(
                payments.size(),
                payments.size(),
                totalIncome,
                0,
                0
        );
    }

    public List<Member> getExpiredMembers() {
        return memberRepository.findByStatus(MemberStatus.EXPIRED);
    }

    public List<AccessLog> getAccessLogsBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return accessLogRepository.findByAccessTimeBetweenOrderByAccessTimeAsc(start, end);
    }

    public ReportSummary getAccessSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        long granted = accessLogRepository.countByAccessStatusAndAccessTimeBetween(
                AccessStatus.GRANTED,
                start,
                end
        );

        long denied = accessLogRepository.countByAccessStatusAndAccessTimeBetween(
                AccessStatus.DENIED,
                start,
                end
        );

        long total = granted + denied;

        return new ReportSummary(
                total,
                0,
                BigDecimal.ZERO,
                granted,
                denied
        );
    }
}