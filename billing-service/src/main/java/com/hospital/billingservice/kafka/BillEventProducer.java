package com.hospital.billingservice.kafka;

import com.hospital.billingservice.dto.BillGeneratedEvent;
import com.hospital.billingservice.model.Bill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillEventProducer {
    
    private final KafkaTemplate<String, BillGeneratedEvent> kafkaTemplate;
    
    @Value("${kafka.topics.billing-created:billing-created-topic}")
    private String billingCreatedTopic;
    
    public void sendBillGeneratedEvent(Bill bill) {
        BillGeneratedEvent event = BillGeneratedEvent.builder()
                .billId(bill.getId())
                .appointmentId(bill.getAppointmentId())
                .patientId(bill.getPatientId())
                .doctorId(bill.getDoctorId())
                .invoiceNumber(bill.getInvoiceNumber())
                .amount(bill.getAmount())
                .tax(bill.getTax())
                .discount(bill.getDiscount())
                .totalAmount(bill.getTotalAmount())
                .status(bill.getStatus().name())
                .dueDate(bill.getDueDate())
                .generatedDate(bill.getGeneratedDate())
                .insuranceCovered(bill.getInsuranceCovered())
                .insuranceAmount(bill.getInsuranceAmount())
                .patientPayable(bill.getPatientPayable())
                .build();
        
        sendEvent(event);
    }
    
    private void sendEvent(BillGeneratedEvent event) {
        try {
            CompletableFuture<SendResult<String, BillGeneratedEvent>> future = 
                kafkaTemplate.send(billingCreatedTopic, event.getBillId().toString(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Bill generated event sent successfully for bill ID: {}, offset: {}", 
                            event.getBillId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send bill generated event for bill ID: {}", event.getBillId(), ex);
                    // TODO: Implement retry logic or dead letter queue
                }
            });
        } catch (Exception ex) {
            log.error("Exception occurred while sending bill generated event for bill ID: {}", 
                    event.getBillId(), ex);
        }
    }
}