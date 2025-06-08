package com.example.user_service.consumer;

import com.example.user_service.entities.UserInfoDto;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {

    @Autowired
    private UserService userService;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserInfoDto eventData) {
        log.info("Received Kafka event to process user: {}", eventData.getUserId());

        try {
            // TODO: Make it transactional to handle idempotency and validate email, phoneNumber etc; Hint : can use Redis distributed lock
            userService.createOrUpdateUser(eventData);
            log.info("User processed successfully: {}", eventData.getUserId());
        } catch (Exception e) {
            log.error("Exception thrown while consuming Kafka event for user {}: {}", eventData.getUserId(), e.getMessage(), e);
            // Rethrow or handle according to your retry or error handling strategy
            throw new RuntimeException("Error processing Kafka event", e);
        }
    }
}
