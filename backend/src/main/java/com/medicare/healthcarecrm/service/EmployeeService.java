package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public long getEmployeeCount() {
        return employeeRepository.count();
    }

    @Transactional
    public String createEmployee(Employee employee) {
        try {
            // Check if email already exists before saving (optional but good practice)
            if (employeeRepository.findByEmail(employee.getEmail()) != null) {
                return "Error: Email already exists.";
            }
            // Encode the password before saving
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employeeRepository.save(employee);
            return null;
        } catch (Exception e) {
            // Log exception
            return "Error creating employee: " + e.getMessage();
        }
    }

    @Transactional
    public String updateEmployee(Employee employee, Long id) {
        try {
            Optional<Employee> existingEmployeeOpt = employeeRepository.findById(id);
            if (existingEmployeeOpt.isEmpty()) {
                return "Employee not found with ID: " + id;
            }
            Employee oldEmployee = existingEmployeeOpt.get();

            // Check if email is being changed and if the new email already exists for another user
            if (!oldEmployee.getEmail().equals(employee.getEmail()) &&
                    employeeRepository.findByEmail(employee.getEmail()) != null) {
                return "Error: New email already exists.";
            }

            oldEmployee.setName(employee.getName());
            oldEmployee.setRole(employee.getRole());
            oldEmployee.setEmail(employee.getEmail());

            // Only update password if a new one is provided and encode it
            // Ensure the password field isn't accidentally cleared if left blank in the form
            if (employee.getPassword() != null && !employee.getPassword().trim().isEmpty()) {
                oldEmployee.setPassword(passwordEncoder.encode(employee.getPassword().trim()));
            } // Else: keep the old password

            employeeRepository.save(oldEmployee);
            return null;
        } catch (Exception e) {
            // Log exception
            return "Error updating employee: " + e.getMessage();
        }
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Transactional
    public String deleteEmployee(Long id) {
        try {
            if (!employeeRepository.existsById(id)) {
                return "Employee not found with ID: " + id;
            }
            // Add logic here if deleting an employee requires reassigning tasks, etc.
            employeeRepository.deleteById(id);
            return null;
        } catch (Exception e) {
            // Log exception
            return "Error deleting employee: " + e.getMessage();
        }
    }
}