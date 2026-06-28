package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.entity.Member;
import lk.janith.gymadmin.entity.MemberStatus;
import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.service.AccessControlService;
import lk.janith.gymadmin.service.MemberService;
import lk.janith.gymadmin.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PaymentService paymentService;
    private final AccessControlService accessControlService;

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.getAllMembers());
        return "members/list";
    }

    @GetMapping("/new")
    public String showAddMemberForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("statuses", MemberStatus.values());
        return "members/form";
    }

    @PostMapping("/save")
    public String saveMember(@ModelAttribute Member member) {
        memberService.saveMember(member);
        return "redirect:/members";
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

    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "redirect:/members";
    }
}