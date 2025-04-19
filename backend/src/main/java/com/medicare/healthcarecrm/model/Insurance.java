package com.medicare.healthcarecrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import validation constraints
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
@Table(name = "insurance")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Insurance provider cannot be empty")
    @Size(max = 100, message = "Provider name is too long (max 100 chars)")
    @Column(nullable = false)
    private String provider;

    @NotEmpty(message = "Policy number cannot be empty")
    @Size(max = 50, message = "Policy number is too long (max 50 chars)")
    @Column(nullable = false)
    private String policyNumber;

    @NotEmpty(message = "Coverage details cannot be empty")
    @Size(max = 2000, message = "Coverage details are too long (max 2000 chars)")
    @Column(nullable = false, length = 2000) // Adjust DB length if needed
    private String coverageDetails;

    @NotNull(message = "Expiry date cannot be null")
    @FutureOrPresent(message = "Expiry date must be in the present or future") // Common validation for expiry
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime expiryDate;

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