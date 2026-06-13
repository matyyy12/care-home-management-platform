package com.bd2.seniorhomeapp.authentication.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bd2.seniorhomeapp.authentication.entity.AppRole;
import com.bd2.seniorhomeapp.authentication.entity.AuthUser;
import com.bd2.seniorhomeapp.authentication.repository.AuthUserRepository;

@Configuration
public class DemoUserSeeder {
    private static final String DEMO_PASSWORD = "Password123!";

    @Bean
    @Order(1)
    CommandLineRunner seedDemoUsers(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createIfMissing(authUserRepository, passwordEncoder, "director@seniorhouse.local", AppRole.DIRECTOR, null);
            createIfMissing(authUserRepository, passwordEncoder, "manager@seniorhouse.local", AppRole.MANAGER, 1L);
            createIfMissing(authUserRepository, passwordEncoder, "employee@seniorhouse.local", AppRole.EMPLOYEE, 1L);
            createIfMissing(authUserRepository, passwordEncoder, "medical@seniorhouse.local", AppRole.MEDICAL, 1L);
            createIfMissing(authUserRepository, passwordEncoder, "admin@seniorhouse.local", AppRole.ADMIN_TECH, null);
        };
    }

    private void createIfMissing(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            String email,
            AppRole role,
            Long assignedSeniorHomeId
    ) {
        if (authUserRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
        user.setRole(role);
        user.setAssignedSeniorHomeId(assignedSeniorHomeId);
        authUserRepository.save(user);
    }
}
