package com.hospital.billingservice.kafka;

import com.hospital.billingservice.dto.AppointmentCompletedEvent;
import com.hospital.billingservice.service.BillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {
    
    private final BillService billService;
    
    @KafkaListener(
            topics = "${kafka.topics.appointment-created:appointment-created-topic}",
            groupId = "${spring.kafka.consumer.group-id:hospital-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAppointmentCompletedEvent(AppointmentCompletedEvent event, Acknowledgment ack) {
        try {
            log.info("Received appointment completed event for appointment ID: {}", event.getAppointmentId());
            
            // Create bill from appointment completion event
            billService.createBillFromAppointment(event);
            
            // Acknowledge the message
            ack.acknowledge();
            
            log.info("Successfully processed appointment completed event for appointment ID: {}", 
                    event.getAppointmentId());
        } catch (Exception ex) {
            log.error("Error processing appointment completed event for appointment ID: {}", 
                    event.getAppointmentId(), ex);
            // TODO: Implement dead letter queue or retry logic
            // For now, we'll acknowledge to prevent blocking the queue
            ack.acknowledge();
        }
    }
}