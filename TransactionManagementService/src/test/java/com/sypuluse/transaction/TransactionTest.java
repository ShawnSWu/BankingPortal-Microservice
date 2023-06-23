package com.sypuluse.transaction;

import com.synpulse.transaction.TransactionManagementApplication;
import com.synpulse.transaction.domain.ExchangeRateApiException;
import com.synpulse.transaction.domain.TransactionService;

import com.synpulse.transaction.domain.model.TransactionRecord;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import com.synpulse.transaction.utils.DateUtils;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = TransactionManagementApplication.class)
@ActiveProfiles("dev")
public class TransactionTest {

    @Autowired
    private TransactionService transactionService;

    private Client mockClient;
    private BatchedQueryResult mockQueryResult;
    private List<Row> mockRows;
    private TransactionRecord mockTransactionRecord;
    private QueryTransactionResponse mockResponse;
    private RestTemplate mockRestTemplate;
    private ResponseEntity<ExchangeRateApiResponse> mockResponseEntity;
    private ExchangeRateApiResponse mockExchangeRateApiResponse;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException {
        mockClient = Mockito.mock(Client.class);
        mockQueryResult = Mockito.mock(BatchedQueryResult.class);
        mockRows = Mockito.mock(List.class);
        mockTransactionRecord = Mockito.mock(TransactionRecord.class);
        mockResponse = Mockito.mock(QueryTransactionResponse.class);
        mockRestTemplate = Mockito.mock(RestTemplate.class);
        mockResponseEntity = Mockito.mock(ResponseEntity.class);
        mockExchangeRateApiResponse = Mockito.mock(ExchangeRateApiResponse.class);

        // 設定相依物件的 mock 行為
        Mockito.when(mockClient.executeQuery(Mockito.anyString())).thenReturn(mockQueryResult);
        Mockito.when(mockQueryResult.get()).thenReturn(mockRows);
        Mockito.when(mockTransactionRecord.getAmount()).thenReturn(BigDecimal.ONE);
//        Mockito.when(mockRows.get(Mockito.anyInt())).thenReturn(mockTransactionRecord);
        Mockito.when(mockResponse.getTotalCredit()).thenReturn(BigDecimal.ONE.doubleValue());
        Mockito.when(mockResponse.getTotalDebit()).thenReturn(BigDecimal.ONE.doubleValue());
        Mockito.when(mockRestTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ExchangeRateApiResponse.class)))
                .thenReturn(mockResponseEntity);
        Mockito.when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(mockResponseEntity.getBody()).thenReturn(mockExchangeRateApiResponse);
        Mockito.when(mockExchangeRateApiResponse.getRates()).thenReturn(Collections.singletonMap("USD", 1.0));

        // 將 mockRestTemplate 設定給 yourClass
        ReflectionTestUtils.setField(transactionService, "restTemplate", mockRestTemplate);

        // 初始化 MockRestServiceServer
        mockServer = MockRestServiceServer.createServer(mockRestTemplate);
    }

    @Test
    public void testRetrieveExchangeRateApiByCurrency() throws ExchangeRateApiException {
        // Arrange
        String mockDate = "2023-06-24";
        String mockCurrency = "USD";
        String mockBaseUrl = "https://api.example.com/";
        String mockApiKey = "your-api-key";
        String expectedUrl = mockBaseUrl + mockDate + "?access_key=" + mockApiKey;
        double expectedRate = 1.0;

        // Act
        double result = transactionService.retrieveExchangeRateApiByCurrency(mockDate, mockCurrency);

        // Assert
        Assertions.assertEquals(expectedRate, result);
        mockServer.verify(); // 驗證是否有對外部API進行呼叫
        mockServer.expect(MockRestRequestMatchers.requestTo(expectedUrl)) // 驗證呼叫的URL是否正確
                .andRespond(MockRestResponseCreators.withSuccess("{\"rates\":{\"USD\": 1.0}}", MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetTransactionRecords() throws ParseException, ExecutionException, InterruptedException, ExchangeRateApiException {
        // Arrange
        QueryTransactionRequest mockRequest = Mockito.mock(QueryTransactionRequest.class);
        Date mockDate = Mockito.mock(Date.class);
        String mockUserId = "user123";
        String mockTargetDate = "2023-06-24";
        String mockKSql = "SELECT * FROM transaction_table WHERE userId = user123 AND valueDate = 1234567890";
        List<TransactionRecord> mockTransactionRecords = Collections.singletonList(mockTransactionRecord);

        Mockito.when(mockRequest.getUserId()).thenReturn(mockUserId);
        Mockito.when(mockRequest.getTargetDate()).thenReturn(mockTargetDate);
        Mockito.when(DateUtils.convertByFormat(mockTargetDate, "yyyy-MM-dd")).thenReturn(mockDate);
        Mockito.when(mockDate.getTime()).thenReturn(1234567890L);
        Mockito.when(transactionService.queryTransactionByKsql(mockKSql)).thenReturn(mockRows);
        Mockito.when(transactionService.convertToTransactionRecords(mockRows)).thenReturn(mockTransactionRecords);
        Mockito.when(transactionService.getTransactionTotalCreditAndDebit(mockTransactionRecords)).thenReturn(mockResponse);

        // Act
        QueryTransactionResponse result = transactionService.getTransactionRecords(mockRequest);

        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(mockRequest).getUserId();
        Mockito.verify(mockRequest).getTargetDate();
        Mockito.verify(transactionService).queryTransactionByKsql(mockKSql);
        Mockito.verify(transactionService).convertToTransactionRecords(mockRows);
        Mockito.verify(transactionService).getTransactionTotalCreditAndDebit(mockTransactionRecords);
    }

}
