package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.service.AccessControlService;
import lk.janith.gymadmin.service.MemberService;
import lk.janith.gymadmin.service.MembershipExpiryService;
import lk.janith.gymadmin.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PaymentService paymentService;
    private final AccessControlService accessControlService;
    private final MembershipExpiryService membershipExpiryService;

    @GetMapping
    public String listMembers(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String gender,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        membershipExpiryService.updateAllMemberStatuses();

        Page<Member> memberPage = memberService.searchMembers(
                keyword,
                status,
                gender,
                page,
                size
        );

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("memberPage", memberPage);

        model.addAttribute("currentPage", memberPage.getNumber());
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("totalItems", memberPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("statuses", MemberStatus.values());

        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedGender", gender);

        return "members/list";
    }

    @GetMapping("/new")
    public String showAddMemberForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("statuses", MemberStatus.values());
        return "members/form";
    }

    @PostMapping("/save")
    public String saveMember(@ModelAttribute Member member,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            memberService.saveMember(member);

            redirectAttributes.addFlashAttribute("successMessage", "Member saved successfully.");
            return "redirect:/members";

        } catch (RuntimeException e) {
            model.addAttribute("member", member);
            model.addAttribute("statuses", MemberStatus.values());
            model.addAttribute("errorMessage", e.getMessage());

            return "members/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditMemberForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.getMemberById(id));
        model.addAttribute("statuses", MemberStatus.values());
        return "members/form";
    }

    @GetMapping("/view/{id}")
    public String viewMemberProfile(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id);
        Payment latestPayment = paymentService.getLatestPaidPayment(member);

        boolean hasActivePayment = paymentService.hasActivePayment(member);

        model.addAttribute("member", member);
        model.addAttribute("latestPayment", latestPayment);
        model.addAttribute("hasActivePayment", hasActivePayment);
        model.addAttribute("payments", paymentService.getPaymentsByMember(member));
        model.addAttribute("accessLogs", accessControlService.getAccessLogsByMember(member));
        model.addAttribute("today", LocalDate.now());

        return "members/profile";
    }

    @GetMapping("/refresh-statuses")
    public String refreshMemberStatuses() {
        membershipExpiryService.updateAllMemberStatuses();
        return "redirect:/members";
    }

    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete this member. This member may have payments or access logs.");
        }

        return "redirect:/members";
    }
}