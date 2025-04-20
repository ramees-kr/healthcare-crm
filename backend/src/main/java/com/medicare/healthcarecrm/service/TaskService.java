package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.TasksRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger; // Add Logger
import org.slf4j.LoggerFactory; // Add LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Remove unused import
// import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime; // Add LocalDateTime import
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    // Add Logger
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TasksRepository tasksRepository;

    public List<Tasks> getAllTasks() {
        return tasksRepository.findAll();
    }

    public long getTaskCountByStatus(String status) {
        return tasksRepository.countByStatus(status);
    }

    public List<Tasks> getTasksByEmployee(Employee employee) {
        if (employee == null) {
            return Collections.emptyList();
        }
        return tasksRepository.findByEmployee(employee);
    }

    @Transactional
    public String createTask(Tasks tasks) {
        try {
            if (tasks.getStatus() == null || tasks.getStatus().isEmpty()) {
                tasks.setStatus("Pending"); // Example default
            }
            tasksRepository.save(tasks);
            log.info("Task created successfully with ID: {}", tasks.getId());
            return null;
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage(), e);
            return "Error creating task: " + e.getMessage();
        }
    }
    @Transactional
    public String updateTask(Tasks tasks, Long id) {
        try {
            Optional<Tasks> existingTaskOpt = tasksRepository.findById(id);
            if (existingTaskOpt.isEmpty()) {
                log.warn("Task not found for update with ID: {}", id);
                return "Task not found with ID: " + id;
            }
            Tasks oldTask = existingTaskOpt.get();

            // Update fields from incoming task object
            oldTask.setTaskName(tasks.getTaskName());
            oldTask.setCustomer(tasks.getCustomer()); // Assuming Customer object is correctly bound or fetched
            oldTask.setEmployee(tasks.getEmployee()); // Assuming Employee object is correctly bound or fetched
            oldTask.setDueDate(tasks.getDueDate());
            oldTask.setPriority(tasks.getPriority());
            oldTask.setDescription(tasks.getDescription());
            oldTask.setStatus(tasks.getStatus());

            tasksRepository.save(oldTask);
            log.info("Task updated successfully with ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error updating task with ID {}: {}", id, e.getMessage(), e);
            return "Error updating task: " + e.getMessage();
        }
    }

    public Tasks getTaskById(Long id) {
        return tasksRepository.findById(id).orElse(null);
    }

    @Transactional
    public String deleteTask(Long id) {
        try {
            if (!tasksRepository.existsById(id)) {
                log.warn("Attempted to delete non-existent task with ID: {}", id);
                return "Task with ID " + id + " not found.";
            }
            tasksRepository.deleteById(id);
            log.info("Task deleted successfully with ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error deleting task with ID {}: {}", id, e.getMessage(), e);
            return "Error deleting task: " + e.getMessage();
        }
    }

    @Transactional // Make this transactional
    public String updateTaskStatus(Long taskId, String newStatus, Employee currentEmployee) {
        if (currentEmployee == null) {
            log.warn("Attempted task status update without valid employee context for task ID: {}", taskId);
            return "Error: Could not identify current employee.";
        }

        try {
            Tasks task = tasksRepository.findById(taskId)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

            // SECURITY CHECK: Ensure the current employee is assigned to this task
            if (task.getEmployee() == null || !Objects.equals(task.getEmployee().getId(), currentEmployee.getId())) {
                log.warn("Unauthorized attempt to update task status for task {} by employee {}", taskId, currentEmployee.getEmail());
                throw new AccessDeniedException("You are not authorized to update this task.");
            }

            // Optional: Add validation for allowed status transitions if needed
            // (e.g., cannot go from Completed back to Pending without specific logic)

            task.setStatus(newStatus);
            tasksRepository.save(task); // Save the updated task
            log.info("Task {} status updated to '{}' by employee {}", taskId, newStatus, currentEmployee.getEmail());
            return null; // Success

        } catch (EntityNotFoundException e) {
            log.error("Error updating task status: Task {} not found.", taskId);
            return e.getMessage(); // Return specific error message
        } catch (AccessDeniedException e) {
            // Already logged above where it was thrown
            return e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error updating task status for task {}: {}", taskId, e.getMessage(), e);
            return "An unexpected error occurred while updating the task status.";
        }
    }

    /**
     * Gets a list of tasks that are overdue.
     * Overdue means the due date has passed and the status is not 'Completed'.
     * @return List of overdue Tasks.
     */
    public List<Tasks> getOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Fetching overdue tasks (due before {})", now);
        return tasksRepository.findOverdueTasks(now);
    }

    /**
     * Gets a list of tasks due within the next specified number of days.
     * Only includes tasks whose status is not 'Completed'.
     * @param days Number of days into the future to check (e.g., 7 for the next week).
     * @return List of Tasks due soon.
     */
    public List<Tasks> getTasksDueSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        log.debug("Fetching tasks due soon (between {} and {})", now, futureDate);
        return tasksRepository.findTasksDueSoon(now, futureDate);
    }
}