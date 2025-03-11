package com.medicare.healthcarecrm.controller;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.service.AdminService;
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

    @Autowired
    private AdminService adminService;
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public AdminController(AdminService adminService, TaskService taskService, EmployeeService employeeService, CustomerService customerService) {
        this.adminService = adminService;
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    @GetMapping
    public String home(Model model) {
        if (isValidLogin()) {
            return "admin/admin";
        }
        return "redirect:/";
    }

    @GetMapping("/tasks")
    public String showTasks(Model model) {
        if (isValidLogin()) {
            model.addAttribute("tasks", taskService.getAllTasks());
            return "admin/tasks";
        }
        return "redirect:/";
    }

    @GetMapping({"tasks/add", "tasks/update/{id}"})
    public String addTask(@PathVariable(required = false) Long id, Model model) {
        if (isValidLogin()) {
            Tasks tasks = (id != null) ? taskService.getTaskById(id) : new Tasks();
            model.addAttribute("task", tasks);
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "admin/addTask";
        }
        return "redirect:/";
    }

    @PostMapping({"tasks/add", "tasks/update/{id}"})
    public String addTask(@PathVariable(required = false) Long id, Tasks tasks, Model model) {
        if (isValidLogin()) {
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
        return "redirect:/";
    }

    @GetMapping("tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, Model model) {
        if (isValidLogin()) {
            String error = taskService.deleteTask(id);
            if (error != null) {
                model.addAttribute("deleteError", error);
                return showEmployees(model);
            }
            return "redirect:/admin/tasks";
        }
        return "redirect:/";
    }


    @GetMapping("employees")
    public String showEmployees(Model model) {
        if (isValidLogin()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "admin/employees";
        }
        return "redirect:/";
    }

    @GetMapping({"employees/add", "employees/update/{id}"})
    public String addEmployee(@PathVariable(required = false) Long id, Model model) {
        if (isValidLogin()) {
            Employee employee = (id != null) ? employeeService.getEmployeeById(id) : new Employee();
            model.addAttribute("employee", employee);
            return "admin/addEmployee";
        }
        return "redirect:/";
    }

    @PostMapping({"employees/add", "employees/update/{id}"})
    public String addEmployee(@PathVariable(required = false) Long id, Employee employee, Model model) {
        if (isValidLogin()) {
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
        return "redirect:/";
    }

    @GetMapping("employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {
        if (isValidLogin()) {
            String error = employeeService.deleteEmployee(id);
            if (error != null) {
                model.addAttribute("deleteError", error);
                return showEmployees(model);
            }
            return "redirect:/admin/employees";
        }
        return "redirect:/";
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {
        if (isValidLogin()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            return "admin/customers";
        }
        return "redirect:/";
    }

    @GetMapping({"customers/add", "customers/update/{id}"})
    public String addCustomer(@PathVariable(required = false) Long id, Model model) {
        if (isValidLogin()) {
            Customer customer = (id != null) ? customerService.getCustomerById(id) : new Customer();
            model.addAttribute("customer", customer);
            return "admin/addCustomer";
        }
        return "redirect:/";
    }

    @PostMapping({"customers/add", "customers/update/{id}"})
    public String addCustomer(@PathVariable(required = false) Long id, Customer customer, Model model) {
        if (isValidLogin()) {
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
        }
        return "redirect:/";
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        if (isValidLogin()) {
            String error = customerService.deleteCustomer(id);
            if (error != null) {
                model.addAttribute("deleteError", error);
                return showEmployees(model);
            }
            return "redirect:/admin/employees";
        }
        return "redirect:/";
    }

    @GetMapping("/follow-up")
    public String showFollowUp(Model model) {
        if (isValidLogin()) {
            return "admin/followup";
        }
        return "redirect:/";
    }

    public boolean isValidLogin() {
        return HomeController.admin != null;
    }

//    public static List<Employee> getDummyEmployees() {
//        return Arrays.asList(
//                Employee.builder()
//                        .name("John Doe")
//                        .role("Doctor")
//                        .email("johndoe@example.com")
//                        .password("securePass123")
//                        .build(),
//
//                Employee.builder()
//                        .name("Jane Smith")
//                        .role("Nurse")
//                        .email("janesmith@example.com")
//                        .password("nursePass456")
//                        .build(),
//
//                Employee.builder()
//                        .name("Robert Brown")
//                        .role("Assistant")
//                        .email("robertbrown@example.com")
//                        .password("adminSecure789")
//                        .build(),
//
//                Employee.builder()
//                        .name("Emily Davis")
//                        .role("Receptionist")
//                        .email("emilydavis@example.com")
//                        .password("receptionPass")
//                        .build()
//        );
//    }

//    public static List<Customer> getDummyCustomers() {
//        return Arrays.asList(
//                Customer.builder()
//                        .name("Alice Johnson")
//                        .age(34)
//                        .gender("Female")
//                        .email("alice.johnson@example.com")
//                        .medicalHistory("Diabetes, Hypertension")
//                        .contactDetails("+1-555-1234")
//                        .insurance(
//                                Insurance.builder()
//                                        .provider("BlueCross")
//                                        .policyNumber("BC123456")
//                                        .coverageDetails("Full coverage including dental and vision")
//                                        .expiryDate(new Date(System.currentTimeMillis() + 31556952000L)) // 1 year from now
//                                        .build()
//                        )
//                        .build(),
//
//                Customer.builder()
//                        .name("Mark Evans")
//                        .age(45)
//                        .gender("Male")
//                        .email("mark.evans@example.com")
//                        .medicalHistory("Asthma")
//                        .contactDetails("+1-555-5678")
//                        .insurance(
//                                Insurance.builder()
//                                        .provider("UnitedHealth")
//                                        .policyNumber("UH987654")
//                                        .coverageDetails("Basic health coverage, no dental")
//                                        .expiryDate(new Date(System.currentTimeMillis() + 63072000000L)) // 2 years from now
//                                        .build()
//                        )
//                        .build(),
//
//                Customer.builder()
//                        .name("Sophia Martinez")
//                        .age(29)
//                        .gender("Female")
//                        .email("sophia.martinez@example.com")
//                        .medicalHistory("No known conditions")
//                        .contactDetails("+1-555-8765")
//                        .insurance(
//                                Insurance.builder()
//                                        .provider("Aetna")
//                                        .policyNumber("AET654321")
//                                        .coverageDetails("Comprehensive health and dental")
//                                        .expiryDate(new Date(System.currentTimeMillis() + 94608000000L)) // 3 years from now
//                                        .build()
//                        )
//                        .build()
//        );
//    }

//    public static List<Tasks> getDummyTasks(List<Customer> customers, List<Employee> employees) {
//        return Arrays.asList(
//                Tasks.builder()
//                        .taskName("Schedule Follow-up Appointment")
//                        .customer(customers.get(0)) // Assuming customers list has at least one entry
//                        .employee(employees.get(0)) // Assuming employees list has at least one entry
//                        .dueDate(LocalDateTime.now().plusDays(7))
//                        .priority("High")
//                        .description("Follow-up checkup for diabetes management")
//                        .status("Pending")
//                        .build(),
//
//                Tasks.builder()
//                        .taskName("Update Medical Records")
//                        .customer(customers.get(1))
//                        .employee(employees.get(1))
//                        .dueDate(LocalDateTime.now().plusDays(3))
//                        .priority("Medium")
//                        .description("Update latest test results in patient records")
//                        .status("In Progress")
//                        .build(),
//
//                Tasks.builder()
//                        .taskName("Insurance Verification")
//                        .customer(customers.get(2))
//                        .employee(employees.get(2))
//                        .dueDate(LocalDateTime.now().plusDays(5))
//                        .priority("Low")
//                        .description("Verify insurance details for upcoming surgery")
//                        .status("Completed")
//                        .build()
//        );
//    }

}
