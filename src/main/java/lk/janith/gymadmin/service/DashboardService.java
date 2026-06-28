package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.DashboardStats;
import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lk.janith.gymadmin.repository.MemberRepository;
import lk.janith.gymadmin.repository.MembershipPlanRepository;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentRepository paymentRepository;
    private final AccessLogRepository accessLogRepository;

    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();

        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today;

        LocalDate last7Start = today.minusDays(6);
        LocalDate last7End = today;

        LocalDateTime todayStartTime = today.atStartOfDay();
        LocalDateTime todayEndTime = today.plusDays(1).atStartOfDay().minusNanos(1);

        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        long expiredMembers = memberRepository.countByStatus(MemberStatus.EXPIRED);
        long suspendedMembers = memberRepository.countByStatus(MemberStatus.SUSPENDED);

        long maleMembers = memberRepository.countByGenderIgnoreCase("Male");
        long femaleMembers = memberRepository.countByGenderIgnoreCase("Female");
        long otherGenderMembers = totalMembers - maleMembers - femaleMembers;

        if (otherGenderMembers < 0) {
            otherGenderMembers = 0;
        }

        long totalPlans = membershipPlanRepository.count();
        long activePlans = membershipPlanRepository.countByStatus(MembershipPlanStatus.ACTIVE);

        List<Payment> todayPaymentList = paymentRepository
                .findByStatusAndPaymentDateBetweenOrderByPaymentDateAsc(
                        PaymentStatus.PAID,
                        today,
                        today
                );

        BigDecimal todayIncome = sumPayments(todayPaymentList);
        long todayPayments = todayPaymentList.size();

        List<Payment> monthlyPaymentList = paymentRepository
                .findByStatusAndPaymentDateBetweenOrderByPaymentDateAsc(
                        PaymentStatus.PAID,
                        monthStart,
                        monthEnd
                );

        BigDecimal monthlyIncome = sumPayments(monthlyPaymentList);

        long todayCheckIns = accessLogRepository.countByAccessTimeBetween(
                todayStartTime,
                todayEndTime
        );

        long todayAccessGranted = accessLogRepository.countByAccessStatusAndAccessTimeBetween(
                AccessStatus.GRANTED,
                todayStartTime,
                todayEndTime
        );

        long todayAccessDenied = accessLogRepository.countByAccessStatusAndAccessTimeBetween(
                AccessStatus.DENIED,
                todayStartTime,
                todayEndTime
        );

        List<Payment> last7DaysPayments = paymentRepository
                .findByStatusAndPaymentDateBetweenOrderByPaymentDateAsc(
                        PaymentStatus.PAID,
                        last7Start,
                        last7End
                );

        List<AccessLog> last7DaysLogs = accessLogRepository
                .findByAccessTimeBetweenOrderByAccessTimeAsc(
                        last7Start.atStartOfDay(),
                        last7End.plusDays(1).atStartOfDay().minusNanos(1)
                );

        Map<LocalDate, BigDecimal> incomeByDate = last7DaysPayments.stream()
                .collect(Collectors.groupingBy(
                        Payment::getPaymentDate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getAmount,
                                BigDecimal::add
                        )
                ));

        Map<LocalDate, Long> checkInsByDate = last7DaysLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getAccessTime().toLocalDate(),
                        Collectors.counting()
                ));

        List<String> last7DaysLabels = new ArrayList<>();
        List<BigDecimal> last7DaysIncome = new ArrayList<>();
        List<Long> last7DaysCheckIns = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        for (int i = 0; i < 7; i++) {
            LocalDate date = last7Start.plusDays(i);

            last7DaysLabels.add(date.format(formatter));
            last7DaysIncome.add(incomeByDate.getOrDefault(date, BigDecimal.ZERO));
            last7DaysCheckIns.add(checkInsByDate.getOrDefault(date, 0L));
        }

        return new DashboardStats(
                totalMembers,
                activeMembers,
                expiredMembers,
                suspendedMembers,
                maleMembers,
                femaleMembers,
                otherGenderMembers,
                totalPlans,
                activePlans,
                todayPayments,
                todayIncome,
                monthlyIncome,
                todayCheckIns,
                todayAccessGranted,
                todayAccessDenied,
                last7DaysLabels,
                last7DaysIncome,
                last7DaysCheckIns
        );
    }

    private BigDecimal sumPayments(List<Payment> payments) {
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}