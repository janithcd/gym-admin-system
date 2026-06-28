package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.dto.AccessCheckResult;
import lk.janith.gymadmin.service.AccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String showAccessLogs(Model model) {
        model.addAttribute("logs", accessControlService.getAllAccessLogs());
        return "access/logs";
    }
}