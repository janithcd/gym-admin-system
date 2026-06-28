package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.AccessCheckResult;
import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lk.janith.gymadmin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final MemberRepository memberRepository;
    private final AccessLogRepository accessLogRepository;
    private final PaymentService paymentService;
    private final MemberService memberService;

    public AccessCheckResult checkAccess(String memberCode) {
        String cleanMemberCode = memberCode == null ? "" : memberCode.trim();

        if (cleanMemberCode.isEmpty()) {
            return saveAndReturnResult(
                    null,
                    cleanMemberCode,
                    AccessStatus.DENIED,
                    "Member code is required"
            );
        }

        Optional<Member> optionalMember = memberRepository.findByMemberCode(cleanMemberCode);

        if (optionalMember.isEmpty()) {
            return saveAndReturnResult(
                    null,
                    cleanMemberCode,
                    AccessStatus.DENIED,
                    "Member not found"
            );
        }

        Member member = optionalMember.get();

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            return saveAndReturnResult(
                    member,
                    cleanMemberCode,
                    AccessStatus.DENIED,
                    "Member account is suspended"
            );
        }

        boolean hasActivePayment = paymentService.hasActivePayment(member);

        if (!hasActivePayment) {
            member.setStatus(MemberStatus.EXPIRED);
            memberService.saveMember(member);

            return saveAndReturnResult(
                    member,
                    cleanMemberCode,
                    AccessStatus.DENIED,
                    "Payment expired or no active payment found"
            );
        }

        member.setStatus(MemberStatus.ACTIVE);
        memberService.saveMember(member);

        return saveAndReturnResult(
                member,
                cleanMemberCode,
                AccessStatus.GRANTED,
                "Access granted. Membership is active"
        );
    }

    public List<AccessLog> getAllAccessLogs() {
        return accessLogRepository.findAllByOrderByAccessTimeDesc();
    }

    public List<AccessLog> getAccessLogsByMember(Member member) {
        return accessLogRepository.findByMemberOrderByAccessTimeDesc(member);
    }

    private AccessCheckResult saveAndReturnResult(Member member,
                                                  String enteredMemberCode,
                                                  AccessStatus accessStatus,
                                                  String reason) {

        AccessLog accessLog = new AccessLog();
        accessLog.setMember(member);
        accessLog.setEnteredMemberCode(enteredMemberCode);
        accessLog.setAccessStatus(accessStatus);
        accessLog.setReason(reason);
        accessLog.setAccessTime(LocalDateTime.now());

        accessLogRepository.save(accessLog);

        return new AccessCheckResult(
                member,
                enteredMemberCode,
                accessStatus,
                reason,
                accessLog.getAccessTime()
        );
    }
}