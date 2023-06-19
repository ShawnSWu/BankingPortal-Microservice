package com.synpulse.exchangerate.persentation.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateApiResponse {
    private boolean success;

    private long timestamp;

    private boolean historical;

    private String base;

    private String date;

    private Map<String,Double> rates;
}
