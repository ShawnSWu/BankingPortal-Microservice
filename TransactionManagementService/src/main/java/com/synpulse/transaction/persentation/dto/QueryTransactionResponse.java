package com.synpulse.transaction.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTransactionResponse {
    private List<TransactionRecordDTO> items;
    private double totalCredit;
    private double totalDebit;
}
