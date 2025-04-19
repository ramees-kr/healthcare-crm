package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public String createEmployee(Employee employee) {
        try {
            // Encode the password before saving
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employeeRepository.save(employee);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String updateEmployee(Employee employee, Long id) {
        Employee oldEmployee = getEmployeeById(id);
        oldEmployee.setName(employee.getName());
        oldEmployee.setRole(employee.getRole());
        oldEmployee.setEmail(employee.getEmail());

        // Only update password if a new one is provided and encode it
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            oldEmployee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        try {
            employeeRepository.save(oldEmployee);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public String deleteEmployee(Long id) {
        try {
            employeeRepository.deleteById(id);
            return null;
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }
}