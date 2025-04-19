package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Employee;
import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.TasksRepository;
import jakarta.persistence.EntityNotFoundException; // Import this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException; // Import this
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this
// Remove @PathVariable if not used elsewhere in this class
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TasksRepository tasksRepository;

    public List<Tasks> getAllTasks() {
        return tasksRepository.findAll();
    }

    // Add this method
    public List<Tasks> getTasksByEmployee(Employee employee) {
        if (employee == null) {
            return Collections.emptyList(); // Return empty list if employee is null
        }
        return tasksRepository.findByEmployee(employee);
    }

    public String createTask(Tasks tasks) {
        try {
            tasksRepository.save(tasks);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String updateTask(Tasks tasks, Long id) {
        // Consider adding checks to ensure the task exists before getting it
        Tasks oldTask = getTaskById(id);
        if (oldTask == null) {
            return "Task with ID " + id + " not found.";
        }
        oldTask.setTaskName(tasks.getTaskName());
        oldTask.setCustomer(tasks.getCustomer());
        oldTask.setEmployee(tasks.getEmployee());
        oldTask.setDueDate(tasks.getDueDate());
        oldTask.setPriority(tasks.getPriority());
        oldTask.setDescription(tasks.getDescription());
        oldTask.setStatus(tasks.getStatus());

        try {
            tasksRepository.save(oldTask);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Tasks getTaskById(Long id) {
        // Use orElse(null) or orElseThrow() for better handling when task not found
        return tasksRepository.findById(id).orElse(null);
    }

    public String deleteTask(@PathVariable Long id) {
        try {
            if (!tasksRepository.existsById(id)) {
                return "Task with ID " + id + " not found.";
            }
            tasksRepository.deleteById(id);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
     * Updates the status of a specific task, ensuring the requesting employee is assigned to it.
     *
     * @param taskId         The ID of the task to update.
     * @param newStatus      The new status to set.
     * @param currentEmployee The employee attempting the update.
     * @return null on success, error message string on failure.
     * @throws EntityNotFoundException if task not found.
     * @throws AccessDeniedException if employee is not assigned to the task.
     */
    @Transactional // Make this transactional
    public String updateTaskStatus(Long taskId, String newStatus, Employee currentEmployee) {
        if (currentEmployee == null) {
            return "Error: Could not identify current employee.";
        }

        try {
            Tasks task = tasksRepository.findById(taskId)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

            // SECURITY CHECK: Ensure the current employee is assigned to this task
            if (!Objects.equals(task.getEmployee().getId(), currentEmployee.getId())) {
                throw new AccessDeniedException("You are not authorized to update this task.");
            }

            // TODO: Optional - Add validation for allowed status transitions if needed
            // (e.g., cannot go from Completed back to Pending)

            task.setStatus(newStatus);
            tasksRepository.save(task);
            return null; // Success

        } catch (EntityNotFoundException | AccessDeniedException e) {
            // Log the exception details if needed
            // log.error("Error updating task status for task {}: {}", taskId, e.getMessage());
            return e.getMessage(); // Return error message to controller
        } catch (Exception e) {
            // Log unexpected exceptions
            // log.error("Unexpected error updating task status for task {}: {}", taskId, e.getMessage(), e);
            return "An unexpected error occurred while updating the task status.";
        }
    }
}