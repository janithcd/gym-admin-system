package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.entity.MembershipPlan;
import lk.janith.gymadmin.entity.MembershipPlanStatus;
import lk.janith.gymadmin.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plans")
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @GetMapping
    public String listPlans(Model model) {
        model.addAttribute("plans", membershipPlanService.getAllPlans());
        return "plans/list";
    }

    @GetMapping("/new")
    public String showAddPlanForm(Model model) {
        model.addAttribute("plan", new MembershipPlan());
        model.addAttribute("statuses", MembershipPlanStatus.values());
        return "plans/form";
    }

    @PostMapping("/save")
    public String savePlan(@ModelAttribute MembershipPlan plan) {
        membershipPlanService.savePlan(plan);
        return "redirect:/plans";
    }

    @GetMapping("/edit/{id}")
    public String showEditPlanForm(@PathVariable Long id, Model model) {
        model.addAttribute("plan", membershipPlanService.getPlanById(id));
        model.addAttribute("statuses", MembershipPlanStatus.values());
        return "plans/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePlan(@PathVariable Long id) {
        membershipPlanService.deletePlan(id);
        return "redirect:/plans";
    }
}