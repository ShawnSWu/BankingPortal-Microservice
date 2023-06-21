package com.synpulse.api;

import com.synpulse.api.presentation.dto.QueryTransactionRequest;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaMessageProducer {

    public static void main(String[] args) {

        // Kafka server
        String bootstrapServers = "localhost:9092";

        // Kafka producer config
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // create Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        String topic = "query_transaction_v1";
        String key = "message-key";

        QueryTransactionRequest value = QueryTransactionRequest.builder()
                .userId("A123")
                .targetDate("2023-06-19")
                .build();

        // create Kafka message
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, "value");

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("message send success!!，offset：" + metadata.offset());
                }
            }
        });

        producer.close();
    }
}