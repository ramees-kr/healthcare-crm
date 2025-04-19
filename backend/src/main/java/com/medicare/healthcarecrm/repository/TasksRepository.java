package com.medicare.healthcarecrm.repository;

import com.medicare.healthcarecrm.model.Employee; // Import Employee
import com.medicare.healthcarecrm.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findByEmployee(Employee employee);

    /**
     * Counts the number of tasks with a specific status.
     * @param status The status to count tasks for (e.g., "Pending").
     * @return The number of tasks matching the status.
     */
    long countByStatus(String status);
}