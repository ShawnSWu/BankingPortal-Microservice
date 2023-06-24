package com.synpulse.transaction.persentation.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTransactionResponse {
    private List<TransactionRecordDTO> items;
    private double totalCredit;
    private double totalDebit;
}
