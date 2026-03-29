package com.hospital.doctorservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "specialization", nullable = false)
    private String specialization;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "qualification", nullable = false)
    private String qualification;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "consultation_fee", nullable = false)
    private Double consultationFee;
    
    @Column(name = "availability", columnDefinition = "TEXT")
    private String availability; // JSON string or description
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "languages")
    private String languages; // Comma separated list
    
    @Column(name = "license_number")
    private String licenseNumber;
    
    @Column(name = "license_expiry_date")
    private java.time.LocalDate licenseExpiryDate;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "rating")
    private Double rating = 0.0;
    
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;
    
    @Column(name = "total_appointments")
    private Integer totalAppointments = 0;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
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
    
    public void addRating(Integer newRating) {
        if (newRating != null && newRating >= 1 && newRating <= 5) {
            double totalScore = this.rating * this.totalRatings;
            this.totalRatings++;
            totalScore += newRating;
            this.rating = totalScore / this.totalRatings;
        }
    }
    
    public void incrementAppointments() {
        this.totalAppointments = (this.totalAppointments == null) ? 1 : this.totalAppointments + 1;
    }
    
    public boolean isLicenseValid() {
        if (licenseExpiryDate == null) {
            return true; // Assume valid if no expiry date
        }
        return !licenseExpiryDate.isBefore(java.time.LocalDate.now());
    }
}