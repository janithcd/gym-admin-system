package lk.janith.gymadmin.repository;

import lk.janith.gymadmin.entity.MembershipPlan;
import lk.janith.gymadmin.entity.MembershipPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    List<MembershipPlan> findByStatus(MembershipPlanStatus status);

    long countByStatus(MembershipPlanStatus status);

    Optional<MembershipPlan> findByPlanNameIgnoreCase(String planName);
}