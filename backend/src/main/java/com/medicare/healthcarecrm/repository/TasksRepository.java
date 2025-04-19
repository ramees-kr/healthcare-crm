package com.medicare.healthcarecrm.repository;

import com.medicare.healthcarecrm.model.Employee; // Import Employee
import com.medicare.healthcarecrm.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    // Add this method to find tasks by employee
    List<Tasks> findByEmployee(Employee employee);
}