package com.hospital.patientservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vital_signs")
public class VitalSign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", nullable = false)
    private Long patientId;
    
    @Column(name = "temperature")
    private Double temperature; // in Celsius
    
    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;
    
    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;
    
    @Column(name = "heart_rate")
    private Integer heartRate; // beats per minute
    
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate; // breaths per minute
    
    @Column(name = "oxygen_saturation")
    private Double oxygenSaturation; // percentage
    
    @Column(name = "height")
    private Double height; // in cm
    
    @Column(name = "weight")
    private Double weight; // in kg
    
    @Column(name = "bmi")
    private Double bmi; // Body Mass Index
    
    @Column(name = "blood_sugar")
    private Double bloodSugar; // mg/dL
    
    @Column(name = "pain_level")
    private Integer painLevel; // 0-10 scale
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "recorded_by")
    private String recordedBy; // doctor/nurse name or ID
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();
    
    @PrePersist
    @PreUpdate
    public void calculateBMI() {
        if (height != null && height > 0 && weight != null && weight > 0) {
            double heightInMeters = height / 100.0;
            this.bmi = weight / (heightInMeters * heightInMeters);
        }
    }
}