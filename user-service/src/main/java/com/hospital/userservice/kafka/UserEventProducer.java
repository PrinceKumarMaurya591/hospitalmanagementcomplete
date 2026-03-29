package com.hospital.userservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.userservice.model.User;
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
public class UserEventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
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
    
    public void publishUserRegistered(User user) {
        try {
            String message = objectMapper.writeValueAsString(UserEvent.builder()
                    .eventType("USER_REGISTERED")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .roles(user.getRoles())
                    .build());
            
            sendMessage(userRegisteredTopic, user.getId(), message);
            log.info("Published USER_REGISTERED event for user: {}", user.getEmail());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user event for user: {}", user.getEmail(), e);
        }
    }
    
    public void publishUserUpdated(User user) {
        try {
            String message = objectMapper.writeValueAsString(UserEvent.builder()
                    .eventType("USER_UPDATED")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .roles(user.getRoles())
                    .build());
            
            sendMessage(userUpdatedTopic, user.getId(), message);
            log.info("Published USER_UPDATED event for user: {}", user.getEmail());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user event for user: {}", user.getEmail(), e);
        }
    }
    
    public void publishUserDeleted(String userId, String email) {
        try {
            String message = objectMapper.writeValueAsString(UserEvent.builder()
                    .eventType("USER_DELETED")
                    .userId(userId)
                    .email(email)
                    .build());
            
            sendMessage(userDeletedTopic, userId, message);
            log.info("Published USER_DELETED event for user: {}", email);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user event for user: {}", email, e);
        }
    }
    
    public void publishUserLogin(String userId, String email) {
        try {
            String message = objectMapper.writeValueAsString(UserEvent.builder()
                    .eventType("USER_LOGIN")
                    .userId(userId)
                    .email(email)
                    .build());
            
            sendMessage(userLoginTopic, userId, message);
            log.debug("Published USER_LOGIN event for user: {}", email);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user login event for user: {}", email, e);
        }
    }
    
    public void publishUserLogout(String userId, String email) {
        try {
            String message = objectMapper.writeValueAsString(UserEvent.builder()
                    .eventType("USER_LOGOUT")
                    .userId(userId)
                    .email(email)
                    .build());
            
            sendMessage(userLogoutTopic, userId, message);
            log.debug("Published USER_LOGOUT event for user: {}", email);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user logout event for user: {}", email, e);
        }
    }
    
    private void sendMessage(String topic, String key, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Message sent successfully to topic: {}, offset: {}", 
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic: {}", topic, ex);
            }
        });
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class UserEvent {
        private String eventType;
        private String userId;
        private String email;
        private String username;
        private Object roles;
        private long timestamp = System.currentTimeMillis();
    }
}