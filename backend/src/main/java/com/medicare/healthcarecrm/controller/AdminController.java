package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.service.CustomerService;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import com.medicare.healthcarecrm.validation.OnCreate;

import org.springframework.dao.DataAccessException; // Import Spring DAO exception
import org.springframework.dao.DataIntegrityViolationException; // Import specific exception
import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    // Constructor Injection
    public AdminController(TaskService taskService, EmployeeService employeeService, CustomerService customerService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    // === GET Mappings ===

    @GetMapping // Maps to /admin
    public String home(Model model) {
        model.addAttribute("activePage", "dashboard");
        try {
            long customerCount = customerService.getCustomerCount();
            long employeeCount = employeeService.getEmployeeCount();
            long pendingTaskCount = taskService.getTaskCountByStatus("Pending");
            model.addAttribute("customerCount", customerCount);
            model.addAttribute("employeeCount", employeeCount);
            model.addAttribute("pendingTaskCount", pendingTaskCount);
        } catch (Exception e) {
            log.error("Error fetching dashboard counts: {}", e.getMessage(), e);
            model.addAttribute("dashboardError", "Could not load dashboard data.");
        }
        return "admin/admin";
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        model.addAttribute("activePage", "tasks");
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin/tasks";
    }

    @GetMapping({"tasks/add", "tasks/update/{id}"})
    public String showAddTaskForm(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("activePage", "tasks");
        Tasks task;
        if (id != null) {
            task = taskService.getTaskById(id);
            if (task == null) {
                log.warn("Task with id {} not found for update, showing add form.", id);
                task = new Tasks();
                // Consider adding a flash attribute here to inform user task wasn't found
            }
        } else {
            task = new Tasks();
        }
        model.addAttribute("task", task);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/addTask";
    }

    @GetMapping("tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, Model model) {
        String error = taskService.deleteTask(id);
        if (error != null) {
            log.error("Error deleting task {}: {}", id, error);
            model.addAttribute("deleteError", error);
            model.addAttribute("tasks", taskService.getAllTasks());
            model.addAttribute("activePage", "tasks");
            return "admin/tasks";
        }
        // Consider adding a success flash attribute
        return "redirect:/admin/tasks";
    }

    @GetMapping("employees")
    public String showEmployees(Model model) {
        model.addAttribute("activePage", "employees");
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/employees";
    }

    @GetMapping({"employees/add", "employees/update/{id}"})
    public String showAddEmployeeForm(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("activePage", "employees");
        Employee employee;
        if (id != null) {
            employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                log.warn("Employee with id {} not found for update, showing add form.", id);
                employee = new Employee();
            }
        } else {
            employee = new Employee();
        }
        model.addAttribute("employee", employee);
        return "admin/addEmployee";
    }


    @GetMapping("employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {
        String error = employeeService.deleteEmployee(id);
        if (error != null) {
            log.error("Error deleting employee {}: {}", id, error);
            model.addAttribute("deleteError", error);
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("activePage", "employees");
            return "admin/employees";
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {
        model.addAttribute("activePage", "customers");
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers";
    }

    @GetMapping({"customers/add", "customers/update/{id}"})
    public String showAddCustomerForm(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("activePage", "customers");
        Customer customer;
        if (id != null) {
            customer = customerService.getCustomerById(id);
            if (customer == null) {
                log.warn("Customer with id {} not found for update, showing add form.", id);
                customer = new Customer();
            }
        } else {
            customer = new Customer();
        }
        model.addAttribute("customer", customer);
        return "admin/addCustomer";
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        String error = customerService.deleteCustomer(id);
        if (error != null) {
            log.error("Error deleting customer {}: {}", id, error);
            model.addAttribute("deleteError", error);
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("activePage", "customers");
            return "admin/customers";
        }
        return "redirect:/admin/customers";
    }

    // === UPDATED METHOD for Follow-Up Feature ===
    @GetMapping("/follow-up")
    public String showFollowUp(Model model) {
        model.addAttribute("activePage", "followup");
        try {
            List<Tasks> overdueTasks = taskService.getOverdueTasks();
            List<Tasks> tasksDueSoon = taskService.getTasksDueSoon(7); // Get tasks due in the next 7 days

            model.addAttribute("overdueTasks", overdueTasks);
            model.addAttribute("tasksDueSoon", tasksDueSoon);
            log.info("Loaded Follow Up page with {} overdue tasks and {} tasks due soon.", overdueTasks.size(), tasksDueSoon.size());

        } catch (Exception e) {
            log.error("Error fetching tasks for follow-up page: {}", e.getMessage(), e);
            model.addAttribute("followUpError", "Could not load follow-up task data.");
        }
        return "admin/followup"; // Return the name of the Thymeleaf template
    }

    // === POST Mappings ===

    @PostMapping({"tasks/add", "tasks/update/{id}"})
    public String saveOrUpdateTask(@PathVariable(required = false) Long id,
                                   @Valid @ModelAttribute("task") Tasks task,
                                   BindingResult bindingResult,
                                   Model model) {

        log.info("Attempting to save/update task. Received task object: {}", task);

        // Explicitly fetch related entities
        Customer customer = null;
        if (task.getCustomer() != null && task.getCustomer().getId() != null) {
            log.info("Fetching customer with ID: {}", task.getCustomer().getId());
            customer = customerService.getCustomerById(task.getCustomer().getId());
            log.info("Fetched customer: {}", customer);
            task.setCustomer(customer);
        } else if (task.getCustomer() != null && task.getCustomer().getId() == null) {
            task.setCustomer(null);
            log.warn("Task submitted with customer object but null ID.");
        } else {
            task.setCustomer(null);
        }


        Employee employee = null;
        if (task.getEmployee() != null && task.getEmployee().getId() != null) {
            log.info("Fetching employee with ID: {}", task.getEmployee().getId());
            employee = employeeService.getEmployeeById(task.getEmployee().getId());
            log.info("Fetched employee: {}", employee);
            task.setEmployee(employee);
        } else if (task.getEmployee() != null && task.getEmployee().getId() == null) {
            task.setEmployee(null);
            log.warn("Task submitted with employee object but null ID.");
        } else {
            task.setEmployee(null);
        }

        // Re-check validation or add specific errors if lookups failed but were expected
        boolean lookupError = false;
        if (customer == null && task.getCustomer() != null && task.getCustomer().getId() != null) {
            log.warn("Customer lookup failed for ID: {}", task.getCustomer().getId());
            bindingResult.rejectValue("customer", "error.task", "Selected Customer not found or invalid.");
            lookupError = true;
        }
        if (employee == null && task.getEmployee() != null && task.getEmployee().getId() != null) {
            log.warn("Employee lookup failed for ID: {}", task.getEmployee().getId());
            bindingResult.rejectValue("employee", "error.task", "Selected Employee not found or invalid.");
            lookupError = true;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "tasks");
            log.warn("Validation errors (or missing related entity) found for Task form BEFORE service call! Errors: {}", bindingResult.getAllErrors());
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "admin/addTask";
        }

        String error = null;
        try {
            log.info("Calling service to save/update task: {}", task);
            if (id != null) {
                task.setId(id);
                error = taskService.updateTask(task, id);
            } else {
                error = taskService.createTask(task);
            }
            log.info("Service call finished. Returned error message: '{}'", error);
        } catch (Exception e) {
            log.error("Exception during saving/updating task (id={}): {}", id, e.getMessage(), e);
            error = "An unexpected system error occurred while saving the task.";
        }

        if (error != null) {
            log.error("Persistence error detected after service call: {}", error);
            model.addAttribute("activePage", "tasks");
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("persistenceError", error);
            return "admin/addTask";
        }

        log.info("Task {} successfully. Redirecting...", (id == null ? "created" : "updated"));
        return "redirect:/admin/tasks";
    }

    @PostMapping({"employees/add", "employees/update/{id}"})
    public String saveOrUpdateEmployee(@PathVariable(required = false) Long id,
                                       @Validated({OnCreate.class}) // Apply OnCreate for new, Default implicitly applies too
                                       @ModelAttribute("employee") Employee employee,
                                       BindingResult bindingResult,
                                       Model model) {

        log.info("Attempting to save/update employee: {}", employee);

        boolean isUpdate = (id != null);
        // Check if it's an update AND the only error is the password (which is allowed to be blank on update)
        boolean onlyPasswordErrorOnUpdate = isUpdate &&
                bindingResult.hasFieldErrors("password") &&
                bindingResult.getFieldErrorCount("password") == bindingResult.getErrorCount();

        if (bindingResult.hasErrors() && !onlyPasswordErrorOnUpdate) {
            model.addAttribute("activePage", "employees");
            log.warn("Validation errors found for Employee form BEFORE service call! isUpdate={}, Errors: {}", isUpdate, bindingResult.getAllErrors());
            return "admin/addEmployee";
        }

        String error = null;
        try {
            log.info("Calling service to save/update employee: {}", employee);
            if (isUpdate) {
                employee.setId(id);
                error = employeeService.updateEmployee(employee, id);
            } else {
                // Ensure validation group was applied correctly for create
                error = employeeService.createEmployee(employee);
            }
            log.info("Service call finished. Returned error message: '{}'", error);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during saving/updating employee (id={}): {}", id, e.getMessage());
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique constraint") && e.getMessage().toLowerCase().contains("email")) {
                error = "Email address already exists. Please use a different email.";
            } else {
                error = "A database constraint was violated. Please check your input.";
            }
        } catch (DataAccessException e) {
            log.error("DataAccessException during saving/updating employee (id={}): {}", id, e.getMessage(), e);
            error = "A database error occurred while saving the employee.";
        } catch (Exception e) {
            log.error("Unexpected exception during saving/updating employee (id={}): {}", id, e.getMessage(), e);
            error = "An unexpected system error occurred while saving the employee.";
        }

        if (error != null) {
            log.error("Persistence error detected after service call: {}", error);
            model.addAttribute("activePage", "employees");
            model.addAttribute("persistenceError", error);
            return "admin/addEmployee";
        }

        log.info("Employee {} successfully. Redirecting...", (id == null ? "created" : "updated"));
        return "redirect:/admin/employees";
    }

    @PostMapping({"customers/add", "customers/update/{id}"})
    public String saveOrUpdateCustomer(@PathVariable(required = false) Long id,
                                       @Valid @ModelAttribute("customer") Customer customer,
                                       BindingResult bindingResult,
                                       Model model) {

        log.info("Attempting to save/update customer: {}", customer);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "customers");
            log.warn("Validation errors found for Customer form BEFORE service call! Errors: {}", bindingResult.getAllErrors());
            return "admin/addCustomer";
        }

        String error = null;
        try {
            log.info("Calling service to save/update customer: {}", customer);
            if (id != null) {
                customer.setId(id);
                error = customerService.updateCustomer(customer, id);
            } else {
                error = customerService.createCustomer(customer);
            }
            log.info("Customer service call finished. Returned error message: '{}'", error);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during saving/updating customer (id={}): {}", id, e.getMessage());
            // Using a more general check for unique email constraint message
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique constraint") && e.getMessage().toLowerCase().contains("email")) {
                error = "Email address already exists. Please use a different email.";
            } else {
                error = "A database constraint was violated. Please check your input.";
            }
        } catch (DataAccessException e) {
            log.error("DataAccessException during saving/updating customer (id={}): {}", id, e.getMessage(), e);
            error = "A database error occurred while saving the customer.";
        } catch (Exception e) {
            log.error("Unexpected exception during saving/updating customer (id={}): {}", id, e.getMessage(), e);
            error = "An unexpected system error occurred while saving the customer.";
        }

        if (error != null) {
            log.error("Persistence error detected after customer service call: {}", error);
            model.addAttribute("activePage", "customers");
            model.addAttribute("persistenceError", error);
            return "admin/addCustomer";
        }

        log.info("Customer {} successfully. Redirecting...", (id == null ? "created" : "updated"));
        return "redirect:/admin/customers";
    }
}