package com.hospital.appointmentservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private String appointmentType;
    private String status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}