package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.entity.PaymentStatus;
import lk.janith.gymadmin.repository.MemberRepository;
import lk.janith.gymadmin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipExpiryService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public void updateAllMemberStatuses() {
        List<Member> members = memberRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Member member : members) {

            // Do not change manually suspended members
            if (member.getStatus() == MemberStatus.SUSPENDED) {
                continue;
            }

            Optional<Payment> latestPaidPayment = paymentRepository
                    .findTopByMemberAndStatusOrderByExpiryDateDesc(member, PaymentStatus.PAID);

            if (latestPaidPayment.isEmpty()) {
                member.setStatus(MemberStatus.EXPIRED);
                memberRepository.save(member);
                continue;
            }

            Payment payment = latestPaidPayment.get();

            if (payment.getExpiryDate().isBefore(today)) {
                member.setStatus(MemberStatus.EXPIRED);
            } else {
                member.setStatus(MemberStatus.ACTIVE);
            }

            memberRepository.save(member);
        }
    }

    // Automatically runs every 1 hour
    @Scheduled(fixedRate = 3600000)
    public void scheduledMembershipExpiryUpdate() {
        updateAllMemberStatuses();
        System.out.println("Membership expiry status update completed.");
    }
}