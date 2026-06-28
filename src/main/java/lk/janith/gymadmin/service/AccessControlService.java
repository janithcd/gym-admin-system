package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.AccessCheckResult;
import lk.janith.gymadmin.entity.*;
import lk.janith.gymadmin.repository.AccessLogRepository;
import lk.janith.gymadmin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Page<AccessLog> searchAccessLogs(String keyword,
                                            String accessStatusText,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            int page,
                                            int size) {

        String cleanKeyword = normalizeText(keyword);

        AccessStatus accessStatus = null;

        if (accessStatusText != null && !accessStatusText.isBlank()) {
            accessStatus = AccessStatus.valueOf(accessStatusText);
        }

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        return accessLogRepository.searchAccessLogs(
                cleanKeyword,
                accessStatus,
                startDateTime,
                endDateTime,
                PageRequest.of(page, size)
        );
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

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}