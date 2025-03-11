package com.medicare.healthcarecrm.repository;

import com.medicare.healthcarecrm.model.Customer;
import com.medicare.healthcarecrm.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}