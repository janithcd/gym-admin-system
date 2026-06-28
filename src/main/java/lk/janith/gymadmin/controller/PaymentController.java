package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.entity.Payment;
import lk.janith.gymadmin.entity.PaymentMethod;
import lk.janith.gymadmin.entity.PaymentStatus;
import lk.janith.gymadmin.repository.AdminUserRepository;
import lk.janith.gymadmin.service.MemberService;
import lk.janith.gymadmin.service.MembershipPlanService;
import lk.janith.gymadmin.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberService memberService;
    private final MembershipPlanService membershipPlanService;
    private final AdminUserRepository adminUserRepository;

    @GetMapping
    public String listPayments(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String paymentMethod,
                               @RequestParam(required = false) String status,

                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                               LocalDate startDate,

                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                               LocalDate endDate,

                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,

                               Model model) {

        Page<Payment> paymentPage = paymentService.searchPayments(
                keyword,
                paymentMethod,
                status,
                startDate,
                endDate,
                page,
                size
        );

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("paymentPage", paymentPage);

        model.addAttribute("currentPage", paymentPage.getNumber());
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("totalItems", paymentPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());

        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedPaymentMethod", paymentMethod);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);

        return "payments/list";
    }

    @GetMapping("/new")
    public String showPaymentForm(Model model) {
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("plans", membershipPlanService.getActivePlans());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("today", LocalDate.now());
        return "payments/form";
    }

    @PostMapping("/save")
    public String savePayment(@RequestParam Long memberId,
                              @RequestParam Long planId,
                              @RequestParam(required = false) BigDecimal amount,
                              @RequestParam PaymentMethod paymentMethod,
                              @RequestParam(required = false) LocalDate paymentDate,
                              @RequestParam(required = false) LocalDate startDate,
                              @RequestParam(required = false) String note) {

        Payment savedPayment = paymentService.recordPayment(
                memberId,
                planId,
                amount,
                paymentMethod,
                paymentDate,
                startDate,
                note
        );

        return "redirect:/payments/receipt/" + savedPayment.getId();
    }

    @GetMapping("/receipt/{id}")
    public String showReceipt(@PathVariable Long id,
                              Model model,
                              Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        String username = authentication.getName();

        String printedBy = adminUserRepository.findByUsername(username)
                .map(admin -> admin.getFullName())
                .orElse(username);

        String receiptNumber = String.format("GYM-REC-%05d", payment.getId());

        model.addAttribute("payment", payment);
        model.addAttribute("receiptNumber", receiptNumber);
        model.addAttribute("printedBy", printedBy);
        model.addAttribute("printedDate", LocalDate.now());

        return "payments/receipt";
    }

    @GetMapping("/cancel/{id}")
    public String cancelPayment(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            paymentService.cancelPayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment cancelled successfully.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not cancel payment.");
        }

        return "redirect:/payments";
    }

    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment deleted successfully.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete payment.");
        }

        return "redirect:/payments";
    }
}