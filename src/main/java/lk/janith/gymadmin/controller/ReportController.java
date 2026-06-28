package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.service.MembershipExpiryService;
import lk.janith.gymadmin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final MembershipExpiryService membershipExpiryService;

    @GetMapping
    public String reportsIndex() {
        return "reports/index";
    }

    @GetMapping("/daily-income")
    public String dailyIncomeReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Model model
    ) {
        if (date == null) {
            date = LocalDate.now();
        }

        model.addAttribute("selectedDate", date);
        model.addAttribute("payments", reportService.getPaidPaymentsBetween(date, date));
        model.addAttribute("summary", reportService.getPaymentSummary(date, date));

        return "reports/daily-income";
    }

    @GetMapping("/monthly-income")
    public String monthlyIncomeReport(
            @RequestParam(required = false) String month,
            Model model
    ) {
        YearMonth selectedMonth;

        if (month == null || month.isBlank()) {
            selectedMonth = YearMonth.now();
        } else {
            selectedMonth = YearMonth.parse(month);
        }

        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("payments", reportService.getPaidPaymentsBetween(startDate, endDate));
        model.addAttribute("summary", reportService.getPaymentSummary(startDate, endDate));

        return "reports/monthly-income";
    }

    @GetMapping("/expired-members")
    public String expiredMembersReport(Model model) {
        membershipExpiryService.updateAllMemberStatuses();

        model.addAttribute("members", reportService.getExpiredMembers());

        return "reports/expired-members";
    }

    @GetMapping("/access-report")
    public String accessReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Model model
    ) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("logs", reportService.getAccessLogsBetween(startDate, endDate));
        model.addAttribute("summary", reportService.getAccessSummary(startDate, endDate));

        return "reports/access-report";
    }
}