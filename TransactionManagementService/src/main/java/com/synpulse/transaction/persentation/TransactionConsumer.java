package com.synpulse.transaction.persentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synpulse.transaction.domain.ExchangeRateApiException;
import com.synpulse.transaction.domain.TransactionService;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private final TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "query_transaction_v1", groupId = "transaction_group")
    public void consumeTransaction(String request) throws JsonProcessingException {
        QueryTransactionRequest queryTransactionRequest = objectMapper.readValue(request, QueryTransactionRequest.class);

        try {
            transactionService.queryRecord(queryTransactionRequest);
        } catch (ExchangeRateApiException e) {
            e.printStackTrace();
        }
    }
}
