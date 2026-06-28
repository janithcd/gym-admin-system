package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberCode(String memberCode);

    long countByStatus(MemberStatus status);

    long countByGenderIgnoreCase(String gender);
}