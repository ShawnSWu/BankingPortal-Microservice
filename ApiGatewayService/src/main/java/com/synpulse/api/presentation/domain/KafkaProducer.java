package com.synpulse.api.presentation.domain;

import com.synpulse.api.presentation.dto.QueryTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, QueryTransactionRequest> kafkaTemplate;
    @Value("${spring.kafka.template.default-topic}")
    private String TOPIC_NAME;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, QueryTransactionRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(QueryTransactionRequest request) {
        kafkaTemplate.send(TOPIC_NAME, request);
    }
}