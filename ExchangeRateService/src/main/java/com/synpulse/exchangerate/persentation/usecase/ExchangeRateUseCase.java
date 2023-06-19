package com.synpulse.exchangerate.persentation.usecase;

import com.synpulse.exchangerate.persentation.domain.exception.ThirdPartyServiceException;
import com.synpulse.exchangerate.persentation.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.exchangerate.persentation.persentation.dto.QueryExchangeRateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
public class ExchangeRateUseCase {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.url}")
    private String exchangeRateBaseUrl;

    @Value("${exchangerate.access.key}")
    private String exchangeRateApiKey;

    public ExchangeRateUseCase(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //https://exchangeratesapi.io/
    public double getRateByExchangeRatesIo(QueryExchangeRateRequest request) {
        String date = request.getDate();
        String queryUrl = exchangeRateBaseUrl + date + "?access_key=" + exchangeRateApiKey;

        ResponseEntity<ExchangeRateApiResponse> response = restTemplate.getForEntity(queryUrl, ExchangeRateApiResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ThirdPartyServiceException();
        }

        ExchangeRateApiResponse body = response.getBody();

        Map<String, Double> rates = Objects.requireNonNull(body).getRates();

        if (!rates.containsKey(request.getTargetCurrency())){
            throw new ThirdPartyServiceException();
        }

        return rates.get(request.getTargetCurrency());
    }
}
