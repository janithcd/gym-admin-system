package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Page<Member> searchMembers(String keyword,
                                      String statusText,
                                      String gender,
                                      int page,
                                      int size) {

        String cleanKeyword = normalizeText(keyword);
        String cleanGender = normalizeText(gender);

        MemberStatus status = null;

        if (statusText != null && !statusText.isBlank()) {
            status = MemberStatus.valueOf(statusText);
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        return memberRepository.searchMembers(
                cleanKeyword,
                status,
                cleanGender,
                PageRequest.of(page, size)
        );
    }

    public Member saveMember(Member member) {
        normalizeMember(member);
        validateMember(member);

        if (member.getId() == null) {
            if (member.getJoinedDate() == null) {
                member.setJoinedDate(LocalDate.now());
            }

            if (member.getStatus() == null) {
                member.setStatus(MemberStatus.ACTIVE);
            }

            return memberRepository.save(member);
        }

        Member existingMember = getMemberById(member.getId());

        existingMember.setMemberCode(member.getMemberCode());
        existingMember.setFullName(member.getFullName());
        existingMember.setPhone(member.getPhone());
        existingMember.setNic(member.getNic());
        existingMember.setGender(member.getGender());
        existingMember.setDateOfBirth(member.getDateOfBirth());
        existingMember.setHeightCm(member.getHeightCm());
        existingMember.setWeightKg(member.getWeightKg());
        existingMember.setAddress(member.getAddress());
        existingMember.setEmergencyContactName(member.getEmergencyContactName());
        existingMember.setEmergencyContactPhone(member.getEmergencyContactPhone());
        existingMember.setMedicalNotes(member.getMedicalNotes());
        existingMember.setStatus(member.getStatus());
        existingMember.setDeviceUserId(member.getDeviceUserId());

        return memberRepository.save(existingMember);
    }

    private void validateMember(Member member) {
        if (member.getMemberCode() == null || member.getMemberCode().isBlank()) {
            throw new RuntimeException("Member code is required.");
        }

        if (member.getFullName() == null || member.getFullName().isBlank()) {
            throw new RuntimeException("Full name is required.");
        }

        if (member.getPhone() == null || member.getPhone().isBlank()) {
            throw new RuntimeException("Phone number is required.");
        }

        memberRepository.findByMemberCode(member.getMemberCode())
                .ifPresent(existingMember -> {
                    if (member.getId() == null || !existingMember.getId().equals(member.getId())) {
                        throw new RuntimeException("Member code already exists.");
                    }
                });

        if (member.getNic() != null && !member.getNic().isBlank()) {
            memberRepository.findByNic(member.getNic())
                    .ifPresent(existingMember -> {
                        if (member.getId() == null || !existingMember.getId().equals(member.getId())) {
                            throw new RuntimeException("NIC already exists.");
                        }
                    });
        }
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member getMemberByCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public boolean existsByMemberCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode).isPresent();
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    private void normalizeMember(Member member) {
        if (member.getNic() != null && member.getNic().trim().isEmpty()) {
            member.setNic(null);
        }

        if (member.getDeviceUserId() != null && member.getDeviceUserId().trim().isEmpty()) {
            member.setDeviceUserId(null);
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}