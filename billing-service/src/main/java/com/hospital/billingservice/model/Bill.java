package com.hospital.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;
    
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    
    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "tax", precision = 10, scale = 2)
    private BigDecimal tax;
    
    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "insurance_covered", nullable = false)
    private Boolean insuranceCovered;
    
    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount;
    
    @Column(name = "patient_payable", precision = 10, scale = 2)
    private BigDecimal patientPayable;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum BillStatus {
        PENDING,
        GENERATED,
        PARTIALLY_PAID,
        PAID,
        OVERDUE,
        CANCELLED,
        REFUNDED
    }
}