package com.hospital.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "bill_id", nullable = false)
    private UUID billId;
    
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "gateway_response")
    private String gatewayResponse;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NET_BANKING,
        INSURANCE,
        WALLET
    }
    
    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}