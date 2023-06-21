package com.synpulse.exchangerate.persentation.persentation;

import com.synpulse.exchangerate.persentation.domain.ExchangeRateApiException;
import com.synpulse.exchangerate.persentation.domain.TransactionService;
import com.synpulse.exchangerate.persentation.persentation.dto.QueryTransactionRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "query_transaction_v1", groupId = "transaction_group")
    public void consumeTransaction(ConsumerRecord<String, QueryTransactionRequest> request) {
        QueryTransactionRequest transactionRequest = request.value();

        try {
            transactionService.queryRecord(transactionRequest);
        } catch (ExchangeRateApiException e) {
            e.printStackTrace();
        }
    }
}
