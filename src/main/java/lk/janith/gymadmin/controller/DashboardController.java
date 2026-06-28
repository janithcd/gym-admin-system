package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.repository.AdminUserRepository;
import lk.janith.gymadmin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AdminUserRepository adminUserRepository;

    @GetMapping("/")
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("stats", dashboardService.getDashboardStats());

        String username = authentication.getName();

        String adminName = adminUserRepository.findByUsername(username)
                .map(admin -> admin.getFullName())
                .orElse(username);

        model.addAttribute("adminName", adminName);

        return "dashboard";
    }
}