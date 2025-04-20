package com.medicare.healthcarecrm.repository;

import com.medicare.healthcarecrm.model.Employee; // Import Employee
import com.medicare.healthcarecrm.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * Finds tasks that are overdue (due date is before the current time and status is not 'Completed').
     * @param now The current date and time.
     * @return A list of overdue tasks.
     */
    @Query("SELECT t FROM Tasks t WHERE t.dueDate < :now AND t.status <> 'Completed'")
    List<Tasks> findOverdueTasks(@Param("now") LocalDateTime now);

    /**
     * Finds tasks that are due soon (due date is between the current time and a future date, and status is not 'Completed').
     * @param now The current date and time.
     * @param futureDate The future date limit (e.g., 7 days from now).
     * @return A list of tasks due soon.
     */
    @Query("SELECT t FROM Tasks t WHERE t.dueDate >= :now AND t.dueDate <= :futureDate AND t.status <> 'Completed'")
    List<Tasks> findTasksDueSoon(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);
}