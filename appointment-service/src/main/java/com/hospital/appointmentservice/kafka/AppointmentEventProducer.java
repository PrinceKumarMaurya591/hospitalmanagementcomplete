package com.hospital.appointmentservice.kafka;

import com.hospital.appointmentservice.dto.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventProducer {
    
    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;
    
    public void sendAppointmentCreatedEvent(AppointmentEvent event) {
        sendEvent("appointment-created-topic", event, "APPOINTMENT_CREATED");
    }
    
    public void sendAppointmentUpdatedEvent(AppointmentEvent event) {
        sendEvent("appointment-updated-topic", event, "APPOINTMENT_UPDATED");
    }
    
    public void sendAppointmentCancelledEvent(AppointmentEvent event) {
        sendEvent("appointment-cancelled-topic", event, "APPOINTMENT_CANCELLED");
    }
    
    private void sendEvent(String topic, AppointmentEvent event, String eventType) {
        event.setEventType(eventType);
        
        CompletableFuture<SendResult<String, AppointmentEvent>> future = 
                kafkaTemplate.send(topic, event.getAppointmentId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent {} event for appointment ID: {} to topic: {}", 
                        eventType, event.getAppointmentId(), topic);
            } else {
                log.error("Failed to send {} event for appointment ID: {} to topic: {}", 
                        eventType, event.getAppointmentId(), topic, ex);
            }
        });
    }
}