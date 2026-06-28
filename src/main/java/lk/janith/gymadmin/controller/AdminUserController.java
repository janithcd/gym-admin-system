package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.dto.AdminUserDTO;
import lk.janith.gymadmin.entity.AdminRole;
import lk.janith.gymadmin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String listAdmins(Model model) {
        model.addAttribute("admins", adminUserService.getAllAdmins());
        return "admins/list";
    }

    @GetMapping("/new")
    public String showAddAdminForm(Model model) {
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setEnabled(true);
        adminUserDTO.setRole(AdminRole.ADMIN);

        model.addAttribute("adminUser", adminUserDTO);
        model.addAttribute("roles", AdminRole.values());
        return "admins/form";
    }

    @PostMapping("/save")
    public String saveAdmin(@ModelAttribute("adminUser") AdminUserDTO adminUserDTO,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            adminUserService.saveAdmin(adminUserDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Admin user saved successfully.");
            return "redirect:/admins";

        } catch (RuntimeException e) {
            model.addAttribute("adminUser", adminUserDTO);
            model.addAttribute("roles", AdminRole.values());
            model.addAttribute("errorMessage", e.getMessage());

            return "admins/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditAdminForm(@PathVariable Long id, Model model) {
        model.addAttribute("adminUser", adminUserService.getAdminDtoById(id));
        model.addAttribute("roles", AdminRole.values());
        return "admins/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdmin(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteAdmin(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Admin user deleted successfully.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admins";
    }
}