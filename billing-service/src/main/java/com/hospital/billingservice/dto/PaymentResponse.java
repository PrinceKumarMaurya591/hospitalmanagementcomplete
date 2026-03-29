package com.hospital.billingservice.dto;

import com.hospital.billingservice.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private UUID id;
    private UUID billId;
    private UUID patientId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private Payment.PaymentStatus status;
    private String remarks;
    private String gatewayResponse;
    private LocalDateTime createdAt;
}