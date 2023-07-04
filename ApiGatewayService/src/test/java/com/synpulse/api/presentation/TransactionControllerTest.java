package com.synpulse.api.presentation;

import com.synpulse.api.presentation.domain.KafkaProducer;
import com.synpulse.api.presentation.dto.QueryTransactionRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducer kafkaProducer;

    private ResultActions perform;

    @Test
    void test_user_id_not_match_pattern() throws Exception {
        String messUpUserId = "1234567";
        when_get(messUpUserId, "2023-07-03");
        then_http_should_be(HttpStatus.BAD_REQUEST);
    }

    @Test
    void test_user_id_ok() throws Exception {
        String messUpUserId = "0-123456789";
        when_get(messUpUserId, "2023-07-03");
        then_http_should_be(HttpStatus.OK);
    }

    @Test
    void test_date_not_match_pattern() throws Exception {
        String messUpUserId = "0-1234567";
        when_get(messUpUserId, "2023/07/03");
        then_http_should_be(HttpStatus.BAD_REQUEST);
    }

    @Test
    void test_date_ok() throws Exception {
        String messUpUserId = "0-123456789";
        when_get(messUpUserId, "2023-07-03");
        then_http_should_be(HttpStatus.OK);
    }

    private void when_get(String userId, String date) throws Exception {
        perform = mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/v1/transaction?userId=%s&date=%s", userId, date))
                        .with(jwt())
        );
    }

    private void then_http_should_be(HttpStatus ok) throws Exception {
        perform.andExpect(status().is(ok.value()));
    }

    //TODO test kafka 400 error(expect exception) and 500 error(unexpect)

    @Test
    @Disabled
    void test_kafka_user_not_found() throws Exception {
        String messUpUserId = "0-123456789";

        doThrow(new ProducerException(""))
                .when(kafkaProducer)
                .sendMessage(any(QueryTransactionRequest.class));

        when_get(messUpUserId, "2023-07-03");
        then_http_should_be(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Disabled
    void test_kafka_error_occurred() throws Exception {
        String userId = "0-123456789";
        String date = "2023-07-03";

        doThrow(new ProducerException(""))
                .when(kafkaProducer)
                .sendMessage(any(QueryTransactionRequest.class));

        when_get(userId, date);
        then_http_should_be(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}