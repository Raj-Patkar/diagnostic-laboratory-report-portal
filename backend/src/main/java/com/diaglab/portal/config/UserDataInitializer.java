package com.diaglab.portal.config;

import com.diaglab.portal.entity.User;
import com.diaglab.portal.entity.UserRole;
import com.diaglab.portal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(UserDataInitializer.class);

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Environment environment
    ) {
        return args -> {
            seedUser(
                    userRepository, passwordEncoder,
                    "admin", "APP_ADMIN_PASSWORD",
                    UserRole.ADMIN, environment
            );

            seedUser(
                    userRepository, passwordEncoder,
                    "technician", "APP_TECHNICIAN_PASSWORD",
                    UserRole.TECHNICIAN, environment
            );

            seedUser(
                    userRepository, passwordEncoder,
                    "doctor", "APP_DOCTOR_PASSWORD",
                    UserRole.DOCTOR, environment
            );
        };
    }

    private void seedUser(
            UserRepository repository,
            PasswordEncoder encoder,
            String username,
            String passwordVariable,
            UserRole role,
            Environment environment
    ) {
        if (repository.existsByUsernameIgnoreCase(username)) {
            return;
        }

        String password = environment.getProperty(passwordVariable);

        if (password == null || password.isBlank()) {
            log.warn(
                    "Skipping demo user '{}': environment variable {} is not set",
                    username, passwordVariable
            );
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);

        repository.save(user);
        log.info("Created demo user '{}' with role {}", username, role);
    }
}