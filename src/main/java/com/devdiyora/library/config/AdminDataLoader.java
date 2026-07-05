package com.devdiyora.library.config;

import com.devdiyora.library.entity.Role;
import com.devdiyora.library.entity.User;
import com.devdiyora.library.enums.RoleType;
import com.devdiyora.library.repository.RoleRepository;
import com.devdiyora.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@gmail.com").isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN.name())
                .orElseThrow(() ->
                        new RuntimeException("Admin role not found."));

        User admin = new User();

        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhoneNumber("9999999999");
        admin.setEnabled(true);

        admin.getRoles().add(adminRole);

        userRepository.save(admin);
    }
}