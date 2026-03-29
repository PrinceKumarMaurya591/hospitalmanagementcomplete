package com.hospital.billingservice.dto;

import com.hospital.billingservice.model.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "Bill ID is required")
    private UUID billId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;
    
    private String transactionId;
    
    private String remarks;
}