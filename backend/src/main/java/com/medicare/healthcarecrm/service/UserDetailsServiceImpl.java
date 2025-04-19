package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.repository.AdminRepository;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try finding user as Admin first
        Admin admin = adminRepository.findByEmail(username);
        if (admin != null) {
            // Prefix role with "ROLE_" as required by Spring Security
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new User(admin.getEmail(), admin.getPassword(), authorities);
        }

        // If not found as Admin, try finding as Employee
        Employee employee = employeeRepository.findByEmail(username);
        if (employee != null) {
            // Prefix role with "ROLE_"
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")); // Assuming a standard employee role
            return new User(employee.getEmail(), employee.getPassword(), authorities);
        }

        // If user not found in either repository
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}