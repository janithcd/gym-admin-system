package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberCode(String memberCode);

    Optional<Member> findByNic(String nic);

    long countByStatus(MemberStatus status);

    long countByGenderIgnoreCase(String gender);

    List<Member> findByStatus(MemberStatus status);

    @Query("""
            SELECT m FROM Member m
            WHERE
                (
                    :keyword IS NULL OR
                    LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(m.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(COALESCE(m.nic, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :status IS NULL OR m.status = :status
                )
            AND
                (
                    :gender IS NULL OR LOWER(m.gender) = LOWER(:gender)
                )
            ORDER BY m.id DESC
            """)
    Page<Member> searchMembers(
            @Param("keyword") String keyword,
            @Param("status") MemberStatus status,
            @Param("gender") String gender,
            Pageable pageable
    );
}