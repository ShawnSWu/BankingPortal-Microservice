package com.synpulse.transaction.persentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synpulse.transaction.domain.ExchangeRateApiException;
import com.synpulse.transaction.domain.TransactionService;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger logger = LogManager.getLogger(TransactionConsumer.class);

    private final TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, QueryTransactionResponse> kafkaProducer;

    @Value("${query.response.topic}")
    private String queryResponseTopic;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "query_transaction_v1", groupId = "transaction_group")
    public void consumeTransaction(String request) {
        try {
            QueryTransactionRequest queryTransactionRequest = objectMapper.readValue(request, QueryTransactionRequest.class);

            QueryTransactionResponse queryTransactionResponse = transactionService.getTransactionTotalCreditAndDebit(queryTransactionRequest);

            notifyResponseTopic(queryTransactionResponse);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Error processing JSON: %s, cause: %s", request, e.getMessage()));
        } catch (ExchangeRateApiException e) {
            e.printStackTrace();
            logger.error(String.format("ExchangeRateApiException, cause: %s", e.getMessage()));
        }
    }

    private void notifyResponseTopic(QueryTransactionResponse queryTransactionResponse) {
        kafkaProducer.send(queryResponseTopic, queryTransactionResponse);
    }

}
