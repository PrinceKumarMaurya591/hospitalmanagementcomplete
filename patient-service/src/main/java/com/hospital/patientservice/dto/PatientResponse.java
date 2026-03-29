package com.hospital.patientservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String bloodGroup;
    private JsonNode medicalHistory;
    private String allergies;
    private String currentMedications;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Computed fields
    private String fullName;
    private Integer age;
    
    public PatientResponse(Long id, String firstName, String lastName, LocalDate dateOfBirth, 
                          String gender, String email, String phone, String address,
                          String emergencyContactName, String emergencyContactPhone,
                          String emergencyContactRelationship, String bloodGroup,
                          JsonNode medicalHistory, String allergies, String currentMedications,
                          String insuranceProvider, String insurancePolicyNumber,
                          Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.emergencyContactRelationship = emergencyContactRelationship;
        this.bloodGroup = bloodGroup;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.currentMedications = currentMedications;
        this.insuranceProvider = insuranceProvider;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
        // Compute derived fields
        this.fullName = firstName + " " + lastName;
        if (dateOfBirth != null) {
            this.age = LocalDate.now().getYear() - dateOfBirth.getYear();
        }
    }
}