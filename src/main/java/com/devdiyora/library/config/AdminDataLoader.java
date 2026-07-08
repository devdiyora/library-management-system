package com.devdiyora.library.config;

import com.devdiyora.library.entity.Role;
import com.devdiyora.library.entity.User;
import com.devdiyora.library.enums.RoleType;
import com.devdiyora.library.repository.RoleRepository;
import com.devdiyora.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN.name())
                .orElseThrow(() ->
                        new RuntimeException("Admin role not found."));

        User admin = new User();

        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setPhoneNumber("9999999999");
        admin.setEnabled(true);

        admin.getRoles().add(adminRole);

        userRepository.save(admin);
    }
}