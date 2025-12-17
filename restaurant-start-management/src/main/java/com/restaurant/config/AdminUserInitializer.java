package com.restaurant.config;

import com.restaurant.ddd.domain.respository.UserRepository;
import com.restaurant.ddd.domain.service.AuthDomainService;
import com.restaurant.ddd.domain.model.User;
import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private static final UUID ADMIN_USER_ID = UUID.fromString("525c1149-0dce-4d0d-b136-97a1bb73db54");
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "abc@123";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthDomainService authDomainService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking for admin user with ID: {}", ADMIN_USER_ID);
        
        // Check if admin user exists
        boolean userExists = userRepository.findById(ADMIN_USER_ID).isPresent();
        
        if (userExists) {
            log.info("Admin user already exists with ID: {}", ADMIN_USER_ID);
            return;
        }

        // Check if username 'admin' already exists
        boolean usernameExists = userRepository.findByUsername(ADMIN_USERNAME).isPresent();
        if (usernameExists) {
            log.warn("Username '{}' already exists, skipping admin user creation", ADMIN_USERNAME);
            return;
        }

        // Create admin user
        log.info("Creating admin user with username: {}", ADMIN_USERNAME);
        
        String encodedPassword = authDomainService.encodePassword(ADMIN_PASSWORD);
        
        User adminUser = new User()
                .setId(ADMIN_USER_ID)
                .setUsername(ADMIN_USERNAME)
                .setEmail("admin@restaurant.com")
                .setPassword(encodedPassword)
                .setFullName("Administrator")
                .setStatus(DataStatus.ACTIVE);
        
        User savedUser = userRepository.save(adminUser);
        
        log.info("Admin user created successfully with ID: {}", savedUser.getId());
    }
}

