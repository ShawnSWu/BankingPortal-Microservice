package com.sypuluse.transaction;

import com.synpulse.transaction.TransactionManagementApplication;
import com.synpulse.transaction.domain.TransactionService;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(SpringJUnit4ClassRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-dev.properties")
@SpringBootTest(classes = {TransactionManagementApplication.class})
public class TransactionTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private TransactionService transactionService;

    @Test
    public void testRetrieveExchangeRateApiByCurrency() throws Exception {
        String date = "2023-06-24";
        String queryTargetCurrency = "USD";
        ExchangeRateApiResponse mockResponse = new ExchangeRateApiResponse();
        double expectedExchangeRate = 1.5;

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ExchangeRateApiResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put(queryTargetCurrency, expectedExchangeRate);
        mockResponse.setRates(mockRates);

        double exchangeRate = transactionService.retrieveExchangeRateApiByCurrency(date, queryTargetCurrency);

        assertEquals(expectedExchangeRate, exchangeRate, 0.001);
    }
}
