package com.expense.service.consumer;

import com.expense.service.dto.ExpenseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExpenseDeserializer implements Deserializer<ExpenseDto> {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDeserializer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No custom config needed for now
    }

    @Override
    public ExpenseDto deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                logger.warn("Received empty data on topic {}", topic);
                return null;
            }
            return mapper.readValue(data, ExpenseDto.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize ExpenseDto from topic {}: {}", topic, e.getMessage(), e);
            return null; // or throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

