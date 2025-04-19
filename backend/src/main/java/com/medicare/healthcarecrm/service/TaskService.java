package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.TasksRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

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
            return null;
        } catch (Exception e) {
            return "Error creating task: " + e.getMessage();
        }
    }
    @Transactional
    public String updateTask(Tasks tasks, Long id) {
        try {
            Optional<Tasks> existingTaskOpt = tasksRepository.findById(id);
            if (existingTaskOpt.isEmpty()) {
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
            return null;
        } catch (Exception e) {
            // Log exception
            return "Error updating task: " + e.getMessage();
        }
    }

    public Tasks getTaskById(Long id) {
        // Use orElse(null) or orElseThrow() for better handling when task not found
        return tasksRepository.findById(id).orElse(null);
    }

    @Transactional
    public String deleteTask(Long id) {
        try {
            if (!tasksRepository.existsById(id)) {
                return "Task with ID " + id + " not found.";
            }
            tasksRepository.deleteById(id);
            return null;
        } catch (Exception e) {
            // Log exception
            return "Error deleting task: " + e.getMessage();
        }
    }

    @Transactional // Make this transactional
    public String updateTaskStatus(Long taskId, String newStatus, Employee currentEmployee) {
        if (currentEmployee == null) {
            // This should ideally be caught earlier by security context
            return "Error: Could not identify current employee.";
        }

        try {
            // Use orElseThrow for cleaner not-found handling
            Tasks task = tasksRepository.findById(taskId)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

            // SECURITY CHECK: Ensure the current employee is assigned to this task
            if (task.getEmployee() == null || !Objects.equals(task.getEmployee().getId(), currentEmployee.getId())) {
                throw new AccessDeniedException("You are not authorized to update this task.");
            }

            // Add validation for allowed status transitions if needed
            // (e.g., cannot go from Completed back to Pending without specific logic)

            task.setStatus(newStatus);
            tasksRepository.save(task); // Save the updated task
            return null; // Success

        } catch (EntityNotFoundException | AccessDeniedException e) {
            // Log the exception details if needed (use SLF4J logger)
            // log.error("Error updating task status for task {}: {}", taskId, e.getMessage());
            return e.getMessage(); // Return specific error message
        } catch (Exception e) {
            // Log unexpected exceptions
            // log.error("Unexpected error updating task status for task {}: {}", taskId, e.getMessage(), e);
            return "An unexpected error occurred while updating the task status.";
        }
    }
}