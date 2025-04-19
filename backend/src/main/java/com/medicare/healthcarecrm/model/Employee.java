package com.medicare.healthcarecrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.medicare.healthcarecrm.validation.OnCreate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // These validations apply by default (Default group)
    @NotEmpty(message = "Employee name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Role cannot be empty")
    @Size(max = 50, message = "Role name is too long (max 50 chars)")
    @Column(nullable = false)
    private String role;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    // <<< CHANGE: Apply password validation ONLY for the OnCreate group >>>
    @NotEmpty(message = "Password is required for new employees", groups = OnCreate.class)
    @Size(min = 8, message = "Password must be at least 8 characters", groups = OnCreate.class)
    @Column(nullable = false) // DB constraint still applies
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}