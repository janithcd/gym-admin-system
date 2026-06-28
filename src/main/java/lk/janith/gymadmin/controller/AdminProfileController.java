package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.dto.ChangePasswordDTO;
import lk.janith.gymadmin.entity.AdminUser;
import lk.janith.gymadmin.service.AdminProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        AdminUser adminUser = adminProfileService.getLoggedAdmin(authentication.getName());

        model.addAttribute("adminUser", adminUser);

        return "admins/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());

        return "admins/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDTO changePasswordDTO,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String errorMessage = adminProfileService.changePassword(
                authentication.getName(),
                changePasswordDTO
        );

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/profile/change-password";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Password changed successfully. Please use your new password next time."
        );

        return "redirect:/profile";
    }
}