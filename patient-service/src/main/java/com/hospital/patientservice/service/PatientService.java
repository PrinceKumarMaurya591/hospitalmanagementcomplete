package com.hospital.patientservice.service;

import com.hospital.patientservice.dto.PatientRequest;
import com.hospital.patientservice.dto.PatientResponse;
import com.hospital.patientservice.exception.DuplicateResourceException;
import com.hospital.patientservice.exception.PatientNotFoundException;
import com.hospital.patientservice.mapper.PatientMapper;
import com.hospital.patientservice.model.Patient;
import com.hospital.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    public PatientResponse createPatient(PatientRequest request) {
        log.info("Creating new patient with email: {}", request.getEmail());
        
        // Check for duplicate email
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Patient", "email", request.getEmail());
        }
        
        // Check for duplicate phone
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Patient", "phone", request.getPhone());
        }
        
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        
        log.info("Patient created successfully with id: {}", savedPatient.getId());
        return patientMapper.toResponse(savedPatient);
    }
    
    public PatientResponse getPatientById(Long id) {
        log.info("Fetching patient with id: {}", id);
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException(id));
        return patientMapper.toResponse(patient);
    }
    
    public PatientResponse getPatientByEmail(String email) {
        log.info("Fetching patient with email: {}", email);
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new PatientNotFoundException("email", email));
        return patientMapper.toResponse(patient);
    }
    
    public PatientResponse getPatientByPhone(String phone) {
        log.info("Fetching patient with phone: {}", phone);
        Patient patient = patientRepository.findByPhone(phone)
            .orElseThrow(() -> new PatientNotFoundException("phone", phone));
        return patientMapper.toResponse(patient);
    }
    
    public List<PatientResponse> getAllPatients() {
        log.info("Fetching all patients");
        return patientRepository.findAll().stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        log.info("Fetching patients with pagination: {}", pageable);
        return patientRepository.findAll(pageable)
            .map(patientMapper::toResponse);
    }
    
    public List<PatientResponse> searchPatients(String searchTerm) {
        log.info("Searching patients with term: {}", searchTerm);
        return patientRepository.searchPatients(searchTerm).stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PatientResponse> getPatientsByGender(String gender) {
        log.info("Fetching patients by gender: {}", gender);
        return patientRepository.findByGender(gender).stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PatientResponse> getPatientsByBloodGroup(String bloodGroup) {
        log.info("Fetching patients by blood group: {}", bloodGroup);
        return patientRepository.findByBloodGroup(bloodGroup).stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PatientResponse> getActivePatients() {
        log.info("Fetching active patients");
        return patientRepository.findByIsActive(true).stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PatientResponse> getPatientsBornAfter(LocalDate date) {
        log.info("Fetching patients born after: {}", date);
        return patientRepository.findPatientsBornAfter(date).stream()
            .map(patientMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        log.info("Updating patient with id: {}", id);
        
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException(id));
        
        // Check if email is being changed and if new email already exists
        if (!patient.getEmail().equals(request.getEmail()) && 
            patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Patient", "email", request.getEmail());
        }
        
        // Check if phone is being changed and if new phone already exists
        if (!patient.getPhone().equals(request.getPhone()) && 
            patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Patient", "phone", request.getPhone());
        }
        
        patientMapper.updateEntityFromRequest(request, patient);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Patient updated successfully with id: {}", id);
        return patientMapper.toResponse(updatedPatient);
    }
    
    public void deletePatient(Long id) {
        log.info("Deleting patient with id: {}", id);
        
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        
        patientRepository.deleteById(id);
        log.info("Patient deleted successfully with id: {}", id);
    }
    
    public PatientResponse deactivatePatient(Long id) {
        log.info("Deactivating patient with id: {}", id);
        
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException(id));
        
        patient.setIsActive(false);
        Patient deactivatedPatient = patientRepository.save(patient);
        
        log.info("Patient deactivated successfully with id: {}", id);
        return patientMapper.toResponse(deactivatedPatient);
    }
    
    public PatientResponse activatePatient(Long id) {
        log.info("Activating patient with id: {}", id);
        
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException(id));
        
        patient.setIsActive(true);
        Patient activatedPatient = patientRepository.save(patient);
        
        log.info("Patient activated successfully with id: {}", id);
        return patientMapper.toResponse(activatedPatient);
    }
    
    public Long countActivePatients() {
        return patientRepository.countActivePatients();
    }
    
    public boolean patientExists(Long id) {
        return patientRepository.existsById(id);
    }
}