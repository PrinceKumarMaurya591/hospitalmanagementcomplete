package com.hospital.appointmentservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Appointment Controller
 * 
 * REST API controller for managing medical appointments in the hospital management system.
 * This controller exposes HTTP endpoints for all appointment-related operations and
 * delegates business logic to the AppointmentService. It handles request validation,
 * response formatting, and HTTP status code management.
 * 
 * API Base Path: /api/appointments
 * 
 * Key Features:
 * - RESTful endpoints for CRUD operations on appointments
 * - Input validation using Jakarta Validation API
 * - Proper HTTP status codes for different scenarios
 * - Support for filtering appointments by patient, doctor, date range, and status
 * - PATCH endpoint for partial updates (status changes)
 * 
 * @author Hospital Management System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    /**
     * Creates a new appointment.
     * 
     * Endpoint: POST /api/appointments
     * 
     * This endpoint accepts an AppointmentRequest in the request body, validates it,
     * creates a new appointment, and returns the created appointment with HTTP 201 Created status.
     * 
     * @param request AppointmentRequest containing appointment details (patientId, doctorId, date/time, type, etc.)
     * @return ResponseEntity containing the created AppointmentResponse with HTTP 201 Created status
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves a specific appointment by ID.
     * 
     * Endpoint: GET /api/appointments/{id}
     * 
     * This endpoint fetches an appointment by its unique identifier.
     * Returns HTTP 200 OK with the appointment details if found.
     * Returns HTTP 404 Not Found if no appointment exists with the given ID.
     * 
     * @param id Unique identifier of the appointment to retrieve
     * @return ResponseEntity containing the AppointmentResponse with HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all appointments for a specific patient.
     * 
     * Endpoint: GET /api/appointments/patient/{patientId}
     * 
     * This endpoint fetches all appointments associated with a given patient ID.
     * Useful for displaying a patient's appointment history or upcoming appointments.
     * 
     * @param patientId ID of the patient whose appointments to retrieve
     * @return ResponseEntity containing a list of AppointmentResponse objects with HTTP 200 OK status
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Retrieves all appointments for a specific doctor.
     * 
     * Endpoint: GET /api/appointments/doctor/{doctorId}
     * 
     * This endpoint fetches all appointments associated with a given doctor ID.
     * Useful for displaying a doctor's schedule or appointment calendar.
     * 
     * @param doctorId ID of the doctor whose appointments to retrieve
     * @return ResponseEntity containing a list of AppointmentResponse objects with HTTP 200 OK status
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Retrieves all appointments in the system.
     * 
     * Endpoint: GET /api/appointments
     * 
     * This endpoint fetches all appointments from the database.
     * Typically used by administrators for system-wide appointment management.
     * 
     * @return ResponseEntity containing a list of all AppointmentResponse objects with HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Updates an existing appointment with new details.
     * 
     * Endpoint: PUT /api/appointments/{id}
     * 
     * This endpoint updates all modifiable fields of an existing appointment.
     * Note: This endpoint does not update the appointment status. Use PATCH /api/appointments/{id}/status for status changes.
     * 
     * @param id ID of the appointment to update
     * @param request AppointmentRequest containing updated appointment details
     * @return ResponseEntity containing the updated AppointmentResponse with HTTP 200 OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Updates the status of an existing appointment.
     * 
     * Endpoint: PATCH /api/appointments/{id}/status
     * 
     * This endpoint is specifically designed for changing appointment status.
     * Common status values include: "SCHEDULED", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "NO_SHOW"
     * 
     * @param id ID of the appointment to update
     * @param status New status to set for the appointment (passed as query parameter)
     * @return ResponseEntity containing the updated AppointmentResponse with HTTP 200 OK status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletes an appointment from the system.
     * 
     * Endpoint: DELETE /api/appointments/{id}
     * 
     * This endpoint removes an appointment from the database.
     * Returns HTTP 204 No Content on successful deletion.
     * 
     * @param id ID of the appointment to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Retrieves appointments within a specific date range.
     * 
     * Endpoint: GET /api/appointments/date-range
     * 
     * This endpoint is useful for:
     * - Generating daily/weekly/monthly appointment reports
     * - Displaying calendar views
     * - Finding available time slots
     * 
     * @param start Start date/time of the range (passed as query parameter)
     * @param end End date/time of the range (passed as query parameter)
     * @return ResponseEntity containing a list of AppointmentResponse objects within the specified date range with HTTP 200 OK status
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Retrieves appointments with a specific status.
     * 
     * Endpoint: GET /api/appointments/status/{status}
     * 
     * This endpoint is useful for:
     * - Finding all pending appointments
     * - Identifying completed appointments for billing
     * - Tracking cancelled appointments
     * 
     * @param status Status to filter appointments by (e.g., "SCHEDULED", "COMPLETED", "CANCELLED")
     * @return ResponseEntity containing a list of AppointmentResponse objects with the specified status with HTTP 200 OK status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@PathVariable String status) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(responses);
    }
}
