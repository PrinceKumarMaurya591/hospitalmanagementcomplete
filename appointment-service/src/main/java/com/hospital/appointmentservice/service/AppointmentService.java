package com.hospital.appointmentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.appointmentservice.dto.AppointmentEvent;
import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.exception.ResourceNotFoundException;
import com.hospital.appointmentservice.kafka.AppointmentEventProducer;
import com.hospital.appointmentservice.mapper.AppointmentMapper;
import com.hospital.appointmentservice.model.Appointment;
import com.hospital.appointmentservice.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Appointment Service
 * 
 * Core business service for managing medical appointments in the hospital management system.
 * This service handles all appointment-related operations including creation, retrieval,
 * updates, deletion, and status management. It integrates with Kafka for event-driven
 * architecture to notify other services about appointment changes.
 * 
 * Key Responsibilities:
 * - Create new appointments with validation
 * - Retrieve appointments by various criteria (ID, patient, doctor, date range, status)
 * - Update appointment details and status
 * - Delete appointments with proper event notification
 * - Send Kafka events for appointment lifecycle changes
 * - Ensure transactional consistency for all operations
 * 
 * @author Hospital Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentEventProducer appointmentEventProducer;
    
    /**
     * Creates a new appointment in the system.
     * 
     * This method performs the following operations:
     * 1. Converts the AppointmentRequest DTO to an Appointment entity
     * 2. Saves the appointment to the database
     * 3. Sends a Kafka event to notify other services about the appointment creation
     * 4. Returns the created appointment as an AppointmentResponse DTO
     * 
     * @param request AppointmentRequest containing appointment details (patientId, doctorId, date/time, type, etc.)
     * @return AppointmentResponse with the created appointment details including generated ID
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment for patient: {}, doctor: {}", 
                request.getPatientId(), request.getDoctorId());
        
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment = appointmentRepository.save(appointment);
        
        log.info("Appointment created with ID: {}", appointment.getId());
        
        // Send Kafka event to notify other services about appointment creation
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_CREATED");
        appointmentEventProducer.sendAppointmentCreatedEvent(event);
        
        return response;
    }
    
    /**
     * Retrieves a specific appointment by its unique ID.
     * 
     * This method fetches an appointment from the database using its ID.
     * If no appointment is found with the given ID, a ResourceNotFoundException is thrown.
     * 
     * @param id Unique identifier of the appointment to retrieve
     * @return AppointmentResponse containing the appointment details
     * @throws ResourceNotFoundException if no appointment exists with the given ID
     */
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        log.info("Fetching appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        return appointmentMapper.toResponse(appointment);
    }
    
    /**
     * Retrieves all appointments for a specific patient.
     * 
     * This method fetches all appointments associated with a given patient ID.
     * Useful for displaying a patient's appointment history or upcoming appointments.
     * 
     * @param patientId ID of the patient whose appointments to retrieve
     * @return List of AppointmentResponse objects for the patient's appointments
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        log.info("Fetching appointments for patient ID: {}", patientId);
        
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all appointments for a specific doctor.
     * 
     * This method fetches all appointments associated with a given doctor ID.
     * Useful for displaying a doctor's schedule or appointment calendar.
     * 
     * @param doctorId ID of the doctor whose appointments to retrieve
     * @return List of AppointmentResponse objects for the doctor's appointments
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        log.info("Fetching appointments for doctor ID: {}", doctorId);
        
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all appointments in the system.
     * 
     * This method fetches all appointments from the database.
     * Typically used by administrators for system-wide appointment management.
     * 
     * @return List of all AppointmentResponse objects in the system
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        log.info("Fetching all appointments");
        
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Updates an existing appointment with new details.
     * 
     * This method performs the following operations:
     * 1. Finds the existing appointment by ID
     * 2. Updates all modifiable fields with new values from the request
     * 3. Saves the updated appointment
     * 4. Sends a Kafka event to notify other services about the update
     * 
     * Note: This method does not update the appointment status. Use updateAppointmentStatus() for status changes.
     * 
     * @param id ID of the appointment to update
     * @param request AppointmentRequest containing updated appointment details
     * @return AppointmentResponse with the updated appointment details
     * @throws ResourceNotFoundException if no appointment exists with the given ID
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        log.info("Updating appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        
        appointment = appointmentRepository.save(appointment);
        log.info("Appointment updated with ID: {}", appointment.getId());
        
        // Send Kafka event to notify other services about appointment update
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_UPDATED");
        appointmentEventProducer.sendAppointmentUpdatedEvent(event);
        
        return response;
    }
    
    /**
     * Updates the status of an existing appointment.
     * 
     * This method is specifically designed for changing appointment status.
     * Common status values include: "SCHEDULED", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "NO_SHOW"
     * 
     * @param id ID of the appointment to update
     * @param status New status to set for the appointment
     * @return AppointmentResponse with the updated appointment details
     * @throws ResourceNotFoundException if no appointment exists with the given ID
     */
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, String status) {
        log.info("Updating appointment status to {} for ID: {}", status, id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);
        
        log.info("Appointment status updated for ID: {}", id);
        
        // Send Kafka event for status update notification
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_STATUS_UPDATED");
        appointmentEventProducer.sendAppointmentUpdatedEvent(event);
        
        return response;
    }
    
    /**
     * Deletes an appointment from the system.
     * 
     * This method performs the following operations:
     * 1. Finds the appointment by ID
     * 2. Sends a Kafka event to notify other services about the cancellation
     * 3. Deletes the appointment from the database
     * 
     * Note: The Kafka event is sent before deletion to ensure other services
     * can process the cancellation before the appointment is removed.
     * 
     * @param id ID of the appointment to delete
     * @throws ResourceNotFoundException if no appointment exists with the given ID
     */
    @Transactional
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        // Send Kafka event before deletion to notify about cancellation
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_CANCELLED");
        appointmentEventProducer.sendAppointmentCancelledEvent(event);
        
        appointmentRepository.deleteById(id);
        log.info("Appointment deleted with ID: {}", id);
    }
    
    /**
     * Retrieves appointments within a specific date range.
     * 
     * This method is useful for:
     * - Generating daily/weekly/monthly appointment reports
     * - Displaying calendar views
     * - Finding available time slots
     * 
     * @param start Start date/time of the range (inclusive)
     * @param end End date/time of the range (inclusive)
     * @return List of AppointmentResponse objects within the specified date range
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching appointments between {} and {}", start, end);
        
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves appointments with a specific status.
     * 
     * This method is useful for:
     * - Finding all pending appointments
     * - Identifying completed appointments for billing
     * - Tracking cancelled appointments
     * 
     * @param status Status to filter appointments by (e.g., "SCHEDULED", "COMPLETED", "CANCELLED")
     * @return List of AppointmentResponse objects with the specified status
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(String status) {
        log.info("Fetching appointments with status: {}", status);
        
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
