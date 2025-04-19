package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Make sure Model is imported
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@RequestMapping("/employee")
@Controller
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService; // Not used directly, but keep if needed
    private final TaskService taskService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeService employeeService, TaskService taskService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping // Handles /employee
    public String home(Model model) { // Add Model parameter
        // <<< ADD activePage attribute >>>
        model.addAttribute("activePage", "myTasks");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        try {
            Employee currentEmployee = employeeRepository.findByEmail(currentPrincipalName);
            if (currentEmployee == null) {
                throw new UsernameNotFoundException("Employee not found with email: " + currentPrincipalName);
            }
            List<Tasks> assignedTasks = taskService.getTasksByEmployee(currentEmployee);
            model.addAttribute("employeeTasks", assignedTasks);
            model.addAttribute("employeeName", currentEmployee.getName());
        } catch (UsernameNotFoundException e) {
            log.error("Error fetching employee details for {}: {}", currentPrincipalName, e.getMessage());
            model.addAttribute("errorMessage", "Could not retrieve employee details."); // Use errorMessage for clarity
            // Still return the view even if employee details fail
        }
        return "employee"; // Return the employee task list view name
    }

    @PostMapping("/tasks/{taskId}/updateStatus")
    public String updateTaskStatus(@PathVariable Long taskId,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String resultMessageKey = "errorMessage"; // Default to error
        String message = "An unexpected error occurred.";

        try {
            Employee currentEmployee = employeeRepository.findByEmail(currentPrincipalName);
            if (currentEmployee == null) {
                throw new UsernameNotFoundException("Authenticated employee not found in database.");
            }

            String serviceError = taskService.updateTaskStatus(taskId, status, currentEmployee);

            if (serviceError == null) {
                resultMessageKey = "successMessage"; // Change to success
                message = "Task #" + taskId + " status updated to '" + status + "'.";
                log.info("Task {} status updated to {} by employee {}", taskId, status, currentPrincipalName);
            } else {
                message = serviceError; // Use error from service
                log.warn("Failed to update task {} status to {} by employee {}: {}", taskId, status, currentPrincipalName, serviceError);
            }

        } catch (UsernameNotFoundException | AccessDeniedException | EntityNotFoundException e) {
            log.error("Error processing task status update for task {}: {}", taskId, e.getMessage());
            message = "Error updating task: " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error updating task status for task {}: {}", taskId, e.getMessage(), e);
            // Keep generic message for unexpected errors
        }

        redirectAttributes.addFlashAttribute(resultMessageKey, message);
        return "redirect:/employee";
    }

}