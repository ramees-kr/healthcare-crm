package com.medicare.healthcarecrm.config;

import com.medicare.healthcarecrm.model.Admin;
import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Insurance;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.AdminRepository;
import com.medicare.healthcarecrm.repository.CustomerRepository;
import com.medicare.healthcarecrm.repository.EmployeeRepository;
import com.medicare.healthcarecrm.repository.TasksRepository;
import com.medicare.healthcarecrm.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final AdminService adminService;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final TasksRepository tasksRepository;
    private final PasswordEncoder passwordEncoder;

    // Use ThreadLocalRandom for better performance and less contention if needed
    private static final Random random = ThreadLocalRandom.current();

    @Autowired
    public DataInitializer(AdminRepository adminRepository,
                           AdminService adminService,
                           EmployeeRepository employeeRepository,
                           CustomerRepository customerRepository,
                           TasksRepository tasksRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.tasksRepository = tasksRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- Create Default Admin (Existing Logic) ---
        if (adminRepository.count() == 0) {
            log.info("No admin users found. Creating default admin user...");
            Admin defaultAdmin = Admin.builder()
                    .name("Default Admin")
                    .role("ADMIN")
                    .email("admin@clinic.com")
                    .password("password123")
                    .build();
            try {
                adminService.createAdmin(defaultAdmin);
                log.info("Default admin user created successfully with email: admin@clinic.com");
                log.warn("Default admin password is: password123");
            } catch (Exception e) {
                log.error("Failed to create default admin user: {}", e.getMessage());
            }
        } else {
            log.info("Admin users already exist. Skipping default admin creation.");
        }


        log.info("Checking if mock data needs to be generated...");

        // --- Create Mock Employees ---
        List<Employee> savedEmployees = new ArrayList<>();
        if (employeeRepository.count() == 0) {
            log.info("No employees found. Creating mock employees...");
            String defaultPassword = "password123";
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            List<Employee> employeesToCreate = Arrays.asList(
                    Employee.builder().name("Alice Johnson").role("Nurse").email("alice.j@clinic.com").password(encodedPassword).build(),
                    Employee.builder().name("Bob Williams").role("Receptionist").email("bob.w@clinic.com").password(encodedPassword).build(),
                    Employee.builder().name("Charlie Brown").role("Therapist").email("charlie.b@clinic.com").password(encodedPassword).build(),
                    Employee.builder().name("Diana Davis").role("Admin Assistant").email("diana.d@clinic.com").password(encodedPassword).build(),
                    Employee.builder().name("Ethan Miller").role("Nurse").email("ethan.m@clinic.com").password(encodedPassword).build()
            );
            try {
                savedEmployees = employeeRepository.saveAll(employeesToCreate);
                log.info("Successfully created {} mock employees.", savedEmployees.size());
                log.warn("Default password for mock employees is: {}", defaultPassword);
            } catch (Exception e) {
                log.error("Failed to create mock employees: {}", e.getMessage(), e);
                savedEmployees.clear();
            }
        } else {
            log.info("Employees already exist. Skipping mock employee creation.");
            savedEmployees = employeeRepository.findAll();
            log.info("Loaded {} existing employees.", savedEmployees.size());
        }
        // --- End Mock Employees ---

        // --- Create Mock Customers (and Insurance) ---
        List<Customer> savedCustomers = new ArrayList<>();
        if (!savedEmployees.isEmpty() && customerRepository.count() == 0) {
            log.info("No customers found. Creating mock customers...");
            List<Customer> customersToCreate = new ArrayList<>();
            String[] firstNames = {"James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth"};
            String[] lastNames = {"Smith", "Jones", "Taylor", "Brown", "Davis", "Miller", "Wilson", "Moore", "Martin", "Lee"};
            String[] genders = {"Male", "Female", "Other"};
            String[] providers = {"BlueCross", "SunLife", "Manulife", "Great-West", "Green Shield", "Canada Life"};
            String[] medicalHistories = {"History of asthma...", "Type 2 Diabetes...", "Previous appendectomy...", "Hypertension...", "Seasonal allergies.", "No significant history.", "Well-managed hypothyroidism.", "History of migraines."};
            String[] coverageDetails = {"Basic: Hospital, Medical.", "Extended: Rx, Dental, Vision.", "Comprehensive: Physio, Chiro.", "Basic + travel.", "Standard group plan."};

            for (int i = 0; i < 20; i++) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                String name = firstName + " " + lastName;
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@email.com";
                String gender = genders[random.nextInt(genders.length)];
                int age = random.nextInt(73) + 18;
                String phone = String.format("(%03d) %03d-%04d", random.nextInt(800)+200, random.nextInt(1000), random.nextInt(10000));
                String medicalHistory = medicalHistories[random.nextInt(medicalHistories.length)];

                Insurance insurance = Insurance.builder()
                        .provider(providers[random.nextInt(providers.length)])
                        .policyNumber(String.format("%c%c%07d", (char)('A' + random.nextInt(26)), (char)('A' + random.nextInt(26)), random.nextInt(10000000)))
                        .coverageDetails(coverageDetails[random.nextInt(coverageDetails.length)])
                        .expiryDate(LocalDateTime.now().plusMonths(6 + random.nextInt(55)))
                        .build();
                Customer customer = Customer.builder()
                        .name(name).age(age).gender(gender).email(email)
                        .medicalHistory(medicalHistory).contactDetails(phone)
                        .insurance(insurance).build();
                customersToCreate.add(customer);
            }
            try {
                savedCustomers = customerRepository.saveAll(customersToCreate);
                log.info("Successfully created {} mock customers (with insurance).", savedCustomers.size());
            } catch (Exception e) {
                log.error("Failed to create mock customers: {}", e.getMessage(), e);
                savedCustomers.clear();
            }
        } else if (!savedEmployees.isEmpty()) {
            log.info("Customers already exist. Skipping mock customer creation.");
            savedCustomers = customerRepository.findAll();
            log.info("Loaded {} existing customers.", savedCustomers.size());
        } else {
            log.warn("Skipping customer generation as no employees were loaded/created.");
        }
        // --- End Mock Customers ---


        // --- Create Mock Tasks ---
        if (!savedEmployees.isEmpty() && !savedCustomers.isEmpty() && tasksRepository.count() == 0) { // Check tasks count
            log.info("No tasks found. Creating mock tasks...");
            List<Tasks> tasksToCreate = new ArrayList<>();
            String[] taskPrefixes = {"Schedule", "Update", "Prepare", "Call patient about", "Process", "Verify", "Follow up on", "Review"};
            String[] taskSubjects = {"appointment", "insurance info", "consultation room", "lab results", "referral request", "billing code", "prescription refill", "patient chart"};
            String[] priorities = {"Low", "Medium", "High"};
            String[] statuses = {"Pending", "In Progress", "Completed"};

            int numberOfTasks = Math.min(savedCustomers.size() * 3, 50); // Create up to 3 tasks per customer, max 50

            for (int i = 0; i < numberOfTasks; i++) {
                Customer randomCustomer = savedCustomers.get(random.nextInt(savedCustomers.size()));
                Employee randomEmployee = savedEmployees.get(random.nextInt(savedEmployees.size()));
                String taskName = taskPrefixes[random.nextInt(taskPrefixes.length)] + " " + taskSubjects[random.nextInt(taskSubjects.length)];
                String description = "Task related to " + taskName + " for customer " + randomCustomer.getName() + ". Please action promptly. Assigned to " + randomEmployee.getName() + ".";
                String priority = priorities[random.nextInt(priorities.length)];
                String status = statuses[random.nextInt(statuses.length)]; // Simple random status for now
                LocalDateTime dueDate = LocalDateTime.now().plusDays(random.nextInt(60) - 15); // Due between 15 days ago and 45 days from now

                Tasks task = Tasks.builder()
                        .taskName(taskName)
                        .customer(randomCustomer) // Assign Customer object
                        .employee(randomEmployee) // Assign Employee object
                        .dueDate(dueDate)
                        .priority(priority)
                        .description(description)
                        .status(status)
                        .build();
                tasksToCreate.add(task);
            }

            try {
                tasksRepository.saveAll(tasksToCreate);
                log.info("Successfully created {} mock tasks.", tasksToCreate.size());
            } catch (Exception e) {
                log.error("Failed to create mock tasks: {}", e.getMessage(), e);
            }

        } else if (savedEmployees.isEmpty() || savedCustomers.isEmpty()) {
            log.warn("Skipping task generation as employees or customers are missing.");
        } else {
            log.info("Tasks already exist. Skipping mock task creation.");
        }
        // --- End Mock Tasks ---

        log.info("Data initialization process completed.");
    }
}
