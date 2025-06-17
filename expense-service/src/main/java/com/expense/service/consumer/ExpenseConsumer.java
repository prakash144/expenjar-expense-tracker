package com.expense.service.consumer;

import com.expense.service.dto.ExpenseDto;
import com.expense.service.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpenseConsumer {

    private final ExpenseService expenseService;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ExpenseDto eventData) {
        try {
            // TODO: Make it transactional and check for duplicate events (handle idempotency)
            log.info("Received event from Kafka: {}", eventData);
            expenseService.createExpense(eventData);
        } catch (Exception ex) {
            log.error("ExpenseConsumer: Exception occurred while consuming Kafka event", ex);
        }
    }
}
