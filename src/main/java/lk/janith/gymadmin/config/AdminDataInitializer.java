package lk.janith.gymadmin.config;

import lk.janith.gymadmin.entity.AdminRole;
import lk.janith.gymadmin.entity.AdminUser;
import lk.janith.gymadmin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultUsername = "admin";

        if (!adminUserRepository.existsByUsername(defaultUsername)) {
            AdminUser admin = new AdminUser();
            admin.setFullName("Janith Dasanayaka");
            admin.setUsername(defaultUsername);
            admin.setEmail("admin@gym.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(AdminRole.SUPER_ADMIN);
            admin.setEnabled(true);

            adminUserRepository.save(admin);

            System.out.println("Default admin created: username=admin, password=admin123");
        }
    }
}