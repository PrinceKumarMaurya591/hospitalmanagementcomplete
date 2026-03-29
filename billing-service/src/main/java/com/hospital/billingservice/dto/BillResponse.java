package com.hospital.billingservice.dto;

import com.hospital.billingservice.model.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    
    private UUID id;
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private String invoiceNumber;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private Bill.BillStatus status;
    private LocalDate dueDate;
    private LocalDate generatedDate;
    private String description;
    private Boolean insuranceCovered;
    private BigDecimal insuranceAmount;
    private BigDecimal patientPayable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}