package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/employee")
@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String home() {
        if (isValidLogin()) {
            return "employee";
        }
        return "redirect:/";
    }

    public boolean isValidLogin() {
        return HomeController.employee != null;
    }
}
