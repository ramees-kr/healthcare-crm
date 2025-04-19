package com.medicare.healthcarecrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Task name cannot be empty")
    @Size(max = 150, message = "Task name is too long (max 150 chars)")
    @Column(nullable = false)
    private String taskName;

    @NotNull(message = "Task must be assigned to a customer")
    @ManyToOne
    @JoinColumn(name = "customer_details", nullable = false) // Keep DB constraint
    private Customer customer;

    @NotNull(message = "Task must be assigned to an employee")
    @ManyToOne
    @JoinColumn(name = "employee_details", nullable = false) // Keep DB constraint
    private Employee employee;

    @NotNull(message = "Due date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime dueDate;

    @NotEmpty(message = "Priority cannot be empty")
    @Column(nullable = false)
    private String priority;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 2000, message = "Description is too long (max 2000 chars)")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotEmpty(message = "Status cannot be empty")
    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Status could also be defaulted here if not set
        if (this.status == null || this.status.isEmpty()) {
            this.status = "Pending";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}