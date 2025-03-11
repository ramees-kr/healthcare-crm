package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    AdminRepository adminRepository;

    public Admin getAdminByEmail(String email, String password) {
        if (adminRepository.findByEmail(email) != null) {
            return adminRepository.findByEmail(email);
        }
        return null;
    }

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
}

//        adminService.createAdmin(Admin.builder()
//                .name("Gaurang")
//                .role("admin")
//                .email("gaurang@gmail.com")
//                .password("Gaurang@123")
//                .build()
//        );