package com.synpulse.api.presentation.dto;

import lombok.*;

import java.io.Serializable;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryTransactionRequest implements Serializable {
    private String userId;
    private String targetDate;
}
