package com.medicare.healthcarecrm.controller.api;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.service.CustomerService;
import com.medicare.healthcarecrm.service.EmployeeService;
import com.medicare.healthcarecrm.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task API", description = "API endpoints for managing tasks")
public class TaskApiController {

    private static final Logger log = LoggerFactory.getLogger(TaskApiController.class);

    private final TaskService taskService;
    private final CustomerService customerService; // Needed to fetch customer if only ID is provided
    private final EmployeeService employeeService; // Needed to fetch employee if only ID is provided


    @Autowired
    public TaskApiController(TaskService taskService, CustomerService customerService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public List<Tasks> getAllTasks() {
        log.info("API request to get all tasks");
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID.")
    @ApiResponse(responseCode = "200", description = "Task found", content = @Content(schema = @Schema(implementation = Tasks.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Tasks> getTaskById(@PathVariable Long id) {
        log.info("API request to get task by ID: {}", id);
        Tasks task = taskService.getTaskById(id);
        if (task == null) {
            log.warn("Task not found for API request with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Adds a new task. Requires customer and employee IDs in the nested objects.")
    @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = Tasks.class)))
    @ApiResponse(responseCode = "400", description = "Invalid task data supplied (validation error or missing/invalid customer/employee ID)")
    public ResponseEntity<Object> createTask(@Valid @RequestBody Tasks task) {
        log.info("API request to create task: {}", task);
        task.setId(null); // Ensure ID is null for creation

        // --- Resolve Customer and Employee ---
        // Expecting request body to have structure like:
        // { ..., "customer": { "id": 123 }, "employee": { "id": 456 }, ... }
        String lookupError = resolveTaskRelations(task);
        if (lookupError != null) {
            return ResponseEntity.badRequest().body(lookupError);
        }
        // --- ---

        String error = taskService.createTask(task);
        if (error != null) {
            log.error("API Error creating task: {}", error);
            return ResponseEntity.badRequest().body(error);
        }

        // Fetch the created task to ensure all fields (like generated ID) are present
        Tasks createdTask = taskService.getTaskById(task.getId());

        log.info("Task created successfully via API with ID: {}", createdTask != null ? createdTask.getId() : "unknown");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task", description = "Updates details for an existing task by ID. Requires customer and employee IDs.")
    @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(schema = @Schema(implementation = Tasks.class)))
    @ApiResponse(responseCode = "400", description = "Invalid task data supplied (validation error or missing/invalid customer/employee ID)")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Object> updateTask(@PathVariable Long id, @Valid @RequestBody Tasks task) {
        log.info("API request to update task ID {}: {}", id, task);
        task.setId(id); // Set ID from path

        // --- Resolve Customer and Employee ---
        String lookupError = resolveTaskRelations(task);
        if (lookupError != null) {
            return ResponseEntity.badRequest().body(lookupError);
        }
        // --- ---

        String error = taskService.updateTask(task, id);
        if (error != null) {
            log.error("API Error updating task {}: {}", id, error);
            if (error.startsWith("Task not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
            }
            return ResponseEntity.badRequest().body(error); // 400 Bad Request
        }

        Tasks updatedTask = taskService.getTaskById(id);
        if (updatedTask == null) {
            log.error("Could not retrieve updated task with ID: {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Task updated but could not be retrieved.");
        }

        log.info("Task updated successfully via API with ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Error deleting task")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        log.info("API request to delete task ID: {}", id);
        String error = taskService.deleteTask(id);
        if (error != null) {
            log.error("API Error deleting task {}: {}", id, error);
            if (error.startsWith("Task not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
            }
            return ResponseEntity.badRequest().body(error); // 400 Bad Request
        }
        log.info("Task deleted successfully via API with ID: {}", id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // --- Helper Method to resolve Customer/Employee from IDs ---
    private String resolveTaskRelations(Tasks task) {
        if (task.getCustomer() == null || task.getCustomer().getId() == null) {
            log.warn("Task creation/update failed: Customer ID missing.");
            return "Customer ID is required.";
        }
        if (task.getEmployee() == null || task.getEmployee().getId() == null) {
            log.warn("Task creation/update failed: Employee ID missing.");
            return "Employee ID is required.";
        }

        try {
            Customer customer = customerService.getCustomerById(task.getCustomer().getId());
            if (customer == null) {
                log.warn("Task creation/update failed: Customer not found with ID {}", task.getCustomer().getId());
                return "Customer with ID " + task.getCustomer().getId() + " not found.";
            }
            task.setCustomer(customer); // Set the full object

            Employee employee = employeeService.getEmployeeById(task.getEmployee().getId());
            // getEmployeeById throws exception if not found, so no need for null check here
            task.setEmployee(employee); // Set the full object

            return null; // No errors

        } catch (RuntimeException e) { // Catch employee not found exception
            log.warn("Task creation/update failed: Employee not found with ID {}", task.getEmployee().getId());
            return "Employee with ID " + task.getEmployee().getId() + " not found.";
        } catch (Exception e) {
            log.error("Unexpected error resolving task relations: {}", e.getMessage(), e);
            return "An unexpected error occurred while looking up customer or employee.";
        }
    }
}