package com.synpulse.exchangerate.persentation.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryTransactionRequest {
    private String userId;
    private String targetDate;
}
