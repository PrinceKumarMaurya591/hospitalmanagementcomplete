package com.hospital.patientservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_email", columnList = "email", unique = true),
    @Index(name = "idx_patient_phone", columnList = "phone"),
    @Index(name = "idx_patient_name", columnList = "firstName, lastName")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(name = "gender", nullable = false)
    private String gender; // MALE, FEMALE, OTHER
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relationship")
    private String emergencyContactRelationship;
    
    @Column(name = "blood_group")
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    
    @Type(JsonBinaryType.class)
    @Column(name = "medical_history", columnDefinition = "jsonb")
    private JsonNode medicalHistory;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;
    
    @Column(name = "insurance_provider")
    private String insuranceProvider;
    
    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}