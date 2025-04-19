package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable
import org.springframework.web.bind.annotation.PostMapping; // Import PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Import RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import RedirectAttributes

import jakarta.persistence.EntityNotFoundException; // Import EntityNotFoundException

import java.util.List;

@RequestMapping("/employee")
@Controller
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class); // Add Logger

    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeService employeeService, TaskService taskService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public String home(Model model) {
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
        }
        return "employee";
    }

    // Add this PostMapping method
    @PostMapping("/tasks/{taskId}/updateStatus")
    public String updateTaskStatus(@PathVariable Long taskId,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) { // Inject RedirectAttributes

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        try {
            // Get the current employee (needed for the service layer check)
            Employee currentEmployee = employeeRepository.findByEmail(currentPrincipalName);
            if (currentEmployee == null) {
                // This case should ideally not happen if the user is authenticated
                throw new UsernameNotFoundException("Authenticated employee not found in database.");
            }

            // Call the service method to update status
            String error = taskService.updateTaskStatus(taskId, status, currentEmployee);

            if (error == null) {
                // Success message
                redirectAttributes.addFlashAttribute("successMessage", "Task #" + taskId + " status updated to '" + status + "'.");
            } else {
                // Error message from service (e.g., task not found, access denied)
                redirectAttributes.addFlashAttribute("errorMessage", error);
            }

        } catch (UsernameNotFoundException | AccessDeniedException | EntityNotFoundException e) {
            log.error("Error processing task status update for task {}: {}", taskId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating task: " + e.getMessage());
        } catch (Exception e) {
            // Catch unexpected errors
            log.error("Unexpected error updating task status for task {}: {}", taskId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }

        // Redirect back to the employee task list page
        return "redirect:/employee";
    }

}