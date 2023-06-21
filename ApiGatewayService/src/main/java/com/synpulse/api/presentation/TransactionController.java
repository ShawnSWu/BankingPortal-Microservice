package com.synpulse.api.presentation;

import com.synpulse.api.presentation.domain.KafkaProducer;
import com.synpulse.api.presentation.dto.QueryTransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping
    @Operation(summary = "Get currency exchange rate", tags = {"ExchangeRateApi"},
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Void.class))})
            }
    )
    public void getMoneyTransaction(@RequestParam String userId, @RequestParam String date) {
        QueryTransactionRequest request = QueryTransactionRequest.builder()
                .userId("0-1234567")
                .targetDate("2023-06-18")
                .build();
        kafkaProducer.sendMessage(request);
    }

}
