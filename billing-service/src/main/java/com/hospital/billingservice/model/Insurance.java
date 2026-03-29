package com.hospital.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "insurances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    
    @Column(name = "insurance_provider", nullable = false)
    private String insuranceProvider;
    
    @Column(name = "policy_number", unique = true, nullable = false)
    private String policyNumber;
    
    @Column(name = "policy_holder_name", nullable = false)
    private String policyHolderName;
    
    @Column(name = "coverage_type", nullable = false)
    private String coverageType;
    
    @Column(name = "coverage_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal coverageAmount;
    
    @Column(name = "remaining_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal remainingAmount;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    
    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "terms_and_conditions")
    private String termsAndConditions;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}