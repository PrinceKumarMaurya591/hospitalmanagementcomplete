package com.hospital.billingservice.kafka;

import com.hospital.billingservice.dto.PaymentProcessedEvent;
import com.hospital.billingservice.model.Payment;
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
public class PaymentEventProducer {
    
    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;
    
    @Value("${kafka.topics.payment-processed:payment-processed-topic}")
    private String paymentProcessedTopic;
    
    public void sendPaymentProcessedEvent(Payment payment) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(payment.getId())
                .billId(payment.getBillId())
                .patientId(payment.getPatientId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .remarks(payment.getRemarks())
                .build();
        
        sendEvent(event);
    }
    
    private void sendEvent(PaymentProcessedEvent event) {
        try {
            CompletableFuture<SendResult<String, PaymentProcessedEvent>> future = 
                kafkaTemplate.send(paymentProcessedTopic, event.getPaymentId().toString(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Payment processed event sent successfully for payment ID: {}, offset: {}", 
                            event.getPaymentId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send payment processed event for payment ID: {}", 
                            event.getPaymentId(), ex);
                    // TODO: Implement retry logic or dead letter queue
                }
            });
        } catch (Exception ex) {
            log.error("Exception occurred while sending payment processed event for payment ID: {}", 
                    event.getPaymentId(), ex);
        }
    }
}