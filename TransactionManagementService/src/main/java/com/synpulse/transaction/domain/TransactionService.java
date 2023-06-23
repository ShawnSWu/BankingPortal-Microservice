package com.synpulse.transaction.domain;

import com.synpulse.transaction.domain.model.TransactionRecord;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import com.synpulse.transaction.persentation.dto.TransactionRecordDTO;
import com.synpulse.transaction.utils.DateUtils;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TransactionService {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.url}")
    private String exchangeRateBaseUrl;

    @Value("${exchangerate.access.key}")
    private String exchangeRateApiKey;

    public TransactionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<TransactionRecord> queryRecord(QueryTransactionRequest transactionRequest) throws ParseException {
        String userId = transactionRequest.getUserId();
        Date targetDate = DateUtils.convertByFormat(transactionRequest.getTargetDate(), "yyyy-MM-dd");

        String queryKSql = String.format("SELECT * FROM transaction_table " +
                "WHERE id > %s AND valueDate = %d;", userId, targetDate.getTime());


        String serverEndpoint = "localhost";

        // Create ksqlDB client options
        ClientOptions options = ClientOptions.create()
                .setHost(serverEndpoint)
                .setPort(8088);

        // Create ksqlDB client
        Client client = Client.create(options);

        BatchedQueryResult batchedQueryResult = client.executeQuery(queryKSql);

        return new ArrayList<>();
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

    public QueryTransactionResponse getTransactionTotalCreditAndDebit(QueryTransactionRequest request) throws ExchangeRateApiException, ParseException {
        //query to get record
        List<TransactionRecord> transactions = queryRecord(request);

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
