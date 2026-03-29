package com.hospital.appointmentservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Appointment Entity
 * 
 * Represents a medical appointment between a patient and a doctor in the hospital management system.
 * This entity is mapped to the "appointments" table in the database and contains all information
 * related to scheduling, status, and details of medical appointments.
 * 
 * Key Features:
 * - Tracks patient-doctor appointments with date/time scheduling
 * - Supports different appointment types (consultation, follow-up, emergency, etc.)
 * - Maintains appointment status (scheduled, confirmed, completed, cancelled, etc.)
 * - Includes notes and reasons for the appointment
 * - Automatically tracks creation and update timestamps
 * 
 * @author Hospital Management System
 * @version 1.0
 */
@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    /**
     * Unique identifier for the appointment.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID of the patient who booked the appointment.
     * References the patient in the patient service.
     */
    @Column(nullable = false)
    private Long patientId;
    
    /**
     * ID of the doctor assigned to the appointment.
     * References the doctor in the doctor service.
     */
    @Column(nullable = false)
    private Long doctorId;
    
    /**
     * Date and time when the appointment is scheduled.
     * Format: LocalDateTime (YYYY-MM-DDTHH:MM:SS)
     */
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;
    
    /**
     * Type of appointment (e.g., "CONSULTATION", "FOLLOW_UP", "EMERGENCY", "CHECKUP").
     * Determines the nature and priority of the medical visit.
     */
    @Column(nullable = false)
    private String appointmentType;
    
    /**
     * Current status of the appointment.
     * Possible values: "SCHEDULED", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "NO_SHOW"
     */
    @Column(nullable = false)
    private String status;
    
    /**
     * Reason for the appointment or medical complaint.
     * Describes why the patient is seeking medical attention.
     */
    @Column
    private String reason;
    
    /**
     * Additional notes or observations about the appointment.
     * Can include pre-appointment instructions or special requirements.
     */
    @Column
    private String notes;
    
    /**
     * Timestamp when the appointment record was created.
     * Automatically set when the appointment is first saved to the database.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the appointment record was last updated.
     * Automatically updated whenever changes are made to the appointment.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Lifecycle callback method that executes before the entity is persisted (inserted).
     * Sets the creation and update timestamps to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Lifecycle callback method that executes before the entity is updated.
     * Updates the modification timestamp to the current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
