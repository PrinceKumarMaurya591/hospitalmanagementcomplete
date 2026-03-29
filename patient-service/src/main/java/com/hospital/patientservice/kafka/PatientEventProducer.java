package com.hospital.patientservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.patientservice.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientEventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String PATIENT_CREATED_TOPIC = "patient.created";
    private static final String PATIENT_UPDATED_TOPIC = "patient.updated";
    private static final String PATIENT_DELETED_TOPIC = "patient.deleted";
    private static final String PATIENT_DEACTIVATED_TOPIC = "patient.deactivated";
    private static final String PATIENT_ACTIVATED_TOPIC = "patient.activated";
    
    public void sendPatientCreatedEvent(PatientResponse patient) {
        sendEvent(PATIENT_CREATED_TOPIC, patient.getId().toString(), patient, "Patient Created");
    }
    
    public void sendPatientUpdatedEvent(PatientResponse patient) {
        sendEvent(PATIENT_UPDATED_TOPIC, patient.getId().toString(), patient, "Patient Updated");
    }
    
    public void sendPatientDeletedEvent(Long patientId) {
        PatientDeletedEvent event = new PatientDeletedEvent(patientId, System.currentTimeMillis());
        sendEvent(PATIENT_DELETED_TOPIC, patientId.toString(), event, "Patient Deleted");
    }
    
    public void sendPatientDeactivatedEvent(PatientResponse patient) {
        sendEvent(PATIENT_DEACTIVATED_TOPIC, patient.getId().toString(), patient, "Patient Deactivated");
    }
    
    public void sendPatientActivatedEvent(PatientResponse patient) {
        sendEvent(PATIENT_ACTIVATED_TOPIC, patient.getId().toString(), patient, "Patient Activated");
    }
    
    private void sendEvent(String topic, String key, Object event, String eventType) {
        try {
            String message = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(topic, key, message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent {} event for patient ID: {} to topic: {}", 
                            eventType, key, topic);
                } else {
                    log.error("Failed to send {} event for patient ID: {} to topic: {}", 
                            eventType, key, topic, ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing {} event for patient ID: {}", eventType, key, e);
        }
    }
    
    public static class PatientDeletedEvent {
        private Long patientId;
        private Long deletedAt;
        
        public PatientDeletedEvent(Long patientId, Long deletedAt) {
            this.patientId = patientId;
            this.deletedAt = deletedAt;
        }
        
        // Getters and setters
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        
        public Long getDeletedAt() { return deletedAt; }
        public void setDeletedAt(Long deletedAt) { this.deletedAt = deletedAt; }
    }
}