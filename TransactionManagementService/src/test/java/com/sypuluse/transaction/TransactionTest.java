package com.sypuluse.transaction;

import com.synpulse.transaction.TransactionManagementApplication;
import com.synpulse.transaction.domain.ExchangeRateApiException;
import com.synpulse.transaction.domain.TransactionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TransactionManagementApplication.class)
@ActiveProfiles("dev")
public class TransactionTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void test_exchange_rate_api() throws ExchangeRateApiException {
        transactionService.retrieveExchangeRateApiByCurrency("2023-04-03", "USD");
    }

}
