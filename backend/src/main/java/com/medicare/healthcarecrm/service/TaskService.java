package com.medicare.healthcarecrm.service;

import com.medicare.healthcarecrm.model.Tasks;
import com.medicare.healthcarecrm.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TasksRepository tasksRepository;

    public List<Tasks> getAllTasks() {
        return tasksRepository.findAll();
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
        Tasks oldTask = getTaskById(id);
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
        return tasksRepository.findById(id).get();
    }

    public String deleteTask(@PathVariable Long id) {
        try {
            tasksRepository.deleteById(id);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}