package com.prakash.userservice.consumer;

import com.prakash.userservice.entities.UserInfoDto;
import com.prakash.userservice.service.UserService;
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
    private final UserService userService;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserInfoDto eventData) {
        try {
            // TODO: Add transactional support and validation for email, phoneNumber, etc.
            log.info("Received Kafka event for user: {}", eventData.getUserId());
            userService.createOrUpdateUser(eventData);
            log.info("User data processed successfully for: {}", eventData.getUserId());
        } catch (Exception ex) {
            log.error("Exception while consuming Kafka event in AuthServiceConsumer", ex);
        }
    }
}
