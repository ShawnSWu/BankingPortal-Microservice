package com.synpulse.exchangerate.persentation.domain;

import com.synpulse.exchangerate.persentation.domain.model.TransactionRecord;
import com.synpulse.exchangerate.persentation.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.exchangerate.persentation.persentation.dto.QueryTransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<TransactionRecord> queryRecord(QueryTransactionRequest transactionRequest) throws ExchangeRateApiException {
        //query from kafka
        String targetDate = transactionRequest.getTargetDate();
        String userId = transactionRequest.getUserId();

        double usd = retrieveExchangeRateApiByCurrency(targetDate, "USD");


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

}
