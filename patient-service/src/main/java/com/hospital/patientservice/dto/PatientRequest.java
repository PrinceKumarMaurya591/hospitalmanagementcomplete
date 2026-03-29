package com.hospital.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Emergency contact phone must be 10 digits")
    private String emergencyContactPhone;
    
    private String emergencyContactRelationship;
    
    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$", message = "Invalid blood group")
    private String bloodGroup;
    
    private JsonNode medicalHistory;
    
    @Size(max = 1000, message = "Allergies cannot exceed 1000 characters")
    private String allergies;
    
    @Size(max = 1000, message = "Current medications cannot exceed 1000 characters")
    private String currentMedications;
    
    private String insuranceProvider;
    
    private String insurancePolicyNumber;
    
    private Boolean isActive = true;
}