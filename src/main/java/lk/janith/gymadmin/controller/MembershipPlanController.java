package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.entity.MembershipPlan;
import lk.janith.gymadmin.entity.MembershipPlanStatus;
import lk.janith.gymadmin.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String savePlan(@ModelAttribute MembershipPlan plan,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            membershipPlanService.savePlan(plan);

            redirectAttributes.addFlashAttribute("successMessage", "Membership plan saved successfully.");
            return "redirect:/plans";

        } catch (RuntimeException e) {
            model.addAttribute("plan", plan);
            model.addAttribute("statuses", MembershipPlanStatus.values());
            model.addAttribute("errorMessage", e.getMessage());

            return "plans/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditPlanForm(@PathVariable Long id, Model model) {
        model.addAttribute("plan", membershipPlanService.getPlanById(id));
        model.addAttribute("statuses", MembershipPlanStatus.values());
        return "plans/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePlan(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            membershipPlanService.deletePlan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Membership plan deleted successfully.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete this plan. It may already be used in payment records.");
        }

        return "redirect:/plans";
    }
}