package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.entity.PaymentStatus;
import java.util.List;
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
    public List<Payment> getPaymentsByMember(Member member) {
        return paymentRepository.findByMemberOrderByExpiryDateDesc(member);
    }

    public Payment getLatestPaidPayment(Member member) {
        return paymentRepository
                .findTopByMemberAndStatusOrderByExpiryDateDesc(member, PaymentStatus.PAID)
                .orElse(null);
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
}