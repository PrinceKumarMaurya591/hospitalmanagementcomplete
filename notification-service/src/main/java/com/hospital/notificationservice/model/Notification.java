package com.hospital.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
    
    @Column(name = "email_sent")
    private Boolean emailSent;
    
    @Column(name = "sms_sent")
    private Boolean smsSent;
    
    @Column(name = "in_app_sent")
    private Boolean inAppSent;
    
    @Column(name = "email_address")
    private String emailAddress;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    public enum NotificationType {
        APPOINTMENT_CONFIRMATION,
        APPOINTMENT_REMINDER,
        APPOINTMENT_CANCELLATION,
        BILL_GENERATED,
        PAYMENT_CONFIRMATION,
        PAYMENT_REMINDER,
        WELCOME,
        PASSWORD_RESET,
        SECURITY_ALERT,
        SYSTEM_ANNOUNCEMENT
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        READ,
        DELETED
    }
}