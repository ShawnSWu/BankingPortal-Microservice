package com.synpulse.api.presentation;

import com.synpulse.api.exception.ParameterException;
import com.synpulse.api.presentation.domain.KafkaProducer;
import com.synpulse.api.presentation.dto.QueryTransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void getMoneyTransaction(@RequestParam String userId, @RequestParam String date) throws Exception {
        Pattern userIdPattern = Pattern.compile("\\d+-\\d+");
        if (!userIdPattern.matcher(userId).matches()) {
            throw new ParameterException("userId does not match the rules.");
        }

        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        if (!datePattern.matcher(date).matches()) {
            throw new ParameterException("date does not match the pattern.");
        }

        QueryTransactionRequest request = QueryTransactionRequest.builder()
                .userId(userId)
                .targetDate(date)
                .build();
        kafkaProducer.sendMessage(request);
    }

}
