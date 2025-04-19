package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
// AdminService import might be unused now unless needed for other admin-specific actions
// import com.medicare.healthcarecrm.service.AdminService;
import com.medicare.healthcarecrm.service.CustomerService;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    // Keep Constructor Injection
    public AdminController(/*AdminService adminService,*/ TaskService taskService, EmployeeService employeeService, CustomerService customerService) {
        // this.adminService = adminService; // Keep if adminService is needed
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    @GetMapping
    public String home(Model model) {
        return "admin/admin";
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        // REMOVED: if (isValidLogin()) { ... } check
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin/tasks";
    }

    @GetMapping({"tasks/add", "tasks/update/{id}"})
    public String addTask(@PathVariable(required = false) Long id, Model model) {
        Tasks tasks = (id != null) ? taskService.getTaskById(id) : new Tasks();
        model.addAttribute("task", tasks);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/addTask";
    }

    @PostMapping({"tasks/add", "tasks/update/{id}"})
    public String addTask(@PathVariable(required = false) Long id, Tasks tasks, Model model) {
        String error = null;
        if (id != null) {
            error = taskService.updateTask(tasks, id);
        } else {
            error = taskService.createTask(tasks);
        }
        if (error != null) {
            model.addAttribute("task", tasks);
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("error", error);
            return "admin/addTask";
        }
        return "redirect:/admin/tasks";
    }

    @GetMapping("tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, Model model) {
        String error = taskService.deleteTask(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            // Revisit this logic - showEmployees might not be the right place on error
            return showTasks(model); // Changed from showEmployees to showTasks for consistency
        }
        return "redirect:/admin/tasks";
    }


    @GetMapping("employees")
    public String showEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/employees";
    }

    @GetMapping({"employees/add", "employees/update/{id}"})
    public String addEmployee(@PathVariable(required = false) Long id, Model model) {
        Employee employee = (id != null) ? employeeService.getEmployeeById(id) : new Employee();
        model.addAttribute("employee", employee);
        return "admin/addEmployee";
    }

    @PostMapping({"employees/add", "employees/update/{id}"})
    public String addEmployee(@PathVariable(required = false) Long id, Employee employee, Model model) {
        String error = null;
        if (id != null) {
            error = employeeService.updateEmployee(employee, id);
        } else {
            error = employeeService.createEmployee(employee);
        }
        if (error != null) {
            model.addAttribute("employee", employee);
            model.addAttribute("error", error);
            return "admin/addEmployee";
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {
        String error = employeeService.deleteEmployee(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            return showEmployees(model); // Show employees list again with error
        }
        return "redirect:/admin/employees";
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers";
        // REMOVED: else part returning "redirect:/"
    }

    @GetMapping({"customers/add", "customers/update/{id}"})
    public String addCustomer(@PathVariable(required = false) Long id, Model model) {
        // REMOVED: if (isValidLogin()) { ... } check
        Customer customer = (id != null) ? customerService.getCustomerById(id) : new Customer();
        model.addAttribute("customer", customer);
        return "admin/addCustomer";
        // REMOVED: else part returning "redirect:/"
    }

    @PostMapping({"customers/add", "customers/update/{id}"})
    public String addCustomer(@PathVariable(required = false) Long id, Customer customer, Model model) {
        // REMOVED: if (isValidLogin()) { ... } check
        String error = null;
        if (id != null) {
            error = customerService.updateCustomer(customer, id);
        } else {
            error = customerService.createCustomer(customer);
        }
        if (error != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("error", error);
            return "admin/addCustomer";
        }
        return "redirect:/admin/customers";
        // REMOVED: else part returning "redirect:/"
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        // REMOVED: if (isValidLogin()) { ... } check
        String error = customerService.deleteCustomer(id);
        if (error != null) {
            model.addAttribute("deleteError", error);
            // Revisit this logic - showEmployees seems wrong here
            return showCustomers(model); // Changed to showCustomers
        }
        // Changed redirect to /admin/customers for consistency
        return "redirect:/admin/customers";
        // REMOVED: else part returning "redirect:/"
    }

    @GetMapping("/follow-up")
    public String showFollowUp(Model model) {
        return "admin/followup";
    }

}