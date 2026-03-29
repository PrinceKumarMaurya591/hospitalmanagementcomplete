package com.hospital.patientservice.controller;

import com.hospital.patientservice.dto.PatientRequest;
import com.hospital.patientservice.dto.PatientResponse;
import com.hospital.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        PatientResponse response = patientService.getPatientById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<PatientResponse> getPatientByEmail(@PathVariable String email) {
        PatientResponse response = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/phone/{phone}")
    public ResponseEntity<PatientResponse> getPatientByPhone(@PathVariable String phone) {
        PatientResponse response = patientService.getPatientByPhone(phone);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> responses = patientService.getAllPatients();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<PatientResponse>> getAllPatientsPaginated(Pageable pageable) {
        Page<PatientResponse> responses = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String query) {
        List<PatientResponse> responses = patientService.searchPatients(query);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<PatientResponse>> getPatientsByGender(@PathVariable String gender) {
        List<PatientResponse> responses = patientService.getPatientsByGender(gender);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<List<PatientResponse>> getPatientsByBloodGroup(@PathVariable String bloodGroup) {
        List<PatientResponse> responses = patientService.getPatientsByBloodGroup(bloodGroup);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<PatientResponse>> getActivePatients() {
        List<PatientResponse> responses = patientService.getActivePatients();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/born-after")
    public ResponseEntity<List<PatientResponse>> getPatientsBornAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PatientResponse> responses = patientService.getPatientsBornAfter(date);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PatientResponse> deactivatePatient(@PathVariable Long id) {
        PatientResponse response = patientService.deactivatePatient(id);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<PatientResponse> activatePatient(@PathVariable Long id) {
        PatientResponse response = patientService.activatePatient(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActivePatients() {
        Long count = patientService.countActivePatients();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> patientExists(@PathVariable Long id) {
        boolean exists = patientService.patientExists(id);
        return ResponseEntity.ok(exists);
    }
}