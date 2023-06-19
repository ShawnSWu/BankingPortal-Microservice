package com.synpulse.exchangerate.persentation.persentation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryExchangeRateRequest {
    private String date;
    private String baseCurrency;
    private String targetCurrency;
}
