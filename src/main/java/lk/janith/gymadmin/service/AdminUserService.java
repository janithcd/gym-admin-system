package lk.janith.gymadmin.service;

import lk.janith.gymadmin.dto.AdminUserDTO;
import lk.janith.gymadmin.entity.AdminRole;
import lk.janith.gymadmin.entity.AdminUser;
import lk.janith.gymadmin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminUser> getAllAdmins() {
        return adminUserRepository.findAll();
    }

    public AdminUser getAdminById(Long id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }

    public AdminUserDTO getAdminDtoById(Long id) {
        AdminUser admin = getAdminById(id);

        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(admin.getId());
        dto.setFullName(admin.getFullName());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        dto.setEnabled(admin.isEnabled());

        return dto;
    }

    public void saveAdmin(AdminUserDTO dto) {
        normalize(dto);

        if (dto.getId() == null) {
            createAdmin(dto);
        } else {
            updateAdmin(dto);
        }
    }

    private void createAdmin(AdminUserDTO dto) {
        validateAdmin(dto, true);

        AdminUser admin = new AdminUser();
        admin.setFullName(dto.getFullName());
        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(dto.getRole() == null ? AdminRole.ADMIN : dto.getRole());
        admin.setEnabled(dto.isEnabled());

        adminUserRepository.save(admin);
    }

    private void updateAdmin(AdminUserDTO dto) {
        validateAdmin(dto, false);

        AdminUser existingAdmin = getAdminById(dto.getId());

        existingAdmin.setFullName(dto.getFullName());
        existingAdmin.setEmail(dto.getEmail());
        existingAdmin.setRole(dto.getRole());
        existingAdmin.setEnabled(dto.isEnabled());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters.");
            }

            existingAdmin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        adminUserRepository.save(existingAdmin);
    }

    private void validateAdmin(AdminUserDTO dto, boolean creatingNewAdmin) {
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new RuntimeException("Full name is required.");
        }

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new RuntimeException("Username is required.");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required.");
        }

        if (dto.getRole() == null) {
            throw new RuntimeException("Admin role is required.");
        }

        if (creatingNewAdmin && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            throw new RuntimeException("Password is required for new admin.");
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank() && dto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters.");
        }

        adminUserRepository.findByUsername(dto.getUsername())
                .ifPresent(existingAdmin -> {
                    if (dto.getId() == null || !existingAdmin.getId().equals(dto.getId())) {
                        throw new RuntimeException("Username already exists.");
                    }
                });

        adminUserRepository.findByEmail(dto.getEmail())
                .ifPresent(existingAdmin -> {
                    if (dto.getId() == null || !existingAdmin.getId().equals(dto.getId())) {
                        throw new RuntimeException("Email already exists.");
                    }
                });
    }

    public void deleteAdmin(Long id, String currentUsername) {
        AdminUser admin = getAdminById(id);

        if (admin.getUsername().equals(currentUsername)) {
            throw new RuntimeException("You cannot delete your own admin account");
        }

        adminUserRepository.deleteById(id);
    }

    private void normalize(AdminUserDTO dto) {
        if (dto.getFullName() != null) {
            dto.setFullName(dto.getFullName().trim());
        }

        if (dto.getUsername() != null) {
            dto.setUsername(dto.getUsername().trim());
        }

        if (dto.getEmail() != null) {
            dto.setEmail(dto.getEmail().trim());
        }
    }
}