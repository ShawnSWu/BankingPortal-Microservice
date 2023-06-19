package com.synpulse.exchangerate.persentation.persentation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QueryExchangeRateResponse {

    private double targetRate;
}
