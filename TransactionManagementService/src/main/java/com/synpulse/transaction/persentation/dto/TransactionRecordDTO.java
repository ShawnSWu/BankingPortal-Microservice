package com.synpulse.transaction.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordDTO {
    private String id;
    private double amount;
    private String currency;
    private String accountIBAN;
    private Date valueDate;
    private String description;
}