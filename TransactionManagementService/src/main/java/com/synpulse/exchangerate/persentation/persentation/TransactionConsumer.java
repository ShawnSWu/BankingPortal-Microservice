package com.synpulse.exchangerate.persentation.persentation;

import com.synpulse.exchangerate.persentation.domain.ExchangeRateApiException;
import com.synpulse.exchangerate.persentation.domain.TransactionService;
import com.synpulse.exchangerate.persentation.persentation.dto.QueryTransactionRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "query-transaction-v1", groupId = "transaction-consumer-group")
    public void consumeTransaction(ConsumerRecord<String, QueryTransactionRequest> request) {
        QueryTransactionRequest transactionRequest = request.value();

        try {
            transactionService.queryRecord(transactionRequest);
        } catch (ExchangeRateApiException e) {
            e.printStackTrace();
        }

    }
}
