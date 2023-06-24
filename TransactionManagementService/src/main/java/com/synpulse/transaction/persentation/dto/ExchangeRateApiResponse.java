package com.synpulse.transaction.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateApiResponse {
    private boolean success;

    private long timestamp;

    private boolean historical;

    private String base;

    private String date;

    private Map<String,Double> rates;
}
