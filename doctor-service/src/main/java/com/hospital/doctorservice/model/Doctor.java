package com.hospital.doctorservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Doctor Entity
 * 
 * Represents a medical doctor in the hospital management system.
 * This entity stores comprehensive information about doctors including their
 * personal details, professional qualifications, availability, and performance metrics.
 * 
 * Key Features:
 * - Stores doctor's personal and professional information
 * - Tracks availability and scheduling preferences
 * - Maintains performance metrics (ratings, appointment counts)
 * - Validates medical license expiration
 * - Supports search optimization through database indexes
 * 
 * Database Indexes:
 * - idx_doctor_email: Unique index on email for fast lookups and uniqueness constraint
 * - idx_doctor_phone: Index on phone number for contact searches
 * - idx_doctor_name: Composite index on first and last name for name-based searches
 * - idx_doctor_specialization: Index on specialization for filtering by medical specialty
 * - idx_doctor_department: Index on department for organizational filtering
 * - idx_doctor_active: Index on active status for filtering active/inactive doctors
 * 
 * @author Hospital Management System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctors", indexes = {
    @Index(name = "idx_doctor_email", columnList = "email", unique = true),
    @Index(name = "idx_doctor_phone", columnList = "phone"),
    @Index(name = "idx_doctor_name", columnList = "firstName, lastName"),
    @Index(name = "idx_doctor_specialization", columnList = "specialization"),
    @Index(name = "idx_doctor_department", columnList = "department"),
    @Index(name = "idx_doctor_active", columnList = "isActive")
})
public class Doctor {
    
    /**
     * Unique identifier for the doctor.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Doctor's first name.
     * Required field for identification.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    /**
     * Doctor's last name.
     * Required field for identification.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    /**
     * Medical specialization of the doctor (e.g., "Cardiology", "Pediatrics", "Orthopedics").
     * Required field that determines the doctor's medical expertise area.
     */
    @Column(name = "specialization", nullable = false)
    private String specialization;
    
    /**
     * Department where the doctor works within the hospital.
     * Optional field for organizational structure.
     */
    @Column(name = "department")
    private String department;
    
    /**
     * Professional qualifications and degrees of the doctor.
     * Required field listing medical degrees and certifications.
     */
    @Column(name = "qualification", nullable = false)
    private String qualification;
    
    /**
     * Number of years of professional medical experience.
     * Optional field for experience tracking.
     */
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    /**
     * Unique email address of the doctor for communication.
     * Required field with uniqueness constraint for login and notifications.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    /**
     * Contact phone number of the doctor.
     * Required field for emergency and appointment communications.
     */
    @Column(name = "phone", nullable = false)
    private String phone;
    
    /**
     * Consultation fee charged by the doctor per appointment.
     * Required field for billing and financial calculations.
     */
    @Column(name = "consultation_fee", nullable = false)
    private Double consultationFee;
    
    /**
     * Availability schedule of the doctor.
     * Stores JSON string or descriptive text about working hours and days.
     * Used for appointment scheduling and availability checks.
     */
    @Column(name = "availability", columnDefinition = "TEXT")
    private String availability; // JSON string or description
    
    /**
     * Professional biography and background of the doctor.
     * Provides detailed information about the doctor's expertise and experience.
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    /**
     * Languages spoken by the doctor.
     * Comma-separated list of languages (e.g., "English, Hindi, Spanish").
     * Useful for patient-doctor language matching.
     */
    @Column(name = "languages")
    private String languages; // Comma separated list
    
    /**
     * Medical license number issued by regulatory authority.
     * Required for legal compliance and verification.
     */
    @Column(name = "license_number")
    private String licenseNumber;
    
    /**
     * Expiration date of the medical license.
     * Used to validate license validity and send renewal reminders.
     */
    @Column(name = "license_expiry_date")
    private java.time.LocalDate licenseExpiryDate;
    
    /**
     * Current availability status for new appointments.
     * True if doctor is accepting new appointments, false otherwise.
     * Default: true (available).
     */
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    /**
     * Active status of the doctor in the system.
     * True if doctor is currently employed/active, false if inactive/left.
     * Default: true (active).
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    /**
     * Average rating of the doctor based on patient feedback.
     * Scale: 0.0 to 5.0 stars.
     * Default: 0.0 (no ratings yet).
     */
    @Column(name = "rating")
    private Double rating = 0.0;
    
    /**
     * Total number of ratings received by the doctor.
     * Used to calculate average rating.
     * Default: 0 (no ratings yet).
     */
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;
    
    /**
     * Total number of appointments handled by the doctor.
     * Tracks doctor's workload and experience.
     * Default: 0 (no appointments yet).
     */
    @Column(name = "total_appointments")
    private Integer totalAppointments = 0;
    
    /**
     * URL or path to the doctor's profile picture.
     * Used for display in patient portals and directories.
     */
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    /**
     * Timestamp when the doctor record was created in the system.
     * Automatically set to current date/time when record is created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Timestamp when the doctor record was last updated.
     * Automatically updated whenever changes are made to the record.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * Lifecycle callback method that executes before the entity is updated.
     * Updates the modification timestamp to the current date and time.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Returns the full name of the doctor by concatenating first and last names.
     * 
     * @return Full name string in "FirstName LastName" format
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Adds a new rating to the doctor's average rating calculation.
     * 
     * This method:
     * 1. Validates the new rating is between 1 and 5 (inclusive)
     * 2. Updates the total ratings count
     * 3. Recalculates the average rating using weighted average formula
     * 
     * @param newRating New rating value (1-5) to add
     */
    public void addRating(Integer newRating) {
        if (newRating != null && newRating >= 1 && newRating <= 5) {
            double totalScore = this.rating * this.totalRatings;
            this.totalRatings++;
            totalScore += newRating;
            this.rating = totalScore / this.totalRatings;
        }
    }
    
    /**
     * Increments the total appointments count by 1.
     * 
     * This method is called whenever the doctor completes a new appointment.
     * Handles null initialization by setting count to 1 if previously null.
     */
    public void incrementAppointments() {
        this.totalAppointments = (this.totalAppointments == null) ? 1 : this.totalAppointments + 1;
    }
    
    /**
     * Checks if the doctor's medical license is currently valid.
     * 
     * A license is considered valid if:
     * 1. No expiry date is set (assumed valid)
     * 2. The expiry date is today or in the future
     * 
     * @return true if license is valid, false if expired
     */
    public boolean isLicenseValid() {
        if (licenseExpiryDate == null) {
            return true; // Assume valid if no expiry date
        }
        return !licenseExpiryDate.isBefore(java.time.LocalDate.now());
    }
}
