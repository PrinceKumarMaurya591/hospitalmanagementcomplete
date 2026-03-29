package com.hospital.billingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCompletedEvent {
    
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String diagnosis;
    private String prescription;
    private BigDecimal consultationFee;
    private BigDecimal medicationFee;
    private BigDecimal testFee;
    private BigDecimal totalFee;
    private LocalDateTime completedAt;
}