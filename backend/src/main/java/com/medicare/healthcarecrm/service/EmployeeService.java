package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeByEmail(String email, String password) {
        if (employeeRepository.findByEmail(email) != null) {
            return employeeRepository.findByEmail(email);
        }
        return null;
    }

    public String createEmployee(Employee employee) {
        try {
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

        if (!employee.getPassword().isEmpty()) {
            oldEmployee.setPassword(employee.getPassword());
        }

        try {
            employeeRepository.save(oldEmployee);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).get();
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