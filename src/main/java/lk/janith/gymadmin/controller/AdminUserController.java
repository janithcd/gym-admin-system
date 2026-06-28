package lk.janith.gymadmin.controller;

import lk.janith.gymadmin.dto.AdminUserDTO;
import lk.janith.gymadmin.entity.AdminRole;
import lk.janith.gymadmin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String saveAdmin(@ModelAttribute("adminUser") AdminUserDTO adminUserDTO) {
        adminUserService.saveAdmin(adminUserDTO);
        return "redirect:/admins";
    }

    @GetMapping("/edit/{id}")
    public String showEditAdminForm(@PathVariable Long id, Model model) {
        model.addAttribute("adminUser", adminUserService.getAdminDtoById(id));
        model.addAttribute("roles", AdminRole.values());
        return "admins/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdmin(@PathVariable Long id, Authentication authentication) {
        adminUserService.deleteAdmin(id, authentication.getName());
        return "redirect:/admins";
    }
}