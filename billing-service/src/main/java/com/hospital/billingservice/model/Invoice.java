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
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "bill_id", nullable = false)
    private UUID billId;
    
    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "generated_by")
    private UUID generatedBy;
    
    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "download_count", nullable = false)
    private Integer downloadCount;
    
    @Column(name = "is_emailed", nullable = false)
    private Boolean isEmailed;
    
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
}