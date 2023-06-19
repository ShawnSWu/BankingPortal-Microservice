package com.synpulse.exchangerate.persentation.persentation;

import com.synpulse.exchangerate.persentation.persentation.dto.QueryExchangeRateRequest;
import com.synpulse.exchangerate.persentation.persentation.dto.QueryExchangeRateResponse;
import com.synpulse.exchangerate.persentation.usecase.ExchangeRateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/exchangeRate/v1")
@SecurityRequirements(value = @SecurityRequirement(name = "x-api-key"))
public class ExchangeRateController {

    private ExchangeRateUseCase exchangeRateUseCase;

    public ExchangeRateController(ExchangeRateUseCase exchangeRateUseCase) {
        this.exchangeRateUseCase = exchangeRateUseCase;
    }

    @PostMapping("/")
    @Operation(summary = "Get currency exchange rate", tags = {"ExchangeRateApi"},
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = QueryExchangeRateResponse.class))})
            })
    public ResponseEntity<?> getExchangeRate(@RequestBody QueryExchangeRateRequest exchangeRateRequest) {
        double targetRate = exchangeRateUseCase.getRateByExchangeRatesIo(exchangeRateRequest);
        return ResponseEntity.ok(QueryExchangeRateResponse.builder().targetRate(targetRate).build());
    }


}
