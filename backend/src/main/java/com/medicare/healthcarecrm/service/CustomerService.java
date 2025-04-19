package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Insurance; // Import Insurance if needed for logic
import com.medicare.healthcarecrm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Ensure this is present
import org.slf4j.Logger; // Add Logger
import org.slf4j.LoggerFactory; // Add LoggerFactory

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class); // Add logger

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public long getCustomerCount() {
        return customerRepository.count();
    }

    public Customer getCustomerById(Long id) {
        // findById returns Optional, handle it appropriately
        return customerRepository.findById(id).orElse(null);
    }

    // Make sure @Transactional is present
    @Transactional // Rollback occurs automatically on runtime exceptions
    public String createCustomer(Customer customer) {
        // FIX: Remove internal try-catch, let exceptions propagate
        // try {
        log.debug("Attempting to save new customer: {}", customer);
        // Ensure cascade save works for Insurance. If Insurance ID is somehow set,
        // JPA might try to merge instead of persist, causing issues. Ensure it's null for new customers.
        if (customer.getInsurance() != null) {
            customer.getInsurance().setId(null); // Ensure insurance ID is null for cascade persist
        }
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully saved new customer with ID: {}", savedCustomer.getId());
        return null; // Return null on success
        // } catch (Exception e) {
        //     log.error("Error creating customer: {}", e.getMessage(), e); // Log the actual exception
        //     // Return a meaningful error message or re-throw a custom exception
        //     return "Error creating customer. See logs for details."; // Example error message
        // }
    }

    // Make sure @Transactional is present
    @Transactional // Rollback occurs automatically on runtime exceptions
    public String updateCustomer(Customer customer, Long id) {
        // FIX: Remove internal try-catch, let exceptions propagate
        // try {
        log.debug("Attempting to update customer with ID: {}", id);
        Optional<Customer> existingCustomerOpt = customerRepository.findById(id);
        if (existingCustomerOpt.isEmpty()) {
            log.warn("Customer not found with ID: {}", id);
            return "Customer not found with ID: " + id; // Return error string if not found
        }
        Customer oldCustomer = existingCustomerOpt.get();

        // --- Update fields ---
        oldCustomer.setName(customer.getName());
        oldCustomer.setAge(customer.getAge());
        oldCustomer.setGender(customer.getGender());
        // Consider checking for email uniqueness if email is changed
        if (!oldCustomer.getEmail().equals(customer.getEmail())) {
            // Add check here if email needs to be unique across customers
            log.debug("Customer email updated for ID: {}", id);
        }
        oldCustomer.setEmail(customer.getEmail());
        oldCustomer.setMedicalHistory(customer.getMedicalHistory());
        oldCustomer.setContactDetails(customer.getContactDetails());

        // --- Handle Insurance update ---
        if (customer.getInsurance() != null) {
            Insurance incomingInsurance = customer.getInsurance();
            if (oldCustomer.getInsurance() != null) {
                // Update existing insurance record
                log.debug("Updating existing insurance for customer ID: {}", id);
                Insurance existingInsurance = oldCustomer.getInsurance();
                existingInsurance.setProvider(incomingInsurance.getProvider());
                existingInsurance.setPolicyNumber(incomingInsurance.getPolicyNumber());
                existingInsurance.setCoverageDetails(incomingInsurance.getCoverageDetails());
                existingInsurance.setExpiryDate(incomingInsurance.getExpiryDate());
            } else {
                // Add new insurance record (cascade should handle save)
                log.debug("Adding new insurance for customer ID: {}", id);
                // Ensure ID is null for cascade persist to work correctly
                incomingInsurance.setId(null);
                oldCustomer.setInsurance(incomingInsurance);
            }
        } else {
            // If incoming customer has no insurance, remove existing one if present
            if (oldCustomer.getInsurance() != null) {
                log.debug("Removing insurance for customer ID: {}", id);
                // Depending on cascade/orphanRemoval settings, setting to null might be enough.
                // Or you might need to explicitly delete the insurance record if not using orphanRemoval.
                oldCustomer.setInsurance(null); // Assuming cascade settings handle removal or orphan removal is true
            }
        }

        Customer updatedCustomer = customerRepository.save(oldCustomer); // Save the updated oldCustomer
        log.info("Successfully updated customer with ID: {}", updatedCustomer.getId());
        return null; // Return null on success
        // } catch (Exception e) {
        //    log.error("Error updating customer with ID {}: {}", id, e.getMessage(), e); // Log the actual exception
        //    return "Error updating customer. See logs for details."; // Example error message
        // }
    }

    @Transactional
    public String deleteCustomer(Long id) {
        // Keep try-catch here or let propagate - depends on desired controller handling
        try {
            if (!customerRepository.existsById(id)) {
                return "Customer not found with ID: " + id;
            }
            log.info("Deleting customer with ID: {}", id);
            // Consider implications: delete related tasks? Handled by DB constraints or requires logic here?
            customerRepository.deleteById(id);
            return null;
        } catch (Exception e) {
            log.error("Error deleting customer {}: {}", id, e.getMessage(), e);
            // Example: Handle potential DataIntegrityViolationException if customer is referenced elsewhere
            return "Error deleting customer: " + e.getMessage();
        }
    }
}