package com.hospital.doctorservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"})
})
public class DoctorSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;
    
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "is_working_day")
    private Boolean isWorkingDay = true;
    
    @Column(name = "max_appointments_per_day")
    private Integer maxAppointmentsPerDay = 20;
    
    @Column(name = "appointment_duration_minutes")
    private Integer appointmentDurationMinutes = 30;
    
    @Column(name = "break_start_time")
    private LocalTime breakStartTime;
    
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "break_end_time")
    private LocalTime breakEndTime;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    public boolean isTimeWithinSchedule(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
    
    public boolean isTimeDuringBreak(LocalTime time) {
        if (breakStartTime == null || breakEndTime == null) {
            return false;
        }
        return !time.isBefore(breakStartTime) && !time.isAfter(breakEndTime);
    }
    
    public boolean isAvailableAt(LocalTime time) {
        return isWorkingDay && isTimeWithinSchedule(time) && !isTimeDuringBreak(time);
    }
}