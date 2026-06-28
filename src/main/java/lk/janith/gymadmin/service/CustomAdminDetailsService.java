package lk.janith.gymadmin.service;

import lk.janith.gymadmin.entity.AdminUser;
import lk.janith.gymadmin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAdminDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .roles(adminUser.getRole().name())
                .disabled(!adminUser.isEnabled())
                .build();
    }
}