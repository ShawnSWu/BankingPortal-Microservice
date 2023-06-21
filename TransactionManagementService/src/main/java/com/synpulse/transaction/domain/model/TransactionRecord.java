package com.synpulse.transaction.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    private String id;
    private BigDecimal amount;
    private String currency;
    private String accountIban;
    private Date valueDate;
    private String description;
}
