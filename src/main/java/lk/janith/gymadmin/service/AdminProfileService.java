package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.ChangePasswordDTO;
import lk.janith.gymadmin.entity.AdminUser;
import lk.janith.gymadmin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUser getLoggedAdmin(String username) {
        return adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }

    public String changePassword(String username, ChangePasswordDTO dto) {
        AdminUser adminUser = getLoggedAdmin(username);

        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
            return "Current password is required.";
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            return "New password is required.";
        }

        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            return "Confirm password is required.";
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), adminUser.getPassword())) {
            return "Current password is incorrect.";
        }

        if (dto.getNewPassword().length() < 6) {
            return "New password must be at least 6 characters.";
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return "New password and confirm password do not match.";
        }

        if (passwordEncoder.matches(dto.getNewPassword(), adminUser.getPassword())) {
            return "New password cannot be the same as the current password.";
        }

        adminUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        adminUserRepository.save(adminUser);

        return null;
    }
}