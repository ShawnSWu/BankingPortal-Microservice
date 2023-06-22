package com.synpulse.transaction.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryTransactionRequest implements Serializable {
    private String userId;
    private String targetDate;
    private int pageSize;
}
