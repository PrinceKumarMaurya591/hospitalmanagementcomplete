package com.hospital.appointmentservice.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentEventProducer appointmentEventProducer;
    
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment for patient: {}, doctor: {}", 
                request.getPatientId(), request.getDoctorId());
        
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment = appointmentRepository.save(appointment);
        
        log.info("Appointment created with ID: {}", appointment.getId());
        
        // Send Kafka event
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_CREATED");
        appointmentEventProducer.sendAppointmentCreatedEvent(event);
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        log.info("Fetching appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        return appointmentMapper.toResponse(appointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        log.info("Fetching appointments for patient ID: {}", patientId);
        
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        log.info("Fetching appointments for doctor ID: {}", doctorId);
        
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        log.info("Fetching all appointments");
        
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
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
        
        // Send Kafka event
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_UPDATED");
        appointmentEventProducer.sendAppointmentUpdatedEvent(event);
        
        return response;
    }
    
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, String status) {
        log.info("Updating appointment status to {} for ID: {}", status, id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);
        
        log.info("Appointment status updated for ID: {}", id);
        
        // Send Kafka event for status update
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_STATUS_UPDATED");
        appointmentEventProducer.sendAppointmentUpdatedEvent(event);
        
        return response;
    }
    
    @Transactional
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        
        // Send Kafka event before deletion
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        AppointmentEvent event = AppointmentEvent.fromAppointmentResponse(response, "APPOINTMENT_CANCELLED");
        appointmentEventProducer.sendAppointmentCancelledEvent(event);
        
        appointmentRepository.deleteById(id);
        log.info("Appointment deleted with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching appointments between {} and {}", start, end);
        
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(String status) {
        log.info("Fetching appointments with status: {}", status);
        
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}