package com.sypuluse.transaction.it;

import com.synpulse.transaction.TransactionManagementApplication;
import com.synpulse.transaction.domain.ExchangeRateApiException;
import com.synpulse.transaction.domain.TransactionService;
import com.synpulse.transaction.persentation.dto.ExchangeRateApiResponse;
import com.synpulse.transaction.persentation.dto.QueryTransactionRequest;
import com.synpulse.transaction.persentation.dto.QueryTransactionResponse;
import com.synpulse.transaction.utils.DateUtils;
import com.sypuluse.transaction.MockRow;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:application-dev.properties")
@SpringBootTest(classes = {TransactionManagementApplication.class})
public class TransactionIntegrationTest {

    @Mock
    private Client client;

    @Mock
    private BatchedQueryResult batchedQueryResult;

    @Autowired
    private TransactionService transactionService;


    @Test
    public void testGetTransactionRecords() throws ParseException, ExecutionException, InterruptedException, ExchangeRateApiException {
        // 模拟查询结果的行数据
        List<Row> mockRows = new ArrayList<>();
        // 添加模拟的行数据，具体根据您的测试需求设置
        mockRows.add(createMockRow("id1", "userId1", new BigDecimal("100"), "USD", "accountIban1", "2023-06-24", "Description1"));
        mockRows.add(createMockRow("id2", "userId2", new BigDecimal("200"), "EUR", "accountIban2", "2023-06-24", "Description2"));

        // 模拟Client的executeQuery方法返回BatchedQueryResult
        when(client.executeQuery(anyString())).thenReturn(batchedQueryResult);
        // 模拟BatchedQueryResult的get方法返回模拟的行数据
        when(batchedQueryResult.get()).thenReturn(mockRows);

        // 创建测试输入数据
        QueryTransactionRequest request = new QueryTransactionRequest();
        request.setUserId("testUserId");
        request.setTargetDate("2023-06-24");
        request.setPageSize(10);

        // 调用被测试的方法
        QueryTransactionResponse response = transactionService.getTransactionRecords(request);

        // 验证返回结果是否符合预期
        assertEquals(2, response.getItems().size());
        // 进行其他断言或验证操作
    }

    // 辅助方法，创建模拟的Row对象
    private Row createMockRow(String id, String userId, BigDecimal amount, String currency, String accountIban, String valueDate, String description) {
        Row mockRow = Mockito.mock(Row.class);
        when(mockRow.getString("id")).thenReturn(id);
        when(mockRow.getString("userId")).thenReturn(userId);
        when(mockRow.getDecimal("amount")).thenReturn(amount);
        when(mockRow.getString("currency")).thenReturn(currency);
        when(mockRow.getString("accountIban")).thenReturn(accountIban);
        when(mockRow.getString("valueDate")).thenReturn(valueDate);
        when(mockRow.getString("description")).thenReturn(description);
        return mockRow;
    }


    @Test
    public void testRetrieveExchangeRateApiByCurrency() throws Exception {
        String date = "2023-06-24";
        String queryTargetCurrency = "TWD";
        double expectedExchangeRate = 33.890867;

        double exchangeRate = transactionService.retrieveExchangeRateApiByCurrency(date, queryTargetCurrency);

        assertEquals(expectedExchangeRate, exchangeRate, 0.001);
    }
}
