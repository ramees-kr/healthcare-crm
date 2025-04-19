package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Admin createAdmin(Admin admin) {
        // Encode the password before saving
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

}