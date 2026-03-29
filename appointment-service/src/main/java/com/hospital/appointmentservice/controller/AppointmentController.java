package com.hospital.appointmentservice.controller;

import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@PathVariable String status) {
        List<AppointmentResponse> responses = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(responses);
    }
}