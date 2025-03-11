package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.service.AdminService;
import com.medicare.healthcarecrm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private EmployeeService employeeService;

    public static Admin admin;
    public static Employee employee;

    public HomeController(AdminService adminService, EmployeeService employeeService) {
        this.adminService = adminService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String home() {
        return "index";
    }

    @PostMapping
    public String login(@RequestParam String username, @RequestParam String password, @RequestParam String role, Model model) {
        if (role.equals("admin")) {
            admin = adminService.getAdminByEmail(username, password);
            if (admin == null) {
                model.addAttribute("error", "Invalid username or password");
                return "index";
            } else {
                return "redirect:/admin";
            }
        } else {
            employee = employeeService.getEmployeeByEmail(username, password);
            if (employee == null) {
                model.addAttribute("error", "Invalid username or password");
                return "index";
            } else {
                return "redirect:/employee";
            }
        }
    }
}