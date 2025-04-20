package com.medicare.healthcarecrm.controller.api;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.service.CustomerService;
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

import java.util.List;

@RestController
@RequestMapping("/api/customers") // Base path for customer API
@Tag(name = "Customer API", description = "API endpoints for managing customers") // Swagger Tag
public class CustomerApiController {

    private static final Logger log = LoggerFactory.getLogger(CustomerApiController.class);

    private final CustomerService customerService;

    @Autowired
    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public List<Customer> getAllCustomers() {
        log.info("API request to get all customers");
        return customerService.getAllCustomers(); // Reuse existing service
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their ID.")
    @ApiResponse(responseCode = "200", description = "Customer found", content = @Content(schema = @Schema(implementation = Customer.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("API request to get customer by ID: {}", id);
        Customer customer = customerService.getCustomerById(id); // Reuse existing service
        if (customer == null) {
            log.warn("Customer not found for API request with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Adds a new customer to the system.")
    @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = Customer.class)))
    @ApiResponse(responseCode = "400", description = "Invalid customer data supplied")
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody Customer customer) { // Use @RequestBody and @Valid
        log.info("API request to create customer: {}", customer);
        // Ensure IDs are null for new entities, especially nested ones like Insurance
        customer.setId(null);
        if (customer.getInsurance() != null) {
            customer.getInsurance().setId(null); // Ensure insurance ID is null
        }
        String error = customerService.createCustomer(customer); // Reuse existing service
        if (error != null) {
            log.error("API Error creating customer: {}", error);
            // Return a more specific error response if possible (e.g., based on error content)
            if (error.contains("Email already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409 Conflict
            }
            return ResponseEntity.badRequest().body(error); // 400 Bad Request for general errors
        }
        log.info("Customer created successfully via API with ID: {}", customer.getId());
        // Return the created customer and 201 status
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer", description = "Updates details for an existing customer by ID.")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = Customer.class)))
    @ApiResponse(responseCode = "400", description = "Invalid customer data supplied")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Object> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        log.info("API request to update customer ID {}: {}", id, customer);
        // Ensure the ID from the path is set on the object
        customer.setId(id);
        String error = customerService.updateCustomer(customer, id); // Reuse existing service

        if (error != null) {
            log.error("API Error updating customer {}: {}", id, error);
            if (error.startsWith("Customer not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
            }
            if (error.contains("Email already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error); // 409 Conflict
            }
            return ResponseEntity.badRequest().body(error); // 400 Bad Request
        }

        // Fetch the updated customer to return it (optional, but good practice)
        Customer updatedCustomer = customerService.getCustomerById(id);
        if (updatedCustomer == null) {
            // Should not happen if update succeeded, but handle defensively
            log.error("Could not retrieve updated customer with ID: {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Customer updated but could not be retrieved.");
        }
        log.info("Customer updated successfully via API with ID: {}", id);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Deletes a customer by their ID.")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @ApiResponse(responseCode = "400", description = "Error deleting customer (e.g., constraints)")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long id) {
        log.info("API request to delete customer ID: {}", id);
        String error = customerService.deleteCustomer(id); // Reuse existing service
        if (error != null) {
            log.error("API Error deleting customer {}: {}", id, error);
            if (error.startsWith("Customer not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found
            }
            // Handle potential constraint violations if necessary, maybe return 409 Conflict or 400 Bad Request
            return ResponseEntity.badRequest().body(error); // 400 Bad Request
        }
        log.info("Customer deleted successfully via API with ID: {}", id);
        return ResponseEntity.noContent().build(); // 204 No Content on successful deletion
    }
}