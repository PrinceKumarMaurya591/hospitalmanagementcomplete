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
public class PaymentProcessedEvent {
    
    private UUID paymentId;
    private UUID billId;
    private UUID patientId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String status;
    private String remarks;
}