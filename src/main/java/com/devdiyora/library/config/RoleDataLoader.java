package com.devdiyora.library.config;

import com.devdiyora.library.entity.Role;
import com.devdiyora.library.enums.RoleType;
import com.devdiyora.library.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class RoleDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleDataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    public void run(String... args) {

        createRoleIfNotExists(RoleType.ADMIN.name());
        createRoleIfNotExists(RoleType.LIBRARIAN.name());
        createRoleIfNotExists(RoleType.MEMBER.name());

    }

    private void createRoleIfNotExists(String roleName) {

        if (roleRepository.findByName(roleName).isEmpty()) {

            Role role = new Role();
            role.setName(roleName);

            roleRepository.save(role);
        }
    }
}