package com.synpulse.api.presentation;

import com.synpulse.api.presentation.domain.KafkaProducer;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    @Disabled
    void test_user_id_not_ok() throws Exception {
        String messUpUserId = "1234567";
        ResultActions perform = when_get(messUpUserId, "2023-07-03");

        perform.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    private ResultActions when_get(String userId, String date) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/v1/transaction?userId=%s&date=%s", userId, date))
                        .with(jwt())
        );
    }

    @Test
    void test_user_id_ok() throws Exception {
        String messUpUserId = "0-123456789";
        ResultActions perform = when_get(messUpUserId, "2023-07-03");
        perform.andExpect(status().is(HttpStatus.OK.value()));
    }

}