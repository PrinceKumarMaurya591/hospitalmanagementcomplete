package com.hospital.billingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillGeneratedEvent {
    
    private UUID billId;
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private String invoiceNumber;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate dueDate;
    private LocalDate generatedDate;
    private Boolean insuranceCovered;
    private BigDecimal insuranceAmount;
    private BigDecimal patientPayable;
}