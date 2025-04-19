package com.medicare.healthcarecrm.model;

import jakarta.persistence.*;
import jakarta.validation.Valid; // Import @Valid
import jakarta.validation.constraints.*; // Import validation constraints
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Customer name cannot be empty") // Ensure name is not null and not empty string
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 120, message = "Age seems unrealistic") // Example upper bound
    @Column(nullable = false)
    private int age;

    @NotEmpty(message = "Gender cannot be empty")
    @Column(nullable = false)
    private String gender; // Consider Enum later if needed

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid") // Validate email format
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty(message = "Medical history cannot be empty")
    @Size(max = 5000, message = "Medical history is too long (max 5000 chars)") // Example size limit
    @Column(nullable = false, length = 5000) // Adjust DB length if needed
    private String medicalHistory;

    @NotEmpty(message = "Contact details cannot be empty")
    @Size(max = 100, message = "Contact details seem too long (max 100 chars)")
    @Column(nullable = false)
    private String contactDetails;

    @NotNull(message = "Insurance details must be provided")
    @Valid // <<< IMPORTANT: Enables validation of fields within the Insurance object
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "insurance_details", nullable = false)
    private Insurance insurance;

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