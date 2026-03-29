package com.hospital.doctorservice.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_leaves")
public class DoctorLeave {
    
    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
    
    public enum LeaveType {
        SICK_LEAVE, CASUAL_LEAVE, EARNED_LEAVE, MATERNITY_LEAVE, PATERNITY_LEAVE, 
        STUDY_LEAVE, EMERGENCY_LEAVE, OTHER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "number_of_days")
    private Double numberOfDays;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;
    
    @Column(name = "approved_by")
    private Long approvedBy; // Admin/HR user ID
    
    @Column(name = "approval_date")
    private LocalDate approvalDate;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "is_half_day")
    private Boolean isHalfDay = false;
    
    @Column(name = "half_day_type") // FIRST_HALF, SECOND_HALF
    private String halfDayType;
    
    @Column(name = "contact_during_leave")
    private String contactDuringLeave;
    
    @Column(name = "handover_doctor_id")
    private Long handoverDoctorId;
    
    @Column(name = "handover_notes", columnDefinition = "TEXT")
    private String handoverNotes;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();
    
    @PrePersist
    @PreUpdate
    public void calculateNumberOfDays() {
        if (startDate != null && endDate != null) {
            if (isHalfDay) {
                this.numberOfDays = 0.5;
            } else {
                // Calculate business days (excluding weekends)
                long days = 0;
                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
                    if (dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                        days++;
                    }
                    current = current.plusDays(1);
                }
                this.numberOfDays = (double) days;
            }
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    public boolean isLeaveOnDate(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    public boolean isApproved() {
        return status == LeaveStatus.APPROVED;
    }
    
    public boolean isPending() {
        return status == LeaveStatus.PENDING;
    }
}