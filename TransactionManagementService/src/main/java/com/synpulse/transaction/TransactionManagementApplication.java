package com.synpulse.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synpulse.transaction.persentation.TransactionConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TransactionManagementApplication {
    private static final Logger logger = LogManager.getLogger(TransactionManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}