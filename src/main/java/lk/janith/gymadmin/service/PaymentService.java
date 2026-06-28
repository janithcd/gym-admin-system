package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberService memberService;
    private final MembershipPlanService membershipPlanService;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Page<Payment> searchPayments(String keyword,
                                        String paymentMethodText,
                                        String statusText,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        int page,
                                        int size) {

        String cleanKeyword = normalizeText(keyword);

        PaymentMethod paymentMethod = null;
        PaymentStatus status = null;

        if (paymentMethodText != null && !paymentMethodText.isBlank()) {
            paymentMethod = PaymentMethod.valueOf(paymentMethodText);
        }

        if (statusText != null && !statusText.isBlank()) {
            status = PaymentStatus.valueOf(statusText);
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        return paymentRepository.searchPayments(
                cleanKeyword,
                paymentMethod,
                status,
                startDate,
                endDate,
                PageRequest.of(page, size)
        );
    }

    public Payment recordPayment(Long memberId,
                                 Long planId,
                                 BigDecimal amount,
                                 PaymentMethod paymentMethod,
                                 LocalDate paymentDate,
                                 LocalDate startDate,
                                 String note) {

        Member member = memberService.getMemberById(memberId);
        MembershipPlan plan = membershipPlanService.getPlanById(planId);

        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }

        if (startDate == null) {
            startDate = paymentDate;
        }

        if (amount == null) {
            amount = plan.getPrice();
        }

        LocalDate expiryDate = startDate.plusDays(plan.getDurationDays());

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setMembershipPlan(plan);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(paymentDate);
        payment.setStartDate(startDate);
        payment.setExpiryDate(expiryDate);
        payment.setStatus(PaymentStatus.PAID);
        payment.setNote(note);

        member.setStatus(MemberStatus.ACTIVE);
        memberService.saveMember(member);

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void cancelPayment(Long id) {
        Payment payment = getPaymentById(id);
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public boolean hasActivePayment(Member member) {
        return paymentRepository
                .findTopByMemberAndStatusOrderByExpiryDateDesc(member, PaymentStatus.PAID)
                .map(payment -> !payment.getExpiryDate().isBefore(LocalDate.now()))
                .orElse(false);
    }

    public List<Payment> getPaymentsByMember(Member member) {
        return paymentRepository.findByMemberOrderByExpiryDateDesc(member);
    }

    public Payment getLatestPaidPayment(Member member) {
        return paymentRepository
                .findTopByMemberAndStatusOrderByExpiryDateDesc(member, PaymentStatus.PAID)
                .orElse(null);
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}