package com.hospital.billingservice.dto;

import com.hospital.billingservice.model.Bill;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {
    
    @NotNull(message = "Appointment ID is required")
    private UUID appointmentId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private BigDecimal tax;
    
    private BigDecimal discount;
    
    private String description;
    
    private Boolean insuranceCovered;
    
    private BigDecimal insuranceAmount;
    
    private LocalDate dueDate;
}