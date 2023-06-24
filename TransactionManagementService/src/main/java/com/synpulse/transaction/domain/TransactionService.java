package com.synpulse.transaction.domain;

import com.synpulse.transaction.domain.model.TransactionRecord;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
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

    @Value("${query.response.topic}")
    private String queryResponseTopic;

    @Autowired
    private DateUtils dateUtils;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Row> queryTransactionByKsql(String queryKSql) throws ExecutionException, InterruptedException {
        ClientOptions options = ClientOptions.create()
                .setHost(ksqlDbServer)
                .setPort(Integer.parseInt(ksqlDbServerPort));

        Client client = Client.create(options);
        BatchedQueryResult batchedQueryResult = client.executeQuery(queryKSql);

        return batchedQueryResult.get();
    }

    public QueryTransactionResponse getTransactionTotalCreditAndDebit(List<TransactionRecord> transactionRecords) {
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (TransactionRecord transactionRecord : transactionRecords) {
            if (transactionRecord.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                totalCredit = totalCredit.add(transactionRecord.getAmount());
            } else {
                totalDebit = totalDebit.add(transactionRecord.getAmount());
            }
        }

        return QueryTransactionResponse.builder()
                .totalCredit(totalCredit.doubleValue())
                .totalDebit(totalDebit.doubleValue())
                .build();
    }

    public List<TransactionRecord> convertToTransactionRecords(List<Row> rows) {
        List<TransactionRecord> transactionRecords = new ArrayList<>();
        for (Row row : rows) {
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
        return transactionRecords;
    }

    public QueryTransactionResponse getTransactionRecords(QueryTransactionRequest transactionRequest)
            throws ParseException, ExecutionException, InterruptedException, ExchangeRateApiException {
        String queryUserId = transactionRequest.getUserId();
        Date targetDate = dateUtils.convertByFormat(transactionRequest.getTargetDate(), "yyyy-MM-dd");
        int pageSize = transactionRequest.getPageSize();

        //TODO find parameter method replace it.
        String queryKSql = String.format("SELECT * FROM transaction_table " +
                "WHERE userId = %s AND valueDate = %d LIMIT %d;", queryUserId, targetDate.getTime(), pageSize);

        List<Row> resultRows = queryTransactionByKsql(queryKSql);

        List<TransactionRecord> transactionRecords = convertToTransactionRecords(resultRows);

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

}
