package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.dto.AccessCheckResult;
import lk.janith.gymadmin.entity.AccessLog;
import lk.janith.gymadmin.entity.AccessStatus;
import lk.janith.gymadmin.service.AccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/access")
public class AccessControlController {

    private final AccessControlService accessControlService;

    @GetMapping("/scan")
    public String showScanPage() {
        return "access/scan";
    }

    @PostMapping("/check")
    public String checkAccess(@RequestParam String memberCode, Model model) {
        AccessCheckResult result = accessControlService.checkAccess(memberCode);
        model.addAttribute("result", result);
        return "access/scan";
    }

    @GetMapping("/logs")
    public String showAccessLogs(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String accessStatus,

                                 @RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 LocalDate startDate,

                                 @RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 LocalDate endDate,

                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,

                                 Model model) {

        Page<AccessLog> accessLogPage = accessControlService.searchAccessLogs(
                keyword,
                accessStatus,
                startDate,
                endDate,
                page,
                size
        );

        model.addAttribute("logs", accessLogPage.getContent());
        model.addAttribute("accessLogPage", accessLogPage);

        model.addAttribute("currentPage", accessLogPage.getNumber());
        model.addAttribute("totalPages", accessLogPage.getTotalPages());
        model.addAttribute("totalItems", accessLogPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("accessStatuses", AccessStatus.values());

        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedAccessStatus", accessStatus);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);

        return "access/logs";
    }
}