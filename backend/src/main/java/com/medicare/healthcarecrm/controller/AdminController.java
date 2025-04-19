package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
// AdminService import might be unused now unless needed for other admin-specific actions
// import com.medicare.healthcarecrm.service.AdminService;
import com.medicare.healthcarecrm.service.CustomerService;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;

import jakarta.validation.Valid; // <<< Import @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // <<< Import BindingResult
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // <<< Import ModelAttribute
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;


@RequestMapping("/admin")
@Controller
public class AdminController {

    // Add Logger
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    // Keep Autowired fields and Constructor Injection as before
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public AdminController(TaskService taskService, EmployeeService employeeService, CustomerService customerService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    @GetMapping
    public String home(Model model) {
        // Placeholder for Step 8 - Populate Admin Dashboard
        // Example: Add counts
        // model.addAttribute("customerCount", customerService.getCustomerCount());
        // model.addAttribute("employeeCount", employeeService.getEmployeeCount());
        // model.addAttribute("pendingTaskCount", taskService.getPendingTaskCount());
        return "admin/admin";
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin/tasks";
    }

    @GetMapping({"tasks/add", "tasks/update/{id}"})
    public String showAddTaskForm(@PathVariable(required = false) Long id, Model model) { // Renamed for clarity
        Tasks task = (id != null) ? taskService.getTaskById(id) : new Tasks();
        model.addAttribute("task", task); // Use "task" (singular) to match th:object
        model.addAttribute("customers", customerService.getAllCustomers()); // Needed for dropdown
        model.addAttribute("employees", employeeService.getAllEmployees()); // Needed for dropdown
        return "admin/addTask";
    }

    @GetMapping("tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, Model model) {
        String error = taskService.deleteTask(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            // Repopulate task list on error if needed, or handle differently
            model.addAttribute("tasks", taskService.getAllTasks());
            return "admin/tasks"; // Return to the list view with error
        }
        return "redirect:/admin/tasks";
    }

    @GetMapping("employees")
    public String showEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/employees";
    }

    @GetMapping({"employees/add", "employees/update/{id}"})
    public String showAddEmployeeForm(@PathVariable(required = false) Long id, Model model) { // Renamed for clarity
        Employee employee = (id != null) ? employeeService.getEmployeeById(id) : new Employee();
        model.addAttribute("employee", employee);
        return "admin/addEmployee";
    }


    @GetMapping("employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {
        String error = employeeService.deleteEmployee(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            model.addAttribute("employees", employeeService.getAllEmployees()); // Repopulate list
            return "admin/employees"; // Return to list view with error
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers";
    }

    @GetMapping({"customers/add", "customers/update/{id}"})
    public String showAddCustomerForm(@PathVariable(required = false) Long id, Model model) { // Renamed for clarity
        Customer customer = (id != null) ? customerService.getCustomerById(id) : new Customer();
        model.addAttribute("customer", customer);
        return "admin/addCustomer";
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        String error = customerService.deleteCustomer(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            model.addAttribute("customers", customerService.getAllCustomers()); // Repopulate list
            return "admin/customers"; // Return to list view with error
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/follow-up")
    public String showFollowUp(Model model) {
        // Placeholder for Step 9 - Implement Follow-Up Feature
        // Example: Fetch tasks due soon
        // model.addAttribute("dueSoonTasks", taskService.getTasksDueSoon());
        return "admin/followup";
    }

    // --- POST Mappings with Validation ---

    // CHANGE: Use @ModelAttribute("task") to align with th:object="${task}"
    // CHANGE: Parameter name changed from 'tasks' to 'task'
    @PostMapping({"tasks/add", "tasks/update/{id}"})
    public String saveOrUpdateTask(@PathVariable(required = false) Long id,
                                   @Valid @ModelAttribute("task") Tasks task, // Use @ModelAttribute("task")
                                   BindingResult bindingResult, // MUST come immediately after @Valid Tasks task
                                   Model model) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors found for Task form!");
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.warn("Field '{}': Rejected value [{}]; Message: {}",
                        error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            }

            // Repopulate dropdown data needed by the form
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());

            // CHANGE: No need to explicitly add 'task' back, @ModelAttribute + BindingResult handles it.
            // Ensure the submitted 'task' object (with errors) is available to the view.

            return "admin/addTask"; // Return to the form view to display errors
        }

        // --- No validation errors, proceed with saving/updating ---
        String error = null;
        try {
            if (id != null) {
                // You might want TaskService.updateTask to take 'id' and 'task'
                // and handle fetching internally for robustness.
                task.setId(id); // Ensure ID is set for update
                error = taskService.updateTask(task, id); // Pass task object and ID
            } else {
                error = taskService.createTask(task);
            }
        } catch (Exception e) {
            // Catch potential persistence or other exceptions during save/update
            log.error("Error saving or updating task: {}", e.getMessage(), e);
            error = "An unexpected error occurred while saving the task."; // User-friendly message
        }


        // Handle potential errors during the save/update operation
        if (error != null) {
            log.error("Persistence error saving task: {}", error);
            // Add submitted data back to model for redisplay
            // model.addAttribute("task", task); // @ModelAttribute should handle this, but double-check if needed
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("persistenceError", error); // Use a specific key for persistence errors

            return "admin/addTask"; // Return to form view with persistence error
        }

        // Success - redirect to prevent duplicate submissions
        return "redirect:/admin/tasks";
    }

    // CHANGE: Use @ModelAttribute("employee") for consistency
    @PostMapping({"employees/add", "employees/update/{id}"})
    public String saveOrUpdateEmployee(@PathVariable(required = false) Long id,
                                       @Valid @ModelAttribute("employee") Employee employee,
                                       BindingResult bindingResult,
                                       Model model) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // 'employee' object with invalid data is automatically added back by @ModelAttribute
            return "admin/addEmployee";
        }

        // No validation errors, proceed with create/update logic
        String error = null;
        try {
            if (id != null) {
                employee.setId(id); // Ensure ID is set
                error = employeeService.updateEmployee(employee, id);
            } else {
                error = employeeService.createEmployee(employee);
            }
        } catch (Exception e) {
            log.error("Error saving or updating employee: {}", e.getMessage(), e);
            error = "An unexpected error occurred while saving the employee."; // User-friendly message
        }


        if (error != null) {
            // Handle persistence errors (e.g., duplicate email)
            // model.addAttribute("employee", employee); // Already handled by @ModelAttribute
            model.addAttribute("persistenceError", error); // Show persistence error
            return "admin/addEmployee";
        }

        // Success
        return "redirect:/admin/employees";
    }

    // CHANGE: Use @ModelAttribute("customer") for consistency
    @PostMapping({"customers/add", "customers/update/{id}"})
    public String saveOrUpdateCustomer(@PathVariable(required = false) Long id,
                                       @Valid @ModelAttribute("customer") Customer customer,
                                       BindingResult bindingResult,
                                       Model model) {
        // Check for validation errors (including nested Insurance validation)
        if (bindingResult.hasErrors()) {
            // 'customer' object with invalid data is automatically added back by @ModelAttribute
            return "admin/addCustomer";
        }

        // No validation errors, proceed with create/update logic
        String error = null;
        try {
            if (id != null) {
                customer.setId(id); // Ensure ID is set
                // Make sure insurance ID is preserved if it exists
                if (customer.getInsurance() != null && customer.getInsurance().getId() == null) {
                    Customer existingCustomer = customerService.getCustomerById(id);
                    if (existingCustomer != null && existingCustomer.getInsurance() != null) {
                        customer.getInsurance().setId(existingCustomer.getInsurance().getId());
                    }
                }
                error = customerService.updateCustomer(customer, id);
            } else {
                error = customerService.createCustomer(customer);
            }
        } catch (Exception e) {
            log.error("Error saving or updating customer: {}", e.getMessage(), e);
            error = "An unexpected error occurred while saving the customer."; // User-friendly message
        }


        if (error != null) {
            // Handle persistence errors
            // model.addAttribute("customer", customer); // Already handled by @ModelAttribute
            model.addAttribute("persistenceError", error); // Show persistence error
            return "admin/addCustomer";
        }

        // Success
        return "redirect:/admin/customers";
    }
}