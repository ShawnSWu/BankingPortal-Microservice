package com.synpulse.exchangerate.persentation;

import com.synpulse.exchangerate.persentation.persentation.dto.QueryTransactionRequest;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaMessageProducer {

    public static void main(String[] args) {

        // Kafka server
        String bootstrapServers = "kafka:9092";

        // Kafka producer config
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // create Kafka producer
        KafkaProducer<String, QueryTransactionRequest> producer = new KafkaProducer<>(properties);


        String topic = "query-transaction-v1";
        String key = "message-key";
        QueryTransactionRequest value = new QueryTransactionRequest(/* 设置消息的参数 */);

        // create Kafka message
        ProducerRecord<String, QueryTransactionRequest> record = new ProducerRecord<>(topic, key, value);

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