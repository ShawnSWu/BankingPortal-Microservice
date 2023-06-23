package com.synpulse.transaction.domain;

import com.synpulse.transaction.domain.model.TransactionRecord;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import com.synpulse.transaction.persentation.dto.TransactionRecordDTO;
import com.synpulse.transaction.utils.DateUtils;
import io.confluent.ksql.api.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class TransactionService {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.url}")
    private String exchangeRateBaseUrl;

    @Value("${exchangerate.access.key}")
    private String exchangeRateApiKey;

    @Value("${ksqldb.server}")
    private String ksqlDbServer;

    @Value("${ksqldb.server.port}")
    private String ksqlDbServerPort;

    @Autowired
    private KafkaTemplate<String, QueryTransactionResponse> kafkaProducer;

    @Value("${query.response.topic}")
    private String queryResponseTopic;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public QueryTransactionResponse getTransactionRecords(QueryTransactionRequest transactionRequest)
            throws ParseException, ExecutionException, InterruptedException {
        String queryUserId = transactionRequest.getUserId();
        Date targetDate = DateUtils.convertByFormat(transactionRequest.getTargetDate(), "yyyy-MM-dd");

        String queryKSql = String.format("SELECT * FROM transaction_table " +
                "WHERE userId = %s AND valueDate = %d;", queryUserId, targetDate.getTime());

        // Create ksqlDB client options
        ClientOptions options = ClientOptions.create()
                .setHost(ksqlDbServer)
                .setPort(Integer.parseInt(ksqlDbServerPort));


        try (Client client = Client.create(options)) {

            ExecuteStatementResult executeStatementResult = client.executeStatement(queryKSql, null).get();

            // Get the query ID
            String queryId = executeStatementResult.queryId().get();

            // Poll for query results
            BatchedQueryResult queryResult = client.executeQuery(queryId);

            List<TransactionRecord> transactionRecords = new ArrayList<>();
            while (!queryResult.isComplete()) {
                for (Row row : queryResult.getRows()) {
                    String id = row.getString("id");
                    String userId = row.getString("userId");
                    BigDecimal amount = row.getDecimal("amount");
                    String currency = row.getString("currency");
                    String accountIban = row.getString("accountIban");
                    String date = row.getString("valueDate");
                    String description = row.getString("description");

                    transactionRecords.add(TransactionRecord.builder()
                            .id(id)
                            .userId(userId)
                            .amount(amount)
                            .currency(currency)
                            .accountIban(accountIban)
                            .valueDate(new Date(date))
                            .description(description)
                            .build());
                }

                // Get the next query result
                queryResult = client.poll(queryId);
            }

            // Cancel the query
            client.executeStatement("TERMINATE " + queryId + ";");
        }
        return getTransactionTotalCreditAndDebit(transactionRecords);
    }

    public double retrieveExchangeRateApiByCurrency(String date, String queryTargetCurrency) throws ExchangeRateApiException {
        String queryUrl = URI.create(exchangeRateBaseUrl)
                .resolve(date + "?access_key=" + exchangeRateApiKey)
                .toString();

        ResponseEntity<ExchangeRateApiResponse> response = restTemplate.getForEntity(queryUrl, ExchangeRateApiResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ExchangeRateApiException("Exchange rate api service got some exception");
        }

        ExchangeRateApiResponse body = response.getBody();

        Map<String, Double> rates = Objects.requireNonNull(body).getRates();

        if (!rates.containsKey(queryTargetCurrency)) {
            throw new ExchangeRateApiException("Not found the currency code you query.");
        }
        return rates.get(queryTargetCurrency);
    }

    public QueryTransactionResponse getTransactionTotalCreditAndDebit(List<TransactionRecord> transactions) throws ExchangeRateApiException, ParseException {
        List<TransactionRecordDTO> transactionDTOs = new ArrayList<>();

        double totalCredit = 0.0;
        double totalDebit = 0.0;

        for (TransactionRecord transaction : transactions) {
            String transactionDate = DateUtils.convertDate(transaction.getValueDate(), "yyyy-MM-dd");
            double exchangeRate = retrieveExchangeRateApiByCurrency(transactionDate, transaction.getCurrency()); //TODO Need optimal, Shouldn't call api every time

            BigDecimal convertedAmount = transaction.getAmount().multiply(BigDecimal.valueOf(exchangeRate));
            double amountDoubleValue = convertedAmount.doubleValue();

            TransactionRecordDTO recordDto = TransactionRecordDTO.builder()
                    .id(transaction.getId())
                    .amount(amountDoubleValue)
                    .currency(transaction.getCurrency())
                    .accountIBAN(transaction.getAccountIban())
                    .valueDate(transaction.getValueDate())
                    .description(transaction.getDescription())
                    .build();

            transactionDTOs.add(recordDto);

            if (amountDoubleValue >= 0) {
                totalCredit += amountDoubleValue;
            } else {
                totalDebit += amountDoubleValue;
            }
        }

        return QueryTransactionResponse.builder()
                .items(transactionDTOs)
                .totalCredit(totalCredit)
                .totalDebit(totalDebit)
                .build();
    }

}
