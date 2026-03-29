package com.hospital.billingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {
    
    @Value("${kafka.topics.appointment-created:appointment-created-topic}")
    private String appointmentCreatedTopic;
    
    @Value("${kafka.topics.billing-created:billing-created-topic}")
    private String billingCreatedTopic;
    
    @Value("${kafka.topics.payment-processed:payment-processed-topic}")
    private String paymentProcessedTopic;
    
    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
            TopicBuilder.name(appointmentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build(),
            TopicBuilder.name(billingCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build(),
            TopicBuilder.name(paymentProcessedTopic)
                .partitions(3)
                .replicas(1)
                .build()
        );
    }
    
    @Bean
    public NewTopic appointmentCreatedTopic() {
        return TopicBuilder.name(appointmentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic billingCreatedTopic() {
        return TopicBuilder.name(billingCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name(paymentProcessedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}