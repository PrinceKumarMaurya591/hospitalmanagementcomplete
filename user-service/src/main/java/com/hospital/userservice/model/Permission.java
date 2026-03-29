package com.hospital.userservice.model;

public enum Permission {
    // User permissions
    USER_READ,
    USER_WRITE,
    USER_DELETE,
    
    // Patient permissions
    PATIENT_READ,
    PATIENT_WRITE,
    PATIENT_DELETE,
    
    // Doctor permissions
    DOCTOR_READ,
    DOCTOR_WRITE,
    DOCTOR_DELETE,
    
    // Appointment permissions
    APPOINTMENT_READ,
    APPOINTMENT_WRITE,
    APPOINTMENT_DELETE,
    
    // Billing permissions
    BILLING_READ,
    BILLING_WRITE,
    BILLING_DELETE,
    
    // Admin permissions
    ADMIN_ACCESS,
    SYSTEM_CONFIG
}