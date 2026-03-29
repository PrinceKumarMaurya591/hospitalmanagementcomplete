package com.hospital.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;
    
    @Value("${kafka.topics.user-updated}")
    private String userUpdatedTopic;
    
    @Value("${kafka.topics.user-deleted}")
    private String userDeletedTopic;
    
    @Value("${kafka.topics.user-login}")
    private String userLoginTopic;
    
    @Value("${kafka.topics.user-logout}")
    private String userLogoutTopic;
    
    @Value("${kafka.topics.partitions:3}")
    private int partitions;
    
    @Value("${kafka.topics.replication-factor:1}")
    private short replicationFactor;
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
    
    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name(userUpdatedTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
    
    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name(userDeletedTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
    
    @Bean
    public NewTopic userLoginTopic() {
        return TopicBuilder.name(userLoginTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
    
    @Bean
    public NewTopic userLogoutTopic() {
        return TopicBuilder.name(userLogoutTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
}