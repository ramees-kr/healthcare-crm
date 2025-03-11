package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public String createCustomer(Customer customer) {
        try {
            customerRepository.save(customer);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String updateCustomer(Customer customer, Long id) {
        try {
            Customer oldCustomer = getCustomerById(id);
            oldCustomer.setName(customer.getName());
            oldCustomer.setAge(customer.getAge());
            oldCustomer.setGender(customer.getGender());
            oldCustomer.setEmail(customer.getEmail());
            oldCustomer.setMedicalHistory(customer.getMedicalHistory());
            oldCustomer.setContactDetails(customer.getContactDetails());
            oldCustomer.getInsurance().setProvider(customer.getInsurance().getProvider());
            oldCustomer.getInsurance().setPolicyNumber(customer.getInsurance().getPolicyNumber());
            oldCustomer.getInsurance().setCoverageDetails(customer.getInsurance().getCoverageDetails());
            oldCustomer.getInsurance().setExpiryDate(customer.getInsurance().getExpiryDate());
            customerRepository.save(customer);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).get();
    }

    public String deleteCustomer(Long id) {
        try {
            customerRepository.deleteById(id);
            return null;
        }catch (Exception e) {
            return e.getMessage();
        }
    }
}
