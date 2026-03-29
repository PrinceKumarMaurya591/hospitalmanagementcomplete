package com.hospital.patientservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_history")
public class MedicalHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", nullable = false)
    private Long patientId;
    
    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;
    
    @Column(name = "treatment")
    private String treatment;
    
    @Column(name = "doctor_name")
    private String doctorName;
    
    @Column(name = "hospital_name")
    private String hospitalName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_diagnosis")
    private LocalDate dateOfDiagnosis;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_recovery")
    private LocalDate dateOfRecovery;
    
    @Column(name = "is_chronic")
    private Boolean isChronic = false;
    
    @Column(name = "is_allergy")
    private Boolean isAllergy = false;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;
    
    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();
    
    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();
}