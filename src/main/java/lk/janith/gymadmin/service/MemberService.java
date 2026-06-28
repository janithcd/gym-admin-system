package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

    public Member saveMember(Member member) {
        normalizeMember(member);

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

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
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
    public Member getMemberByCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
    public boolean existsByMemberCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode).isPresent();
    }
}