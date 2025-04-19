package com.medicare.healthcarecrm.config;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.repository.AdminRepository;
import com.medicare.healthcarecrm.service.AdminService; // Make sure AdminService is available
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Make this a Spring-managed bean
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService; // Inject AdminService to use its createAdmin method

    @Override
    public void run(String... args) throws Exception {
        // Check if any admin users exist
        if (adminRepository.count() == 0) {
            log.info("No admin users found. Creating default admin user...");

            // Define default admin credentials (Consider externalizing these later)
            String adminEmail = "admin@example.com";
            String adminPassword = "password123"; // Choose a secure default password
            String adminName = "Default Admin";
            String adminRole = "ADMIN"; // Role used in UserDetailsServiceImpl

            // Create the admin object
            Admin defaultAdmin = Admin.builder()
                    .name(adminName)
                    .role(adminRole) // Ensure this matches role logic if used elsewhere
                    .email(adminEmail)
                    .password(adminPassword) // Pass the raw password here
                    .build();

            // Use AdminService to create the admin (it handles password encoding)
            try {
                adminService.createAdmin(defaultAdmin); // createAdmin hashes the password
                log.info("Default admin user created successfully with email: {}", adminEmail);
                log.warn("Default admin password is: {}", adminPassword); // Log the password clearly for initial use
            } catch (Exception e) {
                log.error("Failed to create default admin user: {}", e.getMessage());
            }

        } else {
            log.info("Admin users already exist. Skipping default admin creation.");
        }
    }
}