package com.sypuluse.transaction.ut;

import com.synpulse.transaction.TransactionManagementApplication;
import com.synpulse.transaction.domain.TransactionService;
import com.synpulse.transaction.domain.model.TransactionRecord;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import com.synpulse.transaction.utils.DateUtils;
import com.sypuluse.transaction.MockRow;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:application-dev.properties")
@SpringBootTest(classes = {TransactionManagementApplication.class})
public class TransactionTest {

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private TransactionService transactionService;

    private final String dateFormat = "yyyy-MM-dd";


    @Test
    public void testConvertToTransactionRecords() throws Exception {
        // Arrange
        TransactionService transactionService = new TransactionService();


        List<Row> rows = List.of(
                createMockRow("1", "user1", BigDecimal.valueOf(100), "USD", "iban1", "2023-06-18", "Description 1"),
                createMockRow("2", "user2", BigDecimal.valueOf(-50), "EUR", "iban2", "2023-06-19", "Description 2")
        );

        DateUtils dateUtilsMock = Mockito.mock(DateUtils.class);
        transactionService.setDateUtils(dateUtilsMock);
        Mockito.when(dateUtilsMock.convertByFormat("2023-06-18", dateFormat)).thenReturn(new Date(1687017600));
        Mockito.when(dateUtilsMock.convertByFormat("2023-06-19", dateFormat)).thenReturn(new Date(1687017600));

        // Act
        List<TransactionRecord> transactionRecords = transactionService.convertToTransactionRecords(rows);

        // Assert
        Assertions.assertEquals(2, transactionRecords.size());

        TransactionRecord record1 = transactionRecords.get(0);
        Assertions.assertEquals("1", record1.getId());
        Assertions.assertEquals("user1", record1.getUserId());
        Assertions.assertEquals(BigDecimal.valueOf(100), record1.getAmount());
        Assertions.assertEquals("USD", record1.getCurrency());
        Assertions.assertEquals("iban1", record1.getAccountIban());
        Assertions.assertEquals(dateUtilsMock.convertByFormat("2023-06-18", dateFormat), record1.getValueDate());
        Assertions.assertEquals("Description 1", record1.getDescription());

        TransactionRecord record2 = transactionRecords.get(1);
        Assertions.assertEquals("2", record2.getId());
        Assertions.assertEquals("user2", record2.getUserId());
        Assertions.assertEquals(BigDecimal.valueOf(-50), record2.getAmount());
        Assertions.assertEquals("EUR", record2.getCurrency());
        Assertions.assertEquals("iban2", record2.getAccountIban());
        Assertions.assertEquals(dateUtilsMock.convertByFormat("2023-06-19", dateFormat), record2.getValueDate());
        Assertions.assertEquals("Description 2", record2.getDescription());
    }

    private Row createMockRow(String id, String userId, BigDecimal amount, String currency, String accountIban,
                              String valueDate, String description) {
        return new MockRow(id, userId, amount, currency, accountIban, valueDate, description);
    }

    @Test
    public void testGetTransactionTotalCreditAndDebit() throws ParseException {
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        DateUtils dateUtilsMock = Mockito.mock(DateUtils.class);

        TransactionService transactionService = new TransactionService();
        transactionService.setRestTemplate(restTemplateMock);
        transactionService.setDateUtils(dateUtilsMock);

        Mockito.when(dateUtilsMock.convertByFormat("2023-06-24", dateFormat)).thenReturn(new Date(1687536000));
        Date mockDate = dateUtilsMock.convertByFormat("2023-06-24", dateFormat);

        List<TransactionRecord> transactionRecords = new ArrayList<>();
        transactionRecords.add(new TransactionRecord("89d3o179-abcd-465b-o9ee-e2d5f6ofEld46", "user1", new BigDecimal(100), "USD", "iban1", mockDate, "Description 1"));
        transactionRecords.add(new TransactionRecord("89d3o179-abcd-465b-o9ee-e2d5f6ofEld44", "user1", new BigDecimal(-50), "USD", "iban2", mockDate, "Description 2"));
        transactionRecords.add(new TransactionRecord("89d3o179-abcd-465b-o9ee-e2d5f6ofEld43", "user1", new BigDecimal(200), "EUR", "iban3", mockDate, "Description 3"));

        QueryTransactionResponse response = transactionService.getTransactionTotalCreditAndDebit(transactionRecords);

        assertEquals(300.0, response.getTotalCredit());
        assertEquals(-50.0, response.getTotalDebit());
    }

    //TODO research how to insert test data.
    @Test
    void testQueryTransactionByKsql() throws ExecutionException, InterruptedException {
//        Row mockRow1 = Mockito.mock(Row.class);
//        Row mockRow2 = Mockito.mock(Row.class);
//        List<Row> expectedRows = Arrays.asList(mockRow1, mockRow2);

        BatchedQueryResult mockQueryResult = Mockito.mock(BatchedQueryResult.class);

        List<Row> expectedRows = Collections.emptyList();
        Mockito.when(mockQueryResult.get()).thenReturn(expectedRows);

        List<Row> resultRows = transactionService.queryTransactionByKsql("SELECT * FROM transaction_table");

        assertEquals(expectedRows, resultRows);
    }

}
