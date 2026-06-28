package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.MembershipPlan;
import lk.janith.gymadmin.entity.MembershipPlanStatus;
import lk.janith.gymadmin.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public List<MembershipPlan> getAllPlans() {
        return membershipPlanRepository.findAll();
    }

    public List<MembershipPlan> getActivePlans() {
        return membershipPlanRepository.findByStatus(MembershipPlanStatus.ACTIVE);
    }

    public MembershipPlan getPlanById(Long id) {
        return membershipPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership plan not found"));
    }

    public MembershipPlan savePlan(MembershipPlan plan) {
        normalizePlan(plan);

        if (plan.getId() == null) {
            if (plan.getStatus() == null) {
                plan.setStatus(MembershipPlanStatus.ACTIVE);
            }

            return membershipPlanRepository.save(plan);
        }

        MembershipPlan existingPlan = getPlanById(plan.getId());

        existingPlan.setPlanName(plan.getPlanName());
        existingPlan.setDurationDays(plan.getDurationDays());
        existingPlan.setPrice(plan.getPrice());
        existingPlan.setDescription(plan.getDescription());
        existingPlan.setStatus(plan.getStatus());

        return membershipPlanRepository.save(existingPlan);
    }

    public void deletePlan(Long id) {
        membershipPlanRepository.deleteById(id);
    }

    private void normalizePlan(MembershipPlan plan) {
        if (plan.getPlanName() != null) {
            plan.setPlanName(plan.getPlanName().trim());
        }

        if (plan.getDescription() != null && plan.getDescription().trim().isEmpty()) {
            plan.setDescription(null);
        }
    }
}