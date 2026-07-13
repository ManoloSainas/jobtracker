package com.manolo.jobtracker.config;

import com.manolo.jobtracker.enums.Role;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.debug("Admin già presente: {}", adminEmail);
            return;
        }

        User admin = new User();

        admin.setEmail(adminEmail);
        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        log.info("Admin iniziale creato: {}", adminEmail);
    }
}